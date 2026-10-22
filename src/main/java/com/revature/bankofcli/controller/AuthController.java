package com.revature.bankofcli.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.dto.RegisterAccountRequest;
import com.revature.bankofcli.dto.SignInRequest;
import com.revature.bankofcli.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger =LoggerFactory.getLogger(AuthController.class);
    

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }
    

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody SignInRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<AccountResponse> register (@Valid @RequestBody RegisterAccountRequest request) {
            
        AccountResponse response = authService.register(request);
        //Log needs to be updated, message is incorrect and .getAccoutId() is no longer a method
        //logger.info("User signed in successfully. Account ID: {}", response.getAccountId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
}
