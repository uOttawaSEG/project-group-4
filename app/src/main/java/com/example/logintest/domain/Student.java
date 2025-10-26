package com.example.logintest.domain;

public class Student extends User{
    private String program;

    public Student(String firstName, String lastName, String email, String phoneNumber, String program, String password) {
        super(firstName, lastName, email, phoneNumber, password);
        this.program = program;
    }
    // getters and setters
    public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        this.program = program;
    }

    public String getRole() {
        return "Student";
    }
}
