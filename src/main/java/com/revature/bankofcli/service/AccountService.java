package com.revature.bankofcli.service;

import org.springframework.stereotype.Service;

import com.revature.bankofcli.repository.AccountRepository;

@Service 
public class AccountService {

    public final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }
    
    /*
    public AccountResponse getAccountById(Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(
            () -> new AccountNotFoundException("Account not found with ID: " + accountId)); 
        
        return new AccountResponse(account.getFirstName(), account.getLastName(), account.getBalanceInCents());
    }
    */
}
