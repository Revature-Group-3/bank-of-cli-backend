package com.revature.bankofcli.dto.transaction;

public class DepositRequest extends AbstractTransactionRequest {
    public DepositRequest(Long amountInCents, Long accountId) {
        this.setAmountInCents(amountInCents);
        this.setAccountId(accountId);
    }
}
