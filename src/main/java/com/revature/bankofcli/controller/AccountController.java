package com.revature.bankofcli.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.dto.RegisterAccountRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @GetMapping("/me") //rember this is a substitute
    public ResponseEntity<AccountResponse> getCurrentAccount() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping 
    public ResponseEntity<AccountResponse> register (@Valid @RequestBody RegisterAccountRequest request) {
            AccountResponse response = new AccountResponse(
            request.getEmail(),
            request.getFirstName(),
            request.getLastName(),
            0L
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
}
