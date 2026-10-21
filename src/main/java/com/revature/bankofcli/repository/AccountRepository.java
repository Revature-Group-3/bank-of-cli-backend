package com.revature.bankofcli.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;

import com.revature.bankofcli.domain.Account;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long> { // Autoboxing will convert long(private long id in Account field) to Long 
    boolean existsByEmail(String email);
    
    Optional<Account> findByEmail(String email);

    // Finds account for update
    // Locks row to prevent multiple simultaneous modifications of same account
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(Long id);
}
