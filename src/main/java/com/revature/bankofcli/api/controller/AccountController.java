package com.revature.bankofcli.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import com.revature.bankofcli.api.dto.AccountResponse;
import com.revature.bankofcli.api.dto.RegisterAccountRequest;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @GetMapping("/me") //rember this is a substitute
    public ResponseEntity<AccountResponse> getCurrentAccount() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping 
    public ResponseEntity<AccountResponse> register (@Valid @RequestBody RegisterAccountRequest request) {
            //TO:DO, registration to Account Service
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
        }
}
