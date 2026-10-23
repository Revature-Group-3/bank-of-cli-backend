package com.revature.bankofcli.dto.transaction;

import java.time.Instant;

public class RetrieveTransferResponse extends RetrieveTransactionResponse {
    private String senderAccountEmail;
    private String recipientAccountEmail;
    private String direction;

    public RetrieveTransferResponse(
        long id, long amountInCents, Instant timestamp,
        String senderAccountEmail, String recipientAccountEmail,
        String loggedInAccountEmail
    ) {
        super(id, amountInCents, timestamp);
        this.senderAccountEmail = senderAccountEmail;
        this.recipientAccountEmail = recipientAccountEmail;
        
        // If logged-in account is sender, mark direction as "OUTGOING"
        if (loggedInAccountEmail.equals(senderAccountEmail)) {
            this.direction = "OUTGOING";
        }

        // If logged-in account is recipient, mark direction as "INCOMING"
        else if (loggedInAccountEmail.equals(recipientAccountEmail)) {
            this.direction = "INCOMING";
        }

        // Throw exception is logged-in account did not participate in this transfer
        else {
            throw new IllegalArgumentException("Logged-in user did not participate in this transfer.");
        }
    }

    public String getSenderAccountEmail() {
        return senderAccountEmail;
    }

    public String getRecipientAccountEmail() {
        return recipientAccountEmail;
    }

    public String getDirection() {
        return direction;
    }
}
