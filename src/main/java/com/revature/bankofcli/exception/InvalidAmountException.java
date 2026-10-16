package com.revature.bankofcli.exception;

import org.springframework.http.HttpStatus;

/** invalid_transaction_amount.json */
public class InvalidAmountException extends ApiException {
    public InvalidAmountException() {
        super(HttpStatus.BAD_REQUEST, "The transaction amount must be greater than zero.");
    }
}