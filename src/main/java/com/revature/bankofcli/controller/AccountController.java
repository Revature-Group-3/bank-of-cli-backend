package com.revature.bankofcli.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.dto.RegisterAccountRequest;
import com.revature.bankofcli.service.AccountService;
import com.revature.bankofcli.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final AccountService accountService;
    private final AuthService authService;

    public AccountController(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    // Jimmy: didn't want to touch this yet. Follow up with Boone.
    @GetMapping("/accounts/me") 
    public ResponseEntity<AccountResponse> getCurrentAccount(@AuthenticationPrincipal Jwt jwt) {
        long accountId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(accountService.getAccountForUser(accountId));
    }

    // Return HTTP code 201 Created with the account information.
    @PostMapping("/accounts")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterAccountRequest request) {
        var response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}