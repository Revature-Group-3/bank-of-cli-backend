package com.revature.bankofcli.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterAccountRequest {
    @NotBlank(message = "Please provide a First Name.")
    private String firstName;

    @NotBlank(message = "Please provide a Last Name.")
    private String lastName;

    @NotBlank(message = "Please provide an email address.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotBlank(message = "Please provide a password.")
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}