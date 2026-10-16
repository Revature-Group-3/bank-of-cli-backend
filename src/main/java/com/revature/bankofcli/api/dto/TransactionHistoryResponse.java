package com.revature.bankofcli.api.dto;

import java.util.List;

public class TransactionHistoryResponse {
    private final List<TransactionResponse> transactions;

    public TransactionHistoryResponse(List<TransactionResponse> transactions) {
        this.transactions = List.copyOf(transactions);
    }

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }
}
