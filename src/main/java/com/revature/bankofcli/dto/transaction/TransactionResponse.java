package com.revature.bankofcli.dto.transaction;

import java.time.OffsetDateTime;
import com.revature.bankofcli.domain.TransactionTypeEnum;

public class TransactionResponse {
    private long id;
    private String accountEmail;
    private String senderAccountEmail;
    private String recipientAccountEmail;
    private TransactionTypeEnum type;
    private long amountInCents;
    private OffsetDateTime timestamp;

    public TransactionResponse(long id, String accountEmail, String senderAccountEmail, String recipientAccountEmail,
            TransactionTypeEnum type, long amountInCents, OffsetDateTime timestamp) {
        this.id = id;
        this.accountEmail = accountEmail;
        this.senderAccountEmail = senderAccountEmail;
        this.recipientAccountEmail = recipientAccountEmail;
        this.type = type;
        this.amountInCents = amountInCents;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public String getSenderAccountEmail() {
        return senderAccountEmail;
    }

    public String getRecipientAccountEmail() {
        return recipientAccountEmail;
    }

    public TransactionTypeEnum getType() {
        return type;
    }

    public long getAmountInCents() {
        return amountInCents;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}