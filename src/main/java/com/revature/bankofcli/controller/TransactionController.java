package com.revature.bankofcli.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.revature.bankofcli.dto.transaction.DepositMoneyRequest;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionHistoryResponse;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionResponse;
import com.revature.bankofcli.dto.transaction.TransferMoneyRequest;
import com.revature.bankofcli.dto.transaction.WithdrawMoneyRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @GetMapping("/accounts/me/transactions") // me is temporary
    public ResponseEntity<RetrieveTransactionHistoryResponse> getTransactions() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/accounts/me/deposits")
    public ResponseEntity<RetrieveTransactionResponse> deposit(@Valid @RequestBody DepositMoneyRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/accounts/me/withdraw")
    public ResponseEntity<RetrieveTransactionResponse> withdraw(@Valid @RequestBody WithdrawMoneyRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/transfers")
    public ResponseEntity<RetrieveTransactionResponse> transfer(@Valid @RequestBody TransferMoneyRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}
