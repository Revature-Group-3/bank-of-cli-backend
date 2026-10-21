package com.revature.bankofcli.dto.transaction;

import jakarta.validation.constraints.NotBlank;

public class TransferRequest extends AbstractTransactionRequest {
    // See AbstractTransactionRequest for amountInCents and accountId fields.

    @NotBlank(message = "Recipient account ID should not be null.")
    private String recipientAccountEmail;

    public TransferRequest(Long amountInCents, Long accountId, String recipientAccountEmail, String memo) {
        this.setAmountInCents(amountInCents);
        this.setAccountId(accountId);
        this.recipientAccountEmail = recipientAccountEmail;
        this.setMemo(memo);
    }

    public TransferRequest() {
    }

    public String getRecipientAccountEmail() {
        return recipientAccountEmail;
    }

    public void setRecipientAccountEmail(String recipientAccountEmail) {
        this.recipientAccountEmail = recipientAccountEmail;
    }
}