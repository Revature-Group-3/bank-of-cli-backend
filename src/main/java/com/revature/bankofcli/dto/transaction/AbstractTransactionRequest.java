package com.revature.bankofcli.dto.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public abstract class AbstractTransactionRequest {
    @NotNull(message = "Please enter an amount.")
    @Positive(message = "Please enter a positive amount.")
    private Long amountInCents;

    // Should be passed in by the system
    @NotNull(message = "Account ID should not be null.")
    private Long accountId;

    private String memo;

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
