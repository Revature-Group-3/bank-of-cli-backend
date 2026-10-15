package com.revature.bankofcli.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "account")
public class Account {

    // Postgres database in AWS
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Email 
    @Column (name = "email", unique = true)
    private String email;

    @Column (name = "first_name")
    private String firstName;

    @Column (name = "last_name")
    private String lastName;

    @Column (name = "password")
    private String password;

    @Column (name = "balance_in_cents")
    private long balanceInCents;

    protected Account(){}

    public Account( String email, String firstName,String lastName,String password, long balanceInCents){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.balanceInCents = balanceInCents;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
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
    // Might be worth flagging
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public long getBalanceInCents() {
        return balanceInCents;
    }
    public void setBalanceInCents(long balanceInCents) {
        this.balanceInCents = balanceInCents;
    }
}

