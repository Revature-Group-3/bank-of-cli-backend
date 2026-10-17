package com.revature.bankofcli.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

import com.revature.bankofcli.repository.BankTransactionRepository;
import com.revature.bankofcli.domain.BankTransaction;

@Service
public class TransactionHistoryService {
    private final BankTransactionRepository bankTransactionRepository;

    public TransactionHistoryService(BankTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Transactional(readOnly = true)
    public List<BankTransaction> getTransactionHistory(Long accountId) {
        // Should be allowed to be null
        return bankTransactionRepository.findTransactionHistory(accountId);
    }

}
