package com.revature.bankofcli.dto.transaction;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class WithdrawRequest {
    @NotNull(message = "Please enter an amount.")
    @Positive(message = "Please enter a valid amount.")
    @Max(1000000) // Max amount limit
    private Long amountInCents;

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }
}
