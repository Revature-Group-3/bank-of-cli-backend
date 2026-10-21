package com.revature.bankofcli.dto.transaction;

import jakarta.validation.constraints.NotBlank;

public class TransferRequest extends AbstractTransactionRequest {
    // See AbstractTransactionRequest for amountInCents and accountId fields.

    @NotBlank(message = "Recipient account ID should not be null.")
    private Long recipientAccountId;

    public Long getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(Long recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }
}