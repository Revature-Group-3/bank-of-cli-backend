package com.revature.bankofcli.exception;

import org.springframework.http.HttpStatus;

/** insufficient_funds.json */
public class InsufficientFundsException extends ApiException {
    public InsufficientFundsException() {
        super(HttpStatus.BAD_REQUEST, "Insufficient funds. The withdrawal/transfer amount exceeds your available balance.");
    }
}