package com.revature.bankofcli.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class WithdrawRequest {
    @NotNull(message = "Please enter an amount.")
    @Positive(message = "Please enter a valid amount.")
    private Long amountInCents;

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }
}
