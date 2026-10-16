package com.revature.bankofcli.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends ApiException{

    public AccountNotFoundException(){
        super(HttpStatus.NOT_FOUND, "Account could not be found.");
    }

    public AccountNotFoundException(String message){
        super(HttpStatus.NOT_FOUND, message);
    }

}
