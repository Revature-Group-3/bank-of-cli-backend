package com.revature.bankofcli.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public class TransferRequest extends AbstractTransactionRequest {
    // See AbstractTransactionRequest for amountInCents and accountId fields.

    @NotBlank(message = "Recipient email should not be null.") // this message maybe isn't standard?
    @Email(message = "Invalid email address format.")
    private String recipientAccountEmail;

    public TransferRequest(Long amountInCents, String recipientAccountEmail, String memo) {
        this.setAmountInCents(amountInCents);
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