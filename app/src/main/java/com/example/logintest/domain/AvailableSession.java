package com.example.logintest.domain;

public class AvailableSession {
    //private Tutor tutor;
    private String tutorId;
    private String tutorName;
    private String tutorCourses;
    private String date;
    private String timeSlot;
    private String sessionId;
    private boolean isAvailable;

    public AvailableSession() {
    }

    public AvailableSession(String tutorId, Tutor tutor, String date, String timeSlot) {
        this.tutorId=tutorId;
        this.tutorName = tutor.getFirstName() +" "+ tutor.getLastName();
        this.tutorCourses = tutor.getCoursesOffered().toString();
        this.date = date;
        this.timeSlot = timeSlot;
        this.isAvailable = true;
        setId();
    }

    private void setId() {
        this.sessionId = "SESSION_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    public String getDate() {
        return date;
    }
    public String getTutorName() {
        return tutorName;
    }
    public String getTutorCourses() {
        return tutorCourses;
    }


    public String getTimeSlot() {
        return timeSlot;
    }


    public String getSessionId() {
        if(sessionId ==null || sessionId.isEmpty()) {
            setId();
        }
        return sessionId;
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

}