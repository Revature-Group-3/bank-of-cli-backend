package com.revature.bankofcli.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.revature.bankofcli.dto.transaction.ConductTransactionRequest;
import com.revature.bankofcli.dto.transaction.DepositMoneyRequest;
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
    public Deposit deposit(Long accountId, DepositMoneyRequest request) {
        Account account = transactionSetup(accountId, request);

        long amountInCents = request.getAmountInCents();

        // Update Account's Balance
        account.setBalanceInCents(account.getBalanceInCents() + amountInCents);

        // Save and return
        return bankTransactionRepository.save(new Deposit(account, amountInCents, null));
    }

    // Withdraw
    @Transactional
    public Withdrawal withdraw(Long accountId, WithdrawMoneyRequest request) {

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
    public Transfer transfer(Long accountId, TransferMoneyRequest request) {
        validateAmount(request);
        long amountInCents = request.getAmountInCents();

        // Find the recipient's id without loading the account, so it can be locked below
        Long recipientId = accountRepository.findIdByEmail(request.getRecipientAccountEmail())
                .orElseThrow(RecipientNotFoundException::new);

        if (recipientId.equals(accountId)) {
            throw new SelfTransferException();
        }

        // Lock both accounts in a fixed order (lower id first) so two opposite transfers can't deadlock
        Account first = lockAccount(Math.min(accountId, recipientId));
        Account second = lockAccount(Math.max(accountId, recipientId));
        Account sender = (first.getId() == accountId) ? first : second;
        Account recipient = (sender == first) ? second : first;


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
    public List<BankTransaction> getTransactionHistory(Long accountId) {
        // Should be allowed to be null
        return bankTransactionRepository.findTransactionHistory(accountId);
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
        if (request.getAmountInCents() <= 0) {
            throw new InvalidAmountException();
        }
        if (request.getAmountInCents() > 1_000_000) {
            throw new AmountExceedsLimitException();
        }
    }

    // Loads the account with a row lock (SELECT ... FOR UPDATE) that is held until the transaction ends
    private Account lockAccount(Long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(AccountNotFoundException::new);
    }
}
