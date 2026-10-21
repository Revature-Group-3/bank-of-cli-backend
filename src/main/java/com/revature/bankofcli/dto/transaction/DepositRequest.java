package com.revature.bankofcli.dto.transaction;

public class DepositRequest extends AbstractTransactionRequest {
    public DepositRequest() {
    }

    public DepositRequest(Long amountInCents, String memo) {
        this.setAmountInCents(amountInCents);
        this.setMemo(memo);
    }
}
