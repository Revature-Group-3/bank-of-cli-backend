package com.revature.bankofcli.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.revature.bankofcli.dto.transaction.ConductTransactionRequest;
import com.revature.bankofcli.dto.transaction.DepositMoneyRequest;
import com.revature.bankofcli.dto.transaction.RetrieveDepositResponse;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionHistoryResponse;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionResponse;
import com.revature.bankofcli.dto.transaction.RetrieveTransferResponse;
import com.revature.bankofcli.dto.transaction.RetrieveWithdrawalResponse;
import com.revature.bankofcli.dto.transaction.SuccessfulDepositResponse;
import com.revature.bankofcli.dto.transaction.SuccessfulTransferResponse;
import com.revature.bankofcli.dto.transaction.SuccessfulWithdrawalResponse;
import com.revature.bankofcli.dto.transaction.TransferMoneyRequest;
import com.revature.bankofcli.dto.transaction.WithdrawMoneyRequest;

import java.util.List;

import com.revature.bankofcli.domain.Account;
import com.revature.bankofcli.domain.BankTransaction;
import com.revature.bankofcli.domain.Deposit;
import com.revature.bankofcli.domain.Transfer;
import com.revature.bankofcli.domain.Withdrawal;

import com.revature.bankofcli.repository.AccountRepository;
import com.revature.bankofcli.repository.BankTransactionRepository;

import com.revature.bankofcli.exception.AccountNotFoundException;
import com.revature.bankofcli.exception.AmountExceedsLimitException;
import com.revature.bankofcli.exception.InsufficientFundsException;
import com.revature.bankofcli.exception.InvalidAmountException;
import com.revature.bankofcli.exception.RecipientNotFoundException;
import com.revature.bankofcli.exception.SelfTransferException;

@Service
public class TransactionService {
    // Repositories
    private final BankTransactionRepository bankTransactionRepository;
    private final AccountRepository accountRepository;

    // Dependency injection for repositories
    public TransactionService(
        BankTransactionRepository bankTransactionRepository,
        AccountRepository accountRepository
    ) {
        this.bankTransactionRepository = bankTransactionRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Deposit
     * 
     * @Transactional groups the balance update and the new bank_transaction row into one unit:
     * either both happen or neither does.
     */
    @Transactional
    public SuccessfulDepositResponse deposit(long accountId, DepositMoneyRequest request) {
        Account account = standardSetup(accountId, request);

        // Persist deposit record
        Deposit deposit = bankTransactionRepository.save(new Deposit(
            account,
            request.getAmountInCents(),
            null
        ));

        // Set new balance
        account.setBalanceInCents(account.getBalanceInCents() + request.getAmountInCents());

        // Successful deposit response
        return new SuccessfulDepositResponse(
            deposit.getId(),
            deposit.getAmountInCents(),
            account.getBalanceInCents(),
            deposit.getTimestamp(),
            account.getEmail()
        );
    }

    // Withdraw
    @Transactional
    public SuccessfulWithdrawalResponse withdraw(long accountId, WithdrawMoneyRequest request) {
        Account account = standardSetup(accountId, request);

        // Check if amount is greater than what you currently have
        validateSufficientFunds(account, request);

        // Persist withdrawal record
        Withdrawal withdrawal = bankTransactionRepository.save(new Withdrawal(
            account,
            request.getAmountInCents(),
            null
        ));

        // Set new balance
        account.setBalanceInCents(account.getBalanceInCents() - request.getAmountInCents());

        // Successful withdrawal response
        return new SuccessfulWithdrawalResponse(
            withdrawal.getId(),
            withdrawal.getAmountInCents(),
            account.getBalanceInCents(),
            withdrawal.getTimestamp(),
            account.getEmail()
        );
    }

    // Transfer
    @Transactional
    public SuccessfulTransferResponse transfer(long accountId, TransferMoneyRequest request) {
        List<Account> accounts = transferSetup(accountId, request);
        Account senderAccount = accounts.get(0);
        Account recipientAccount = accounts.get(1);

        // Check if amount is greater than what sender currently has
        validateSufficientFunds(senderAccount, request);

        // Persist transfer record
        Transfer transfer = bankTransactionRepository.save(new Transfer(
            senderAccount,
            recipientAccount,
            request.getAmountInCents(),
            null
        ));

        // Set new balances
        senderAccount.setBalanceInCents(senderAccount.getBalanceInCents() - request.getAmountInCents());
        recipientAccount.setBalanceInCents(recipientAccount.getBalanceInCents() + request.getAmountInCents());

        // Successful transfer response
        return new SuccessfulTransferResponse(
            transfer.getId(),
            transfer.getAmountInCents(),
            senderAccount.getBalanceInCents(),
            transfer.getTimestamp(),
            transfer.getSenderAccount().getEmail(),
            transfer.getRecipientAccount().getEmail()
        );
    }

    @Transactional(readOnly = true)
    public RetrieveTransactionHistoryResponse getTransactionHistory(long accountId) {
        // Should be allowed to be empty
        List<BankTransaction> pastBankTransactions = bankTransactionRepository.findTransactionHistory(accountId);

        // Map to retrieval DTOs
        return new RetrieveTransactionHistoryResponse(mapBankTransactionsToRetrievalDtos(pastBankTransactions));
    }

    /*
     * HELPER METHODS
    */

    // Standard bank transaction setup:
    // Validate general bank transaction rules and get account
    private Account standardSetup(long accountId, ConductTransactionRequest request) {
        validateBankTransactionRequest(request);
        return getAccountForBankTransaction(accountId);
    }

    // Transfer setup:
    // Validate general bank transaction rules and get accounts while avoiding double-locking
    // Returns list of two elements: first is sender account and second is recipient account
    private List<Account> transferSetup(long senderAccountId, TransferMoneyRequest request) {
        validateBankTransactionRequest(request);

        // Non-locked sender account
        // This is only used to retrieve sender account information for set-up
        // We will retrieve this again in a locked form to start the transfer
        Account senderAccountNonLocked = accountRepository.findById(senderAccountId)
            .orElseThrow(AccountNotFoundException::new);

        // Verify that sender and recipient emails are different 
        if (senderAccountNonLocked.getEmail().equalsIgnoreCase(request.getRecipientAccountEmail())) {
            throw new SelfTransferException();
        }

        // Non-locked recipient account
        // This is only used to retrieve recipient account information for set-up
        // We will retrieve this again in a locked form to start the transfer
        // We throw error if recipient does not exist
        Account recipientAccountNonLocked = accountRepository.findByEmail(request.getRecipientAccountEmail())
            .orElseThrow(RecipientNotFoundException::new);

        // Get locked accounts in ascending order of ID:

        // Will refer to locked sender and recipient
        Account senderAccount;
        Account recipientAccount;

        // Lock sender first if ID is smaller
        if (senderAccountNonLocked.getId() < recipientAccountNonLocked.getId()) {
            senderAccount = getAccountForBankTransaction(senderAccountNonLocked.getId());
            recipientAccount = getAccountForBankTransaction(recipientAccountNonLocked.getId());
        }

        // Lock recipient first if ID is smaller
        else {
            recipientAccount = getAccountForBankTransaction(recipientAccountNonLocked.getId());
            senderAccount = getAccountForBankTransaction(senderAccountNonLocked.getId());
        }

        // Return accounts in list
        return List.of(senderAccount, recipientAccount);
    }
    
    // Validate bank transaction request; this checks general business rules applying to all bank transaction types 
    private void validateBankTransactionRequest(ConductTransactionRequest request) {
        // Error code 400
        if (request.getAmountInCents() <= 0) {
            throw new InvalidAmountException();
        }

        // Error code 400
        if (request.getAmountInCents() > 1_000_000) {
            throw new AmountExceedsLimitException();
        }
    }

    // Validate that account has sufficient funds for withdrawal
    private void validateSufficientFunds(Account account, WithdrawMoneyRequest request) {
        // Error code 400
        if (request.getAmountInCents() > account.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }
    }

    // Validate that sender account has sufficient funds for transfer
    private void validateSufficientFunds(Account senderAccount, TransferMoneyRequest request) {
        // Error code 400
        if (request.getAmountInCents() > senderAccount.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }
    }
    
    // Loads and row-locks the specified account for bank transaction
    // If account not found, we throw exception
    private Account getAccountForBankTransaction(long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
            .orElseThrow(AccountNotFoundException::new);
    }

    // Maps bank transaction list to bank transaction retrieval dto list
    private List<RetrieveTransactionResponse> mapBankTransactionsToRetrievalDtos(List<BankTransaction> bankTransactions) {
        return bankTransactions.stream()
            // Map each bank transaction type to its corresponding retrieval DTO:
            .map(bankTransaction -> {
                // Deposit
                if (bankTransaction instanceof Deposit deposit)
                    { return mapDepositToRetrievalDto(deposit); }
                // Withdrawal
                else if (bankTransaction instanceof Withdrawal withdrawal)
                    { return mapWithdrawalToRetrievalDto(withdrawal); }
                // Transfer
                else if (bankTransaction instanceof Transfer transfer)
                    { return mapTransferToRetrievalDto(transfer); }
                // Default (we should not reach this point)
                else
                    { throw new IllegalArgumentException("Unknown bank transaction type: " + bankTransaction.getClass().getName()); }
            })
            .toList();
    }

    // Maps deposit to deposit retrieval DTO
    private RetrieveDepositResponse mapDepositToRetrievalDto(Deposit deposit) {
        return new RetrieveDepositResponse(
            deposit.getId(),
            deposit.getAmountInCents(),
            deposit.getTimestamp(),
            deposit.getAccount().getEmail()
        );
    }

    // Maps withdrawal to withdrawal retrieval DTO
    private RetrieveWithdrawalResponse mapWithdrawalToRetrievalDto(Withdrawal withdrawal) {
        return new RetrieveWithdrawalResponse(
            withdrawal.getId(),
            withdrawal.getAmountInCents(),
            withdrawal.getTimestamp(),
            withdrawal.getAccount().getEmail()
        );
    }

    // Maps transfer to to transfer retrieval DTO
    private RetrieveTransferResponse mapTransferToRetrievalDto(Transfer transfer) {
        return new RetrieveTransferResponse(
            transfer.getId(),
            transfer.getAmountInCents(),
            transfer.getTimestamp(),
            transfer.getSenderAccount().getEmail(),
            transfer.getRecipientAccount().getEmail()
        );
    }
}
