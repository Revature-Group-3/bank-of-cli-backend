package com.revature.bankofcli.domain;

import java.time.Instant;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Table(name = "bank_transaction")
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@EntityListeners(AuditingEntityListener.class)
public abstract class BankTransaction {

    /**
     * Deposit, withdrawal and transfer need to be split because Deposit and withdrawl link to one account,
     * and Transfer links to two: a sender and a recipient.
     * 
     * The shared parts (id, amountInCents, timestamp, memo) stay in BankTransaction, so you dont repeat them.
     * Each subclass adds only its own account link(s).
     * 
     * Why abstract ? A transaction is always one of the three. There is no such thing as a plain "bank transaction"
     * thats neither a deposit, a withdrawal, nor a transfer.
     * 
     * This also stops anyone from writing a new BankTransaction() by mistake, so the compiler enforces the rule.
     * It also makes it clear the class is a shared base, not something you save on its own.
     * 
     * JPA doesn't strictly require it to be abstract. It just prevents a bug.
     * 
     * We can run one query on BankTransaction and get every type back for the history page.
     */

    @Id // Required by JPA to identify the primary key
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    // Timestamp initialized at instant of creation, not updateable
    @CreatedDate @NotNull
    @Column (name = "timestamp", updatable = false)
    private Instant timestamp;

    @Positive @NotNull
    @Column (name = "amount_in_cents")
    private long amountInCents;

    // Memo: free-text notes field
    @Column (name = "memo")
    private String memo;

    protected BankTransaction() {}

    public BankTransaction(long amountInCents, String memo) {
        this.amountInCents = amountInCents;
        this.memo = memo;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
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
