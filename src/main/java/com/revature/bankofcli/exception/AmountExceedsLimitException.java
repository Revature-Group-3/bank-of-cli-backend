package com.revature.bankofcli.exception;

import org.springframework.http.HttpStatus;

/** deposit_transfer_amount_exceeds_limit.json */
public class AmountExceedsLimitException extends ApiException {
    public AmountExceedsLimitException() {
        super(HttpStatus.BAD_REQUEST, "The deposit/transfer amount exceeds the maximum allowed limit of $10,000.");
    }

    public AmountExceedsLimitException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}