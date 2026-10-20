package com.revature.bankofcli.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger =LoggerFactory.getLogger(AccountController.class);
    /* 

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }
    

    @PostMapping 
    public ResponseEntity<AccountResponse> signIn (@Valid @RequestBody SignInRequest request) {

        AccountResponse response = authService.signIn(request);
        logger.info("Account registered successfully. Account ID: {}", response.getAccountId());

        return ResponseEntity.ok(response);
    }

    @PostMapping 
    public ResponseEntity<AccountResponse> register (@Valid @RequestBody RegisterAccountRequest request) {
            
        AccountResponse response = authService.register(request);
        logger.info("User signed in successfully. Account ID: {}", response.getAccountId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    */
}
