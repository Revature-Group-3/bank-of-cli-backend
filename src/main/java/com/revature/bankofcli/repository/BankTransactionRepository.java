package com.revature.bankofcli.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.revature.bankofcli.domain.BankTransaction;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> { // auto-boxing will convert
                                                                                          // long(private long id in
                                                                                          // Transaction field) to Long

        // Query selects all transactions that are connected to the specified account and orders them by timestamp (descending) 
        @Query("SELECT t FROM BankTransaction t WHERE " +
                "(TREAT(t AS Deposit).account.id = :accountId) OR " +
                "(TREAT(t AS Withdrawal).account.id = :accountId) OR " +
                "(TREAT(t AS Transfer).senderAccount.id = :accountId) OR " +
                "(TREAT(t AS Transfer).recipientAccount.id = :accountId) " +
                "ORDER BY t.timestamp DESC")
        List<BankTransaction> findTransactionHistory(@Param("accountId") Long accountId);

}
