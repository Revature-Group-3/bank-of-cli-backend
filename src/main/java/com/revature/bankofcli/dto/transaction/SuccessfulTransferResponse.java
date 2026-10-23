package com.revature.bankofcli.dto.transaction;

import java.time.Instant;

public class SuccessfulTransferResponse extends SuccessfulTransactionResponse {
    private String senderAccountEmail;
    private String recipientAccountEmail;

    public SuccessfulTransferResponse(
        long id, long amountInCents, long newBalanceInCents,
        Instant timestamp,
        String senderAccountEmail, String recipientAccountEmail
    ) {
        super(id, amountInCents, newBalanceInCents, timestamp);
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
