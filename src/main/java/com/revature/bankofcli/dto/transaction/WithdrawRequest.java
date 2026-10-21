package com.revature.bankofcli.dto.transaction;

public class WithdrawRequest extends AbstractTransactionRequest {
    public WithdrawRequest(Long amountInCents, Long accountId, String memo) {
        this.setAmountInCents(amountInCents);
        this.setAccountId(accountId);
        this.setMemo(memo);
    }

    public WithdrawRequest() {
    }
}
