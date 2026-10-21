package com.revature.bankofcli.dto.transaction;

public class DepositRequest extends AbstractTransactionRequest {
    public DepositRequest() {
    }

    public DepositRequest(Long amountInCents, Long accountId, String memo) {
        this.setAmountInCents(amountInCents);
        this.setAccountId(accountId);
        this.setMemo(memo);
    }
}
