package com.revature.bankofcli.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.bankofcli.domain.Account;
import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.exception.AccountNotFoundException;
import com.revature.bankofcli.repository.AccountRepository;

@Service
public class AccountService {
    
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountForUser(long accountId) {
        var account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        return new AccountResponse(account.getEmail(), account.getFirstName(), account.getLastName(), account.getBalanceInCents());
    }
}
