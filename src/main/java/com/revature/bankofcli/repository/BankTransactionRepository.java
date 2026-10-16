package com.revature.bankofcli.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.bankofcli.domain.BankTransaction;

@Repository 
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long>{ // auto-boxing will convert long(private long id in Transaction field) to Long 

    // CRUD:
    // create, read, update, delete

    // Read (GET)
    //public BankTransaction getBankTransactionById();

    // public BankTransaction getAllBankTransactions();

    // // Update(PATCH) & Create(POST)
    // public BankTransaction deposit();

    // public BankTransaction withdrawal();

    // public BankTransaction transfer();

    // Delete(DELETE)
    //public BankTransaction deleteBankTransaction();
    

}
