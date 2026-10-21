package com.revature.bankofcli.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
    /*
    private final AccountService accountService;
    
    
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }   

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {

        AccountResponse response = accountService.getAccountById(accountId);
        logger.info("Successfully retrieved account ID: {}", accountId);

        return ResponseEntity.ok(response);
    }

    */
}
