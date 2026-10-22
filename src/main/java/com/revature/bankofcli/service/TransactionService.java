package com.revature.bankofcli.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.revature.bankofcli.dto.transaction.ConductTransactionRequest;
import com.revature.bankofcli.dto.transaction.DepositMoneyRequest;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionHistoryResponse;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionResponse;
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

        long amountInCents = request.getAmountInCents();

        // Update Account's Balance
        account.setBalanceInCents(account.getBalanceInCents() + amountInCents);

        // Save and return
        return bankTransactionRepository.save(new Deposit(account, amountInCents, null));
    }

    // Withdraw
    @Transactional
    public SuccessfulWithdrawalResponse withdraw(Long accountId, WithdrawMoneyRequest request) {

        Account account = transactionSetup(accountId, request);

        long amountInCents = request.getAmountInCents();

        // Check if amount is greater than what you currently have.
        // error code 400
        if (amountInCents > account.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }

        // Set new balance
        account.setBalanceInCents(account.getBalanceInCents() - amountInCents);

        // persist to transactions
        return bankTransactionRepository.save(new Withdrawal(account, amountInCents, null));

    }

    @Transactional
    public SuccessfulTransferResponse transfer(Long accountId, TransferMoneyRequest request) {
        Account sender = transactionSetup(accountId, request);

        long amountInCents = request.getAmountInCents();

        if (sender.getEmail().equalsIgnoreCase(request.getRecipientAccountEmail())) {
            throw new SelfTransferException();
        }

        // Error Code 404
        Account recipient = accountRepository.findByEmail(request.getRecipientAccountEmail())
                .orElseThrow(RecipientNotFoundException::new);

        // Business logic / checks
        // Additional Checks
        // error code 400
        if (amountInCents > sender.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }

        // Set new balance
        sender.setBalanceInCents(sender.getBalanceInCents() - amountInCents);
        recipient.setBalanceInCents(recipient.getBalanceInCents() + amountInCents);

        // Persist to database
        return bankTransactionRepository.save(new Transfer(sender, recipient, amountInCents, null));

    }

    @Transactional(readOnly = true)
    public RetrieveTransactionHistoryResponse getTransactionHistory(long accountId) {
        // Should be allowed to be empty
        List<BankTransaction> pastBankTransactions = bankTransactionRepository.findTransactionHistory(accountId);

        return pastBankTransactions.stream()
            .map(bankTransaction -> RetrieveTransactionResponse);
    }

    /*
     * HELPER METHODS
    */
    
    // Loads and row-locks the specified account and checks the rules shared by every transaction type
    private Account transactionSetup(long accountId, ConductTransactionRequest request) {
        // Does the basic setup validation common to all types of transactions

        // Load account, or fail if it doesn't exist
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(AccountNotFoundException::new);

        // Business rules
        // Error code 400
        if (request.getAmountInCents() <= 0) {
            throw new InvalidAmountException();
        }

        // Error code 400
        if (request.getAmountInCents() > 1_000_000) {
            throw new AmountExceedsLimitException();
        }

        return account;
    }
}
