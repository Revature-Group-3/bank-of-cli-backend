package com.revature.bankofcli.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.revature.bankofcli.domain.Account;
import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.dto.RegisterAccountRequest;
import com.revature.bankofcli.repository.AccountRepository;
import com.revature.bankofcli.exception.InvalidCredentialsException;
import com.revature.bankofcli.security.TokenService;
//import com.revature.bankofcli.domain.Role;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public String login(String email, String rawPassword) { // Takes email and raw pass to check and return JWT
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException()); // Throws 401
        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new InvalidCredentialsException(); // Also throws 401
        }
        return tokenService.generateToken(account);
    }

    public AccountResponse register(RegisterAccountRequest request) {
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exist.");
        }

        // Store PW as a hash
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create the account
        Account account = new Account(request.getEmail(), request.getFirstName(), request.getLastName(), hashedPassword,
                0L);

        // Always set new account's role to USER.
        // account.setRole(Role.USER);

        // Save this to the DB
        Account savedAccount = accountRepository.save(account);

        // Return a safe account without returning the hash.
        return new AccountResponse(savedAccount.getEmail(), savedAccount.getFirstName(), savedAccount.getLastName(),
                0L);
    }

}
