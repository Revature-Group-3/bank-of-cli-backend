package com.revature.bankofcli.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.revature.bankofcli.dto.DepositRequest;
import com.revature.bankofcli.dto.TransactionHistoryResponse;
import com.revature.bankofcli.dto.TransactionResponse;
import com.revature.bankofcli.dto.TransferRequest;
import com.revature.bankofcli.dto.WithdrawRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class TransactionController {
    
    @GetMapping("/accounts/me/transactions") //me is temporary
    public ResponseEntity<TransactionHistoryResponse> getTransactions() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/accounts/me/deposits")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/accounts/me/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}
