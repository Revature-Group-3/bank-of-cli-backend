package com.revature.bankofcli.dto.transaction;

import java.time.OffsetDateTime;

public class SuccessfulTransferResponse extends SuccessfulTransactionResponse {
    private String senderAccountEmail;
    private String recipientAccountEmail;

    public SuccessfulTransferResponse(
        long id, long amountInCents, long newBalanceInCents,
        OffsetDateTime timestamp,
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
