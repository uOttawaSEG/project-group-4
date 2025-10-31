package com.example.logintest.domain;

public class PendingUser {
    private String status;
    private String pendingId;
    private User user;

    public PendingUser() {
        // empty constructor for the firebase
    }

    public PendingUser(String requestId, User user) {
        this.pendingId = requestId;
        this.user = user;
        this.status = "pending";
    }


    // getters and setters for the firebase
    public String getRequestId() {
        return pendingId;
    }
    public void setRequestId(String pendingId) {
         this.pendingId = pendingId;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPendingName() {
        return user.getFirstName()+" "+ user.getLastName();
    }

    public String getPendingEmail() {
        return user.getEmail();
    }

    public String getPendingRole() {
        return user.getRole();
    }
}