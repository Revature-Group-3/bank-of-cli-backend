package com.revature.bankofcli.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.revature.bankofcli.domain.BankTransaction;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> { // auto-boxing will convert
                                                                                          // long(private long id in
                                                                                          // Transaction field) to Long

        // Importantly, this gets the whole row, it's not formatted yet
        @Query("SELECT transaction FROM BankTransaction transaction " +
                        "WHERE transaction.senderAccountId = :accountId " +
                        "OR transaction.recipientAccountId = :accountId " +
                        "ORDER BY transaction.timestamp DESC")
        List<BankTransaction> findTransactionHistory(@Param("accountId") Long accountId);

}
