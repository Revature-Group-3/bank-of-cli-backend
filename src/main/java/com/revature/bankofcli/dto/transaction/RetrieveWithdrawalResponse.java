package com.revature.bankofcli.dto.transaction;

import java.time.OffsetDateTime;

public class RetrieveWithdrawalResponse extends RetrieveTransactionResponse {
    private String accountEmail;

    public RetrieveWithdrawalResponse(long id, long amountInCents, OffsetDateTime timestamp, String accountEmail) {
        super(id, amountInCents, timestamp);
        this.accountEmail = accountEmail;
    }

    public String getAccountEmail() {
        return accountEmail;
    }
}
