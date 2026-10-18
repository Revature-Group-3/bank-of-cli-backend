package com.revature.bankofcli.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.revature.bankofcli.domain.Account;
import com.revature.bankofcli.repository.AccountRepository;
import com.revature.bankofcli.exception.InvalidCredentialsException;
import com.revature.bankofcli.security.TokenService;

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

}
