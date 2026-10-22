package com.revature.bankofcli.dto.transaction;

import java.time.OffsetDateTime;

public class SuccessfulDepositResponse extends SuccessfulTransactionResponse {
    private String accountEmail;

    public SuccessfulDepositResponse(
        long id, long amountInCents, long newBalanceInCents,
        OffsetDateTime timestamp,
        String accountEmail
    ) {
        super(id, amountInCents, newBalanceInCents, timestamp);
        this.accountEmail = accountEmail;
    }

    public String getAccountEmail() {
        return accountEmail;
    }
}
