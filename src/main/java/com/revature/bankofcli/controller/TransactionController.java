package com.revature.bankofcli.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.bankofcli.dto.transaction.DepositMoneyRequest;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionHistoryResponse;
import com.revature.bankofcli.dto.transaction.TransferMoneyRequest;
import com.revature.bankofcli.dto.transaction.WithdrawMoneyRequest;
import com.revature.bankofcli.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.jwt.Jwt;
import com.revature.bankofcli.dto.transaction.SuccessfulDepositResponse;
import com.revature.bankofcli.dto.transaction.SuccessfulWithdrawalResponse;
import com.revature.bankofcli.dto.transaction.SuccessfulTransferResponse;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/account/transactions")
    public ResponseEntity<RetrieveTransactionHistoryResponse> getTransactions(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = Long.valueOf(jwt.getSubject());
        RetrieveTransactionHistoryResponse response = transactionService.getTransactionHistory(accountId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<SuccessfulDepositResponse> deposit(@Valid @RequestBody DepositMoneyRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long accountId = Long.valueOf(jwt.getSubject());
        SuccessfulDepositResponse response = transactionService.deposit(accountId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/account/withdraw")
    public ResponseEntity<SuccessfulWithdrawalResponse> withdraw(@Valid @RequestBody WithdrawMoneyRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long accountId = Long.valueOf(jwt.getSubject());
        SuccessfulWithdrawalResponse response = transactionService.withdraw(accountId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("account/transfer")
    public ResponseEntity<SuccessfulTransferResponse> transfer(@Valid @RequestBody TransferMoneyRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long accountId = Long.valueOf(jwt.getSubject());
        SuccessfulTransferResponse response = transactionService.transfer(accountId, request);
        return ResponseEntity.ok(response);
    }
}
