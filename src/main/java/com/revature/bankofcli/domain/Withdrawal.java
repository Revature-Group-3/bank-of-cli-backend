package com.revature.bankofcli.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity 
@Table (name = "withdrawal")
@DiscriminatorValue ("withdrawal")
public class Withdrawal extends BankTransaction{

    @ManyToOne // many withdrawls to one
    @JoinColumn (name = "account_id", nullable = false)
    private Account account;

    protected  Withdrawal(){}

    public Withdrawal(Account account, OffsetDateTime timestamp, long amountInCents, String memo ){
        super(timestamp, amountInCents, memo);
        this.account = account;
    }

    public Account getAccount(){
        return account;
    }
    public void setAccount(Account account){
        this.account = account;
    }

}
