package com.revature.bankofcli.dto;

public class AccountResponse {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final long balanceInCents;

    public AccountResponse(String email, String firstName, String lastName, long balanceInCents) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balanceInCents = balanceInCents;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getBalanceInCents() {
        return balanceInCents;
    }
}