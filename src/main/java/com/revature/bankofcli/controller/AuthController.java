package com.revature.bankofcli.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.dto.SignInRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/login")
public class AuthController {

    @PostMapping 
    public ResponseEntity<AccountResponse> signIn (@Valid @RequestBody SignInRequest request) {
        //TO:DO, log in to Account Service
    throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}
