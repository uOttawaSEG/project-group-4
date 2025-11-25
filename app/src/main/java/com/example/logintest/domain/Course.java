package com.example.logintest.domain;

public class Course {
    private String name;
    private String description;
    public Course(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}
