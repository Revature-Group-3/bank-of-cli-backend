package com.revature.bankofcli.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for every error that maps to a response in docs/json_format/unsuccessful_responses/.
 * Extends RuntimeException so @Transactional rolls back when one is thrown.
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}