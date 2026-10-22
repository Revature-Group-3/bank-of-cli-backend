
package com.revature.bankofcli.service;

import com.revature.bankofcli.domain.Account;
import com.revature.bankofcli.dto.AccountResponse;
import com.revature.bankofcli.dto.RegisterAccountRequest;
import com.revature.bankofcli.exception.InvalidCredentialsException;
import com.revature.bankofcli.repository.AccountRepository;
import com.revature.bankofcli.security.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//Because it's a test we'll use mockito.
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private Account account;
    private RegisterAccountRequest request;

    @BeforeEach
    void setUp() {
        account = new Account(
                "JimmyLe@test.com",
                "Jimmy",
                "Le",
                "hashedPassword");

        request = new RegisterAccountRequest();
        request.setEmail("JimmyLe@test.com");
        request.setFirstName("Jimmy");
        request.setLastName("Le");
        request.setPassword("password123");
    }

    // Very simple test for success, non existence failure and incorrect failure for
    // both login and register
    @Test
    void loginSuccess() {
        when(accountRepository.findByEmail("JimmyLe@test.com"))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches(
                "password123", "hashedPassword")).thenReturn(true);

        when(tokenService.generateToken(account))
                .thenReturn("mock-jwt-token");

        String token = authService.login(
                "JimmyLe@test.com", "password123");

        assertEquals("mock-jwt-token", token);
        verify(tokenService).generateToken(account);
    }

    @Test
    void loginFailsWhenEmailDoesNotExist() {
        when(accountRepository.findByEmail("missing@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        "missing@email.com", "password123"));

        verifyNoInteractions(passwordEncoder, tokenService);
    }

    @Test
    void loginFailsWhenPasswordIsIncorrect() {
        when(accountRepository.findByEmail("JimmyLe@test.com"))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches(
                "wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(
                        "JimmyLe@test.com", "wrongPassword"));

        verifyNoInteractions(tokenService);
    }

    @Test
    void registerSuccess() {
        when(accountRepository.findByEmail("JimmyLe@test.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("JimmyLe@test.com", response.getEmail());
        assertEquals("Jimmy", response.getFirstName());
        assertEquals("Le", response.getLastName());
        assertEquals(0L, response.getBalanceInCents());

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void registerFailsWhenEmailAlreadyExists() {
        when(accountRepository.findByEmail("JimmyLe@test.com"))
                .thenReturn(Optional.of(account));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(accountRepository, never()).save(any(Account.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void registerHashesPasswordBeforeSaving() {
        when(accountRepository.findByEmail("JimmyLe@test.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(request);

        verify(passwordEncoder).encode("password123");

        verify(accountRepository).save(argThat(saved -> saved.getPassword().equals("hashedPassword")
                && !saved.getPassword().equals("password123")));
    }
}
