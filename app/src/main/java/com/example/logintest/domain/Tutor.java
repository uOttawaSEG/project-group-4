package com.example.logintest.domain;

import java.util.List;

public class Tutor extends User  {
    private String highestDegree;
    private List<String> coursesOffered;
    private double rating = 0.0; // Added field
    private int numberOfRatings = 0; // Added field

    //empty constructor
    public Tutor() {
        //super();
    }
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

    // Added Getters and Setters
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }
}
