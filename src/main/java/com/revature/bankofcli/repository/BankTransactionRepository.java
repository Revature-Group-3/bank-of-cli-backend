package com.revature.bankofcli.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.revature.bankofcli.domain.BankTransaction;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> { // auto-boxing will convert
                                                                                          // long(private long id in
                                                                                          // Transaction field) to Long

    // TODO: update this with the actual field names
    @Query("SELECT transaction FROM BankTransaction transaction " +
            "WHERE transaction.senderAccountId = :accountId " +
            "OR transaction.recipientAccountId = :accountId " +
            "ORDER BY transaction.timestamp DESC")
    List<BankTransaction> findTransactionHistory(@Param("accountId") Long accountId);

}
