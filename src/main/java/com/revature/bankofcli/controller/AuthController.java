package com.revature.bankofcli.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.dto.SignInRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/login")
public class AuthController {

    @PostMapping 
    public ResponseEntity<AccountResponse> signIn (@Valid @RequestBody SignInRequest request) {
        AccountResponse response = new AccountResponse(
            request.getEmail(),
            "Test",
            "User",
            0L
        );
        return ResponseEntity.ok(response);
    }
}
