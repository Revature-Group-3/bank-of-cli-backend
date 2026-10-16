package com.revature.bankofcli.service;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.bankofcli.domain.Account;
import com.revature.bankofcli.domain.Deposit;
import com.revature.bankofcli.domain.Transfer;
import com.revature.bankofcli.domain.Withdrawal;
import com.revature.bankofcli.repository.AccountRepository;
import com.revature.bankofcli.repository.BankTransactionRepository;

import com.revature.bankofcli.exception.AccountNotFoundException;

import java.math.BigInteger;


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
     * */ 
    private final  BankTransactionRepository bankTransactionRepository;
    private final  AccountRepository accountRepository;

    public TransactionService(BankTransactionRepository bankTransactionRepository, AccountRepository accountRepository){
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
     * or none do. If step 3 fails, balance change from step 1 is rolled back.
     * 
     * Similar to part 1 of BANKOFCLI, its just Spring does it for you now.
     */
    @Transactional 
    public Deposit deposit(long accountEmail, long amountInCents, String memo){

        // 1. Load account, or fail if it doesnt exist
        Account account = accountRepository.findById(accountEmail)
                            .orElseThrow(() -> new AccountNotFoundException("Account not found."));

        // 2.Business rules
        if (amountInCents <= 0){
            throw new IllegalArgumentException("The transaction amount must be greater than 0.");
        }
        if (amountInCents > 1_000_000){ //$10,000 in cents
            throw new IllegalArgumentException("The deposit amount exceeds the maximum allowed limit of $10,000.");
        }
        // 3.Update Account's Balance
        account.setBalanceInCents(account.getBalanceInCents() + amountInCents);

        // 4.Create a transaction (Deposit)
        Deposit deposit = new Deposit (account, OffsetDateTime.now(), amountInCents, memo);

        // 5.Save and return
        return bankTransactionRepository.save(deposit);
    }


    // Withdraw
    @Transactional 
    public Withdrawal withdraw(String accountEmail, long amountInCents, String memo){

        // TODO
        // setup data
        Account account = accountRepository.findByEmail(accountEmail)
                            .orElseThrow(() -> new AccountNotFoundException("Account could not be found. No Withdrawals available at this time."));

        // logic/business rules / check
        if (amountInCents <= 0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (amountInCents > 1_000_000){
            throw new IllegalArgumentException("Amount exceeds $10,000.");
        }
        // Check if amount is greater than what you currently have.
        if (amountInCents > account.getBalanceInCents() ){
            throw new IllegalArgumentException("Amount is greater than current balance.");

        }

        // Set new balance
        account.setBalanceInCents(account.getBalanceInCents() - amountInCents);

        // Create withdrawl
        //  Syntax: (Account account, OffsetDateTime timestamp, long amountInCents, String memo )
        Withdrawal withdraw = new Withdrawal(account, OffsetDateTime.now(), amountInCents, memo);


        // persist to transactions
        return bankTransactionRepository.save(withdraw);

    }


    // Transfer Transfer(Account senderAccount, Account recipientAccount,  OffsetDateTime timestamp, long amountInCents, String memo)
    @Transactional 
    public Transfer transfer(String accountSenderEmail, String accountRecipientEmail, long amountInCents, String memo ){

        // Base Check
        if (accountSenderEmail.equals(accountRecipientEmail)){
            throw new IllegalArgumentException("Cant transfer funds to yourself.");
        }

        // Gather accounts : from and to
        Account accountSender = accountRepository.findByEmail(accountSenderEmail)
                                .orElseThrow(() -> new AccountNotFoundException("Could not find sender account."));
        Account accountRecipient = accountRepository.findByEmail(accountRecipientEmail)
                                    .orElseThrow(()-> new AccountNotFoundException("Could not find recipient account."));


        // Business logic / checks

        // Additional Checks
        if (amountInCents <= 0){
            throw new IllegalArgumentException("Amount cannot be less than 0.");
        }
        if (amountInCents > 1_000_000){
            throw new IllegalArgumentException("Amount is greater than $10,000.");
        }
        if (amountInCents > accountSender.getBalanceInCents() ){
            throw new IllegalArgumentException("Amount is greater than your current balance.");
        }
        // Set new balance
        accountSender.setBalanceInCents(accountSender.getBalanceInCents() -  amountInCents);
        accountRecipient.setBalanceInCents(accountRecipient.getBalanceInCents() +  amountInCents);

        // Create transfer
        Transfer transfer = new Transfer(accountSender, accountRecipient, OffsetDateTime.now(), amountInCents, memo);


        // Persist to database    
        return bankTransactionRepository.save(transfer);

    }


}
