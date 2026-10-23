package com.revature.bankofcli.dto.transaction;

import java.time.Instant;

public class RetrieveWithdrawalResponse extends RetrieveTransactionResponse {
    private String accountEmail;

    public RetrieveWithdrawalResponse(long id, long amountInCents, Instant timestamp, String accountEmail) {
        super(id, amountInCents, timestamp);
        this.accountEmail = accountEmail;
    }

    public String getAccountEmail() {
        return accountEmail;
    }
}
