package com.revature.bankofcli.exception;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException(String message){
        super(message);
    }

    public AccountNotFoundException(Throwable cause){
        super(cause);
    }

    // Has to be in specific order to match the RuntimeException parameters
    public AccountNotFoundException( String message, Throwable cause ){
        super(message, cause);

    }



}
