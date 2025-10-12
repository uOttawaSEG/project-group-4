package com.example.logintest.domain;

import java.util.List;

public class Tutor extends User  {
    private String highestDegree;
    private List<String> coursesOffered;

    public Tutor(String firstName, String lastName, String email, String phoneNumber, String highestDegree, List<String> coursesOffered) {
        super(firstName, lastName, email, phoneNumber);
        this.highestDegree = highestDegree;
        this.coursesOffered = coursesOffered;
    }

    // getters and setters

    public String getHighestDegree() {
        return highestDegree;
    }

    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree;
    }

    public List<String> getCoursesOffered() {
        return coursesOffered;
    }

    public void setCoursesOffered(List<String> coursesOffered) {
        this.coursesOffered = coursesOffered;
    }

    public String getRole() {
        return "Tutor";
    }

}
