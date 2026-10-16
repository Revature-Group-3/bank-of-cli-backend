package com.revature.bankofcli.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.bankofcli.domain.BankTransaction;

@Repository 
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long>{ // auto-boxing will convert long(private long id in Transaction field) to Long 
}
