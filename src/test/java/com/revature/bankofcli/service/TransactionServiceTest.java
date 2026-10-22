package com.revature.bankofcli.service;

import com.revature.bankofcli.domain.Account;
import com.revature.bankofcli.domain.BankTransaction;
import com.revature.bankofcli.domain.Deposit;
import com.revature.bankofcli.domain.Transfer;
import com.revature.bankofcli.domain.Withdrawal;
import com.revature.bankofcli.dto.transaction.DepositMoneyRequest;
import com.revature.bankofcli.dto.transaction.TransferMoneyRequest;
import com.revature.bankofcli.dto.transaction.WithdrawMoneyRequest;
import com.revature.bankofcli.exception.AccountNotFoundException;
import com.revature.bankofcli.exception.AmountExceedsLimitException;
import com.revature.bankofcli.exception.InsufficientFundsException;
import com.revature.bankofcli.exception.InvalidAmountException;
import com.revature.bankofcli.exception.RecipientNotFoundException;
import com.revature.bankofcli.exception.SelfTransferException;
import com.revature.bankofcli.repository.AccountRepository;
import com.revature.bankofcli.repository.BankTransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

  import com.revature.bankofcli.dto.transaction.SuccessfulDepositResponse;
  import com.revature.bankofcli.dto.transaction.SuccessfulWithdrawalResponse;
  import com.revature.bankofcli.dto.transaction.SuccessfulTransferResponse;

// Unit tests for TransactionService. The repositories are mocks, so no database is needed
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BankTransactionRepository bankTransactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    // helpers 

    private Account account(long id, String email, long balanceInCents) {
        Account account = new Account(email, "First", "Last", "hashedPassword");
        account.setId(id);
        account.setBalanceInCents(balanceInCents);
        return account;
    }

    private DepositMoneyRequest depositRequest(long amountInCents) {
        DepositMoneyRequest request = new DepositMoneyRequest();
        request.setAmountInCents(amountInCents);
        return request;
    }

    private WithdrawMoneyRequest withdrawRequest(long amountInCents) {
        WithdrawMoneyRequest request = new WithdrawMoneyRequest();
        request.setAmountInCents(amountInCents);
        return request;
    }

    private TransferMoneyRequest transferRequest(String recipientEmail, long amountInCents) {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setRecipientAccountEmail(recipientEmail);
        request.setAmountInCents(amountInCents);
        return request;
    }

    // save() returns whatever it was given, like the real repository does
    private void saveReturnsItsArgument() {
        when(bankTransactionRepository.save(any(BankTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // deposit 

    @Test
    void depositAddsToBalanceAndSavesDeposit() {
        Account hal = account(1L, "hal@email.com", 1000);
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hal));
        saveReturnsItsArgument();

        SuccessfulDepositResponse deposit = transactionService.deposit(1L, depositRequest(500));

        assertEquals(1500, hal.getBalanceInCents());
        assertEquals(500, deposit.getAmountInCents());
        assertEquals("hal@email.com", deposit.getAccountEmail());
        verify(accountRepository).findByIdForUpdate(1L); // the locking lookup is used
    }

    @Test
    void depositRejectsZeroAmountBeforeTouchingTheDatabase() {
        assertThrows(InvalidAmountException.class,
                () -> transactionService.deposit(1L, depositRequest(0)));

        verifyNoInteractions(accountRepository, bankTransactionRepository);
    }

    @Test
    void depositRejectsAmountOverLimit() {
        assertThrows(AmountExceedsLimitException.class,
                () -> transactionService.deposit(1L, depositRequest(1_000_001)));

        verifyNoInteractions(accountRepository, bankTransactionRepository);
    }

    @Test
    void depositAllowsExactlyTheLimit() {
        Account hal = account(1L, "hal@email.com", 0);
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hal));
        saveReturnsItsArgument();

        transactionService.deposit(1L, depositRequest(1_000_000));

        assertEquals(1_000_000, hal.getBalanceInCents());
    }

    @Test
    void depositFailsWhenAccountDoesNotExist() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.deposit(1L, depositRequest(500)));

        verify(bankTransactionRepository, never()).save(any(BankTransaction.class));
    }

    // withdraw 

    @Test
    void withdrawSubtractsFromBalanceAndSavesWithdrawal() {
        Account hal = account(1L, "hal@email.com", 1000);
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hal));
        saveReturnsItsArgument();

        SuccessfulWithdrawalResponse withdrawal = transactionService.withdraw(1L, withdrawRequest(400));

        assertEquals(600, hal.getBalanceInCents());
        assertEquals(400, withdrawal.getAmountInCents());
        assertEquals("hal@email.com", withdrawal.getAccountEmail());
    }

    @Test
    void withdrawAllowsTakingTheWholeBalance() {
        Account hal = account(1L, "hal@email.com", 1000);
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hal));
        saveReturnsItsArgument();

        transactionService.withdraw(1L, withdrawRequest(1000));

        assertEquals(0, hal.getBalanceInCents());
    }

    @Test
    void withdrawRejectsInsufficientFundsAndLeavesBalanceUnchanged() {
        Account hal = account(1L, "hal@email.com", 1000);
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hal));

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.withdraw(1L, withdrawRequest(5000)));

        assertEquals(1000, hal.getBalanceInCents());
        verify(bankTransactionRepository, never()).save(any(BankTransaction.class));
    }

    @Test
    void withdrawRejectsNegativeAmount() {
        assertThrows(InvalidAmountException.class,
                () -> transactionService.withdraw(1L, withdrawRequest(-100)));

        verifyNoInteractions(accountRepository, bankTransactionRepository);
    }

    // transfer 

    @Test
    void transferMovesMoneyBetweenAccounts() {
        Account hal = account(1L, "hal@email.com", 1000);
        Account jo = account(2L, "jo@email.com", 0);
        when(accountRepository.findIdByEmail("jo@email.com")).thenReturn(Optional.of(2L));
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hal));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(jo));
        saveReturnsItsArgument();

        SuccessfulTransferResponse transfer = transactionService.transfer(1L, transferRequest("jo@email.com", 400));

        assertEquals(600, hal.getBalanceInCents());
        assertEquals(400, jo.getBalanceInCents());
        assertEquals("hal@email.com", transfer.getSenderAccountEmail());
        assertEquals("jo@email.com", transfer.getRecipientAccountEmail());
    }

    @Test
    void transferLocksLowerIdFirstEvenWhenSenderHasHigherId() {
        // Sender has id 5, recipient has id 2: the recipient must be locked first
        Account sender = account(5L, "sender@email.com", 1000);
        Account recipient = account(2L, "recipient@email.com", 0);
        when(accountRepository.findIdByEmail("recipient@email.com")).thenReturn(Optional.of(2L));
        when(accountRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(recipient));
        saveReturnsItsArgument();

        SuccessfulTransferResponse transfer = transactionService.transfer(5L, transferRequest("recipient@email.com", 300));

        InOrder lockOrder = inOrder(accountRepository);
        lockOrder.verify(accountRepository).findByIdForUpdate(2L);
        lockOrder.verify(accountRepository).findByIdForUpdate(5L);

        // and the money still goes the right way
        assertEquals(700, sender.getBalanceInCents());
        assertEquals(300, recipient.getBalanceInCents());
        assertEquals("sender@email.com", transfer.getSenderAccountEmail());
    }

    @Test
    void transferRejectsSendingToYourself() {
        // The database matches emails case-insensitively, so a different-case email
        // still resolves to the sender's own id
        when(accountRepository.findIdByEmail("HAL@email.com")).thenReturn(Optional.of(1L));

        assertThrows(SelfTransferException.class,
                () -> transactionService.transfer(1L, transferRequest("HAL@email.com", 400)));

        verify(accountRepository, never()).findByIdForUpdate(any());
        verify(bankTransactionRepository, never()).save(any(BankTransaction.class));
    }

    @Test
    void transferRejectsMissingRecipient() {
        when(accountRepository.findIdByEmail("nobody@email.com")).thenReturn(Optional.empty());

        assertThrows(RecipientNotFoundException.class,
                () -> transactionService.transfer(1L, transferRequest("nobody@email.com", 400)));

        verify(accountRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void transferRejectsInsufficientFundsAndLeavesBothBalancesUnchanged() {
        Account hal = account(1L, "hal@email.com", 1000);
        Account jo = account(2L, "jo@email.com", 50);
        when(accountRepository.findIdByEmail("jo@email.com")).thenReturn(Optional.of(2L));
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(hal));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(jo));

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.transfer(1L, transferRequest("jo@email.com", 5000)));

        assertEquals(1000, hal.getBalanceInCents());
        assertEquals(50, jo.getBalanceInCents());
        verify(bankTransactionRepository, never()).save(any(BankTransaction.class));
    }

    @Test
    void transferRejectsAmountOverLimitBeforeAnyLookup() {
        assertThrows(AmountExceedsLimitException.class,
                () -> transactionService.transfer(1L, transferRequest("jo@email.com", 2_000_000)));

        verifyNoInteractions(accountRepository, bankTransactionRepository);
    }
}