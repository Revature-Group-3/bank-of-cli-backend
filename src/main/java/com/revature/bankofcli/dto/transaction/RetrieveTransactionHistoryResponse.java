package com.revature.bankofcli.dto.transaction;

import java.util.List;

public class RetrieveTransactionHistoryResponse {
    private final List<RetrieveTransactionResponse> transactions;

    public RetrieveTransactionHistoryResponse(List<RetrieveTransactionResponse> transactions) {
        this.transactions = List.copyOf(transactions);
    }

    public List<RetrieveTransactionResponse> getTransactions() {
        return transactions;
    }
}
