package com.revature.bankofcli.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity 
@Table (name = "deposit")
@DiscriminatorValue("deposit") // JPA writes type "deposit" to bank_transaction table
public class Deposit extends BankTransaction{ // Deposit "is a" bank transaction

    // many deposits to one account
    @ManyToOne
    @JoinColumn (name = "account_id", nullable = false) // Business rule. FK column in the deposit table 
    private Account account;    // The field annotations above describe

    protected Deposit(){}       // JPA needs an empty constructor


    // It lets you go from the transaction to the account: deposit.getAccount().getBalanceInCents().
    // You load the deposit, and its account comes with it.
    // The order of arguments has to match the parent constructor: BankTransaction
    public Deposit(Account account,  OffsetDateTime timestamp, long amountInCents, String memo){
        super(timestamp, amountInCents, memo); // Has to be first in statement in order to work.
        this.account = account;
    }
    
    public Account getAccount(){
        return account;
    }

    public void setAccount(Account account){
        this.account = account;
    }
}
