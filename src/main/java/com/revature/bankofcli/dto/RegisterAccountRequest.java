package com.revature.bankofcli.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterAccountRequest {
    @NotBlank(message = "Please provide a First Name.")
    @Size(min = 2, max = 60, message = "Invalid name format." ) // Standard length max for first name 
    private String firstName;

    @NotBlank(message = "Please provide a Last Name.")
    @Size(min = 2, max = 60, message = "Invalid name format." ) // Standard length max for last name
    private String lastName;

    @NotBlank(message = "Please provide an email address.")
    @Email(message = "Please provide a valid email address.")
    @Size(min = 2, max = 254, message = "Invalid email address format.") // Standard length for emails
    private String email;

    @NotBlank(message = "Please provide a password.")
    @Size(min = 8, max = 72 , message = "Invalid password format.") // Standard length for passwords
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