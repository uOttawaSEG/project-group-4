package com.example.logintest.domain;

public abstract class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

//    private String userId;
//    private String role;

    //empty constructor
    public User() {

    }
    public User(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }


    // getters and setters
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

//    public String getUserId() {return userId;}
//    public void setUserId(String userId) {this.userId = userId;}
//    public void setRole(String role) {this.role = role; }

    public abstract String getRole();

}
