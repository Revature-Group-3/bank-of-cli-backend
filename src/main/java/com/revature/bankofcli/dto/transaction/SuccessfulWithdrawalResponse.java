package com.revature.bankofcli.dto.transaction;

import java.time.Instant;

public class SuccessfulWithdrawalResponse extends SuccessfulTransactionResponse {
    private String accountEmail;

    public SuccessfulWithdrawalResponse(
        long id, long amountInCents, long newBalanceInCents,
        Instant timestamp,
        String accountEmail
    ) {
        super(id, amountInCents, newBalanceInCents, timestamp);
        this.accountEmail = accountEmail;
    }

    public String getAccountEmail() {
        return accountEmail;
    }
}
