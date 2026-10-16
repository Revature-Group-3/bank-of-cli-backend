package com.revature.bankofcli.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransferRequest {
    @NotBlank(message = "Please provide the recipient's email address.")
    @Email(message = "Please provide a valid recipient email address.")
    private String recipientAccountEmail;

    @NotNull
    @Positive(message = "Transfer amount must be greater than zero.")
    private Long amountInCents;

    public String getRecipientAccountEmail() {
        return recipientAccountEmail;
    }

    public void setRecipientAccountEmail(String recipientAccountEmail) {
        this.recipientAccountEmail = recipientAccountEmail;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }
}