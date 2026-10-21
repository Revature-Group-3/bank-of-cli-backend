package com.revature.bankofcli.dto.transaction;

import jakarta.validation.constraints.NotBlank;

public class TransferRequest extends AbstractTransactionRequest {
    // See AbstractTransactionRequest for amountInCents and accountId fields.

    @NotBlank(message = "Recipient account ID should not be null.")
    private Long recipientAccountId;

    public TransferRequest(Long amountInCents, Long accountId, Long recipientAccountId, String memo) {
        this.setAmountInCents(amountInCents);
        this.setAccountId(accountId);
        this.recipientAccountId = recipientAccountId;
        this.setMemo(memo);
    }

    public Long getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(Long recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }
}