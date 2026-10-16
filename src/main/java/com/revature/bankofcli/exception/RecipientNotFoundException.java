package com.revature.bankofcli.exception;

import org.springframework.http.HttpStatus;

/** transfer_recipient_does_not_exist.json */
public class RecipientNotFoundException extends ApiException {
    public RecipientNotFoundException() {
        super(HttpStatus.NOT_FOUND, "The recipient account could not be found.");
    }
}