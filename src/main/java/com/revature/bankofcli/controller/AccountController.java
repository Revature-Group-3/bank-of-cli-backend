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
import com.revature.bankofcli.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    // Reference to the service that handles registration.
    private final AuthService authService;

    public AccountController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me") // rember this is a substitute
    public ResponseEntity<AccountResponse> getCurrentAccount() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> register(@Valid @RequestBody RegisterAccountRequest request) {
        AccountResponse response = new AccountResponse(
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                0L);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// Github push note: Modified AccountController to reference the service layer.
public class AccountController {
    // Refers to the service for registering.
    private final AuthService authService;

    public AccountController(AuthService authService) {
        this.authService = authService;
    }

    // Jimmy: didn't want to touch this yet. Follow up with Boone.
    @GetMapping("/me") // rember this is a substitute
    public ResponseEntity<AccountResponse> getCurrentAccount() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    // Return HTTP code 201 Created with the account information.
    @PostMapping
    public ResponseEntity<AccountResponse> register(@Valid @RequestBody RegisterAccountRequest request) {
        // Changed this to refer to the service of account registration.
        AccountResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}