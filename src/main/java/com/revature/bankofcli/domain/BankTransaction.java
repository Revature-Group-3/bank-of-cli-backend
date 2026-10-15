package com.revature.bankofcli.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity 
@Table(name = "bank_transaction")
@Inheritance (strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type") // Discriminator approach
public abstract class BankTransaction {

    /**
     * Deposit, withdrawal and transfer need to be split because Deposit and withdrawl link to one account,
     * and Transfer links to two: a sender and a recipient.
     * 
     * The shared parts (id, amount, timestamp, memo, type) stay in BankTransaction, so you dont repeat them
     * 2 times. Each subclass adds only its own account link(s)-> thats what the ERD's "d" circle describes.
     * 
     * Why abstract ? A transaction is always one of the three. There is no such thing as a plain "bank transaction"
     * thats neither a deposit, a withdrawal, nor a transfer.
     * 
     * This also stops anyone from writing a new BankTransaction() by mistake, so the compiler enforces the rule.
     * It also makes it clear the class is a shared base, not something you save on its own.
     * 
     * JPA doesn't strictly Require it to be abstract. It just makes the design match the ERD and prevents a bug
     * 
     * We can run one query on BankTransaction and get every type back for the history page.
     */

    @Id // Required by JPA to identify the primary key
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Column (name = "timestamp")
    private OffsetDateTime timestamp;

    @Column (name = "amount_in_cents")
    private long amountInCents;

    // Memo
    @Column (name = "memo")
    private String memo;

    protected BankTransaction(){}

    public BankTransaction( OffsetDateTime timestamp, long amountInCents, String memo){
        this.timestamp = timestamp;
        this.amountInCents = amountInCents;
        this.memo = memo;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public long getAmountInCents() {
        return amountInCents;
    }
    public void setAmountInCents(long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
