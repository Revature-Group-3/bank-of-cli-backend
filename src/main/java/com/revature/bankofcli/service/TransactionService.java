package com.revature.bankofcli.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.revature.bankofcli.dto.transaction.DepositRequest;
import com.revature.bankofcli.dto.transaction.WithdrawRequest;
import com.revature.bankofcli.dto.transaction.TransferRequest;
import com.revature.bankofcli.dto.transaction.AbstractTransactionRequest;

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
import com.revature.bankofcli.exception.SelfTransferException;

@Service
public class TransactionService {

    /**
     * Transaction uses the repository, no need fo "implements".
     * 
     * 
     * Wouldn't call the BankTransaction because its an abstract class.
     * Wouldn't call each type either.
     * 
     * What you need is to inject repositories.
     * 
     */
    private final BankTransactionRepository bankTransactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(BankTransactionRepository bankTransactionRepository,
            AccountRepository accountRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
        this.accountRepository = accountRepository;
    }

    // Deposit
    /**
     * Need the @Transactional. A deposit is several database writes
     * 1. Update the account's balance
     * 2. Insert a row into bank_transaction
     * 3. Insert a row into deposit
     * 
     * @Transactional groups them into one unit: either all of them happen
     *                or none do. If step 3 fails, balance change from step 1 is
     *                rolled back.
     * 
     *                Similar to part 1 of BANKOFCLI, its just Spring does it for
     *                you now.
     */
    @Transactional
    public Deposit deposit(DepositRequest request) {
        Account account = transactionSetup(request);

        long amountInCents = request.getAmountInCents();
        String memo = request.getMemo();

        // Update Account's Balance
        account.setBalanceInCents(account.getBalanceInCents() + amountInCents);

        // Create a transaction (Deposit)
        Deposit deposit = new Deposit(account, amountInCents, memo);

        // Save and return
        return bankTransactionRepository.save(deposit);
    }

    // Withdraw
    @Transactional
    public Withdrawal withdraw(WithdrawRequest request) {

        Account account = transactionSetup(request);

        long amountInCents = request.getAmountInCents();
        String memo = request.getMemo();

        // Check if amount is greater than what you currently have.
        // error code 400
        if (amountInCents > account.getBalanceInCents()) {
            throw new InsufficientFundsException(
                    "Insufficient funds. The withdrawal amount exceeds your available balance.");

        }

        // Set new balance
        account.setBalanceInCents(account.getBalanceInCents() - amountInCents);

        // Create withdrawal
        // Syntax: (Account account, OffsetDateTime timestamp, long amountInCents,
        // String memo )
        Withdrawal withdraw = new Withdrawal(account, amountInCents, memo);

        // persist to transactions
        return bankTransactionRepository.save(withdraw);

    }

    // Transfer Transfer(Account senderAccount, Account recipientAccount,
    // OffsetDateTime timestamp, long amountInCents, String memo)
    @Transactional
    public Transfer transfer(TransferRequest request) {

        transactionSetup(request);

        long amountInCents = request.getAmountInCents();
        String memo = request.getMemo();

        // Base Check
        // error code 400
        if (request.getAccountId().equals(request.getRecipientAccountId())) {
            throw new SelfTransferException();
        }

        // Gather accounts : from and to
        Account accountSender = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Could not find sender account."));
        // Error Code 404
        Account accountRecipient = accountRepository.findById(request.getRecipientAccountId())
                .orElseThrow(() -> new AccountNotFoundException("The recipient account could not be found."));

        // Business logic / checks

        // Additional Checks
        // error code 400
        if (amountInCents <= 0) {
            throw new InvalidAmountException();
        }
        // error code 400
        if (amountInCents > 1_000_000) {
            throw new AmountExceedsLimitException("The transfer amount exceeds the maximum allowed limit of $10,000.");
        }
        // error code 400
        if (amountInCents > accountSender.getBalanceInCents()) {
            throw new InsufficientFundsException(
                    "Insufficient funds. The transfer amount exceeds your available balance.");
        }
        // Set new balance
        accountSender.setBalanceInCents(accountSender.getBalanceInCents() - amountInCents);
        accountRecipient.setBalanceInCents(accountRecipient.getBalanceInCents() + amountInCents);

        // Create transfer
        Transfer transfer = new Transfer(accountSender, accountRecipient, amountInCents, memo);

        // Persist to database
        return bankTransactionRepository.save(transfer);

    }

    @Transactional(readOnly = true)
    public List<BankTransaction> getTransactionHistory(Long accountId) {
        // Should be allowed to be null
        return bankTransactionRepository.findTransactionHistory(accountId);
    }

    // Helper functions
    private Account transactionSetup(AbstractTransactionRequest transactionRequest) {
        // Does the basic setup validation common to all types of transactions

        // Load account, or fail if it doesn't exist
        Account account = accountRepository.findById(transactionRequest.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Could not find account."));

        // Business rules
        // Error code 400
        if (transactionRequest.getAmountInCents() <= 0) {
            throw new InvalidAmountException();
        }
        // Error code 400
        if (transactionRequest.getAmountInCents() > 1_000_000) {
            throw new AmountExceedsLimitException("The amount exceeds the maximum allowed limit of $10,000.");
        }

        return account;
    }

}
