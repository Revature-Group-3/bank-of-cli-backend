package com.revature.bankofcli.dto.transaction;

import jakarta.validation.constraints.NotBlank;

public class TransferRequest extends AbstractTransactionRequest {
    // See AbstractTransactionRequest for amountInCents and accountId fields.

    @NotBlank(message = "Recipient account ID should not be null.")
    private String recipientAccountId;

    public String getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(String recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }
}