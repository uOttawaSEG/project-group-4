package com.example.logintest.domain;

public class pendingUser {
    private String status;
    private String pendingId;
    private User user;

    public pendingUser() {
        // empty constructor for the firebase
    }

    public pendingUser(String requestId, User user) {
        this.pendingId = requestId;
        this.user = user;
        this.status = "pending";
    }

    // Getters and Setters (required for Firebase)
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


    public String getStatus() {
        return status;
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