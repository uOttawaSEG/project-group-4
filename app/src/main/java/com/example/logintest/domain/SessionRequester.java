package com.example.logintest.domain;

// this class is used to log all the Students who request a session
public class SessionRequester {
    private String studentName;
    private String studentProgram;
    private String studentEmail;
    private String studentPhone;
    private String sessionDate;
    private String sessionTime;
    private boolean sessionAvailable;
    public String sessionStatus;
    private String sessionId;
    private String tutorId;


    public SessionRequester() {
    }

    public SessionRequester(Student student, AvailableSession session) {
        this.studentName = student.getFirstName() +" "+ student.getLastName();
        this.studentProgram = student.getProgram();
        this.studentEmail = student.getEmail();
        this.studentPhone = student.getPhoneNumber();
        this.sessionDate = session.getDate();
        this.sessionTime = session.getTimeSlot();
        this.sessionAvailable = session.isAvailable();
        this.sessionStatus = "pending";
        this.sessionId = session.getSessionId();
        this.tutorId = session.getTutorId();
    }


    public String getStudentName() {
        return studentName;
    }

    public String getStudentProgram() {
        return studentProgram;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public boolean isSessionAvailable() {
        return sessionAvailable;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String status) {
        this.sessionStatus = status;
    }
    public String getSessionId() {
        return sessionId;
    }
    public String getTutorId() {
        return tutorId;
    }
    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }
}
