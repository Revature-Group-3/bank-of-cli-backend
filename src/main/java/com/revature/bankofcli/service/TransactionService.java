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
    public SuccessfulDepositResponse deposit(Long accountId, DepositMoneyRequest request) {
        Account account = transactionSetup(accountId, request);

        // Set new balance
        account.setBalanceInCents(account.getBalanceInCents() + request.getAmountInCents());

        // Persist deposit record
        Deposit deposit = bankTransactionRepository.save(new Deposit(
            account,
            request.getAmountInCents(),
            null
        ));

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
    public SuccessfulWithdrawalResponse withdraw(Long accountId, WithdrawMoneyRequest request) {
        Account account = transactionSetup(accountId, request);

        // Check if amount is greater than what you currently have
        // Error code 400
        if (request.getAmountInCents() > account.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }

        // Set new balance
        account.setBalanceInCents(account.getBalanceInCents() - request.getAmountInCents());

        // Persist withdrawal record
        Withdrawal withdrawal = bankTransactionRepository.save(new Withdrawal(
            account,
            request.getAmountInCents(),
            null
        ));

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
    public SuccessfulTransferResponse transfer(Long accountId, TransferMoneyRequest request) {
        validateAmount(request);
        long amountInCents = request.getAmountInCents();

        // Find the recipient's id without loading the account, so it can be locked below
        // Error code 404
        Long recipientId = accountRepository.findIdByEmail(request.getRecipientAccountEmail())
                .orElseThrow(RecipientNotFoundException::new);

        // Error code 400
        if (recipientId.equals(accountId)) {
            throw new SelfTransferException();
        }

        // Lock both accounts in a fixed order (lower id first) so two opposite transfers can't deadlock
        Account first = lockAccount(Math.min(accountId, recipientId));
        Account second = lockAccount(Math.max(accountId, recipientId));
        Account sender = (first.getId() == accountId) ? first : second;
        Account recipient = (sender == first) ? second : first;

        // Check if amount is greater than what sender currently has
        // Error code 400
        if (amountInCents > sender.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }

        // Set new balances
        sender.setBalanceInCents(sender.getBalanceInCents() - amountInCents);
        recipient.setBalanceInCents(recipient.getBalanceInCents() + amountInCents);

        // Persist transfer record
        Transfer transfer = bankTransactionRepository.save(new Transfer(sender, recipient, amountInCents, null));

        // Successful transfer response
        return new SuccessfulTransferResponse(
            transfer.getId(),
            transfer.getAmountInCents(),
            sender.getBalanceInCents(),
            transfer.getTimestamp(),
            sender.getEmail(),
            recipient.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public RetrieveTransactionHistoryResponse getTransactionHistory(Long accountId) {
        // Get logged-in account
        Account loggedInAccount = accountRepository.findById(accountId)
            .orElseThrow(AccountNotFoundException::new);
        
        // Should be allowed to be empty
        List<BankTransaction> pastBankTransactions = bankTransactionRepository.findTransactionHistory(accountId);

        // Map to retrieval DTOs
        return new RetrieveTransactionHistoryResponse(mapBankTransactionsToRetrievalDtos(pastBankTransactions, loggedInAccount));
    }

    /*
     * HELPER METHODS
     */

    // Loads and row-locks the account, then checks the rules shared by every transaction type
    private Account transactionSetup(Long accountId, ConductTransactionRequest request) {
        validateAmount(request);
        return lockAccount(accountId);
    }

    // Rules shared by every transaction type
    private void validateAmount(ConductTransactionRequest request) {
        // Error code 400
        if (request.getAmountInCents() <= 0) {
            throw new InvalidAmountException();
        }
        // Error code 400
        if (request.getAmountInCents() > 1_000_000) {
            throw new AmountExceedsLimitException();
        }
    }

    // Loads the account with a row lock (SELECT ... FOR UPDATE) that is held until the transaction ends
    private Account lockAccount(Long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(AccountNotFoundException::new);
    }

    // Maps bank transaction list to bank transaction retrieval dto list
    private List<RetrieveTransactionResponse> mapBankTransactionsToRetrievalDtos(List<BankTransaction> bankTransactions, Account loggedInAccount) {
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
                    { return mapTransferToRetrievalDto(transfer, loggedInAccount); }
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

    // Maps transfer to transfer retrieval DTO
    private RetrieveTransferResponse mapTransferToRetrievalDto(Transfer transfer, Account loggedInAccount) {
        return new RetrieveTransferResponse(
            transfer.getId(),
            transfer.getAmountInCents(),
            transfer.getTimestamp(),
            transfer.getSenderAccount().getEmail(),
            transfer.getRecipientAccount().getEmail(),
            loggedInAccount.getEmail()
        );
    }
}
