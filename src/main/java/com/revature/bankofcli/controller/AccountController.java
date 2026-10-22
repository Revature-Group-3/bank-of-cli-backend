package com.revature.bankofcli.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    // Refers to the service for registering.
    //private final AuthService authService;

    private static final Logger logger =LoggerFactory.getLogger(AccountController.class);
    

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }   

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getAccount(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = Long.parseLong(jwt.getSubject());

        AccountResponse response = accountService.getAccountById(accountId);
        logger.info("Successfully retrieved account ID: {}", accountId);

        return ResponseEntity.ok(response);
    }

    
}
