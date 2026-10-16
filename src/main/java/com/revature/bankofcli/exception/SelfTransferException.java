package com.revature.bankofcli.exception;

import org.springframework.http.HttpStatus;

/** attempted_transfer_to_own_account.json */
public class SelfTransferException extends ApiException {
    public SelfTransferException() {
        super(HttpStatus.BAD_REQUEST, "You cannot transfer funds to your own account.");
    }
}