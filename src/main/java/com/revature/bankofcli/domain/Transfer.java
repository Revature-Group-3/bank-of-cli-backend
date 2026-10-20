package com.revature.bankofcli.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@DiscriminatorValue ("transfer")
public class Transfer extends BankTransaction{

    /**
     * JPA turns that id into a full Account object when it loads the row.
     * That’s what an ORM does: ids in the database, objects in Java.
     */


    @ManyToOne 
    @JoinColumn (name = "sender_account_id", nullable = false)
    private Account senderAccount;    // The account this type of transaction refers to

    @ManyToOne 
    @JoinColumn(name = "recipient_account_id" , nullable = false) 
    private Account recipientAccount;

    protected Transfer (){}

    /**
     *  Why not a primitive?: right. With just a long accountId,
     *  you’d have to look up the account yourself every time. With an Account object, JPA does that for you.
     *  @param account
     */
    public Transfer(Account senderAccount, Account recipientAccount, long amountInCents, String memo){
        super(amountInCents, memo);
        this.senderAccount = senderAccount;
        this.recipientAccount = recipientAccount;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Account getRecipientAccount() {
        return recipientAccount;
    }

    public void setRecipientAccount(Account recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

}
