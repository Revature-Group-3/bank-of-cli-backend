package com.revature.bankofcli.dto.transaction;

import java.time.OffsetDateTime;

public class RetrieveTransferResponse extends RetrieveTransactionResponse {
    private String senderAccountEmail;
    private String recipientAccountEmail;

    public RetrieveTransferResponse(
        long id, long amountInCents, OffsetDateTime timestamp,
        String senderAccountEmail, String recipientAccountEmail
    ) {
        super(id, amountInCents, timestamp);
        this.senderAccountEmail = senderAccountEmail;
        this.recipientAccountEmail = recipientAccountEmail;
    }

    public String getSenderAccountEmail() {
        return senderAccountEmail;
    }

    public String getRecipientAccountEmail() {
        return recipientAccountEmail;
    }
}
