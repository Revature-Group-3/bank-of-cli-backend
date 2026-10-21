package com.revature.bankofcli.dto.transaction;

public class WithdrawalRequest extends AbstractTransactionRequest {
    public WithdrawalRequest(Long amountInCents, Long accountId, String memo) {
        this.setAmountInCents(amountInCents);
        this.setAccountId(accountId);
        this.setMemo(memo);
    }

    public WithdrawalRequest() {
    }
}
