package com.revature.bankofcli.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity 
@DiscriminatorValue ("withdrawal")
public class Withdrawal extends BankTransaction {

    @ManyToOne // many withdrawls to one
    @JoinColumn (name = "sender_account_id", nullable = false)
    private Account account;

    protected  Withdrawal(){}

    public Withdrawal(Account account, long amountInCents, String memo ){
        super(amountInCents, memo);
        this.account = account;
    }

    public Account getAccount(){
        return account;
    }
    public void setAccount(Account account){
        this.account = account;
    }

}
