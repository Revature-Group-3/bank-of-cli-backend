package com.revature.bankofcli.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.bankofcli.domain.Account;

@Repository 
public interface AccountRepository extends JpaRepository< Account, Long>{ //autoboxing will convert long(private long id in Account field) to Long 

}
