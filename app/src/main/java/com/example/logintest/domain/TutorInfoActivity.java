package com.example.logintest.domain;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class TutorInfoActivity extends AppCompatActivity {

    // Database services
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // Variables for each input field
    EditText firstNameInput;
    EditText lastNameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText phoneInput;
    EditText degreeInput;
    EditText coursesInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorinfo);

        // Link variables to the XML fields by their ID
        firstNameInput = findViewById(R.id.tutor_firstname);
        lastNameInput = findViewById(R.id.tutor_lastname);
        emailInput = findViewById(R.id.tutor_email);
        passwordInput = findViewById(R.id.tutor_password);
        phoneInput = findViewById(R.id.tutor_phone);
        degreeInput = findViewById(R.id.tutor_degree);
        coursesInput = findViewById(R.id.tutor_courses);
        submitButton = findViewById(R.id.submit_tutor_btn);

        // Initialize  both firebase services
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // When user clicks submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndRegisterUser();
            }
        });
    }
    // Home button
    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Check if all fields are filled correctly
    boolean checkForErrors() {
        // Get what the user typed and removed extra spaces
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String degree = degreeInput.getText().toString().trim().toLowerCase();
        String courses = coursesInput.getText().toString().trim().toLowerCase();

        boolean everythingOK = true;

        // Check first name
        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            everythingOK = false;
        }
        else if (firstName.length() < 2) {
            firstNameInput.setError("First name must be at least 2 characters");
            everythingOK = false;
        }

        // Check last name
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            everythingOK = false;
        }
        else if (lastName.length() < 2) {
            lastNameInput.setError("Last name must be at least 2 characters");
            everythingOK = false;
        }

        // Check email
        if (email.isEmpty()) {
            emailInput.setError("Email address is required");
            everythingOK = false;
        }
        else if (!email.contains("@") || !email.contains(".")) {
            emailInput.setError("Please enter a valid email address");
            everythingOK = false;
        }

        // Check password
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            everythingOK = false;
        }
        else if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            everythingOK = false;
        }
        else if (!password.matches(".*[A-Z].*")) {
            passwordInput.setError("Password must contain at least one uppercase letter");
            everythingOK = false;
        }
        else if (!password.matches(".*[a-z].*")) {
            passwordInput.setError("Password must contain at least one lowercase letter");
            everythingOK = false;
        }
        else if (!password.matches(".*[0-9].*")) {
            passwordInput.setError("Password must contain at least one number");
            everythingOK = false;
        }
        else if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            passwordInput.setError("Password must contain at least one special character");
            everythingOK = false;
        }

        // Check phone
        if (phone.isEmpty()) {
            phoneInput.setError("Phone number is required");
            everythingOK = false;
        }
        else if (phone.length() != 10) {
            phoneInput.setError("Phone number must be exactly 10 digits");
            everythingOK = false;
        }
        else if (!phone.matches("[0-9]+")) {
            phoneInput.setError("Phone number can only contain numbers");
            everythingOK = false;
        }

        // Check courses offered
        if (courses.isEmpty()){
            coursesInput.setError("Courses offered is a required field");
            everythingOK = false;
        }
        else if (!courses.contains("french") && !courses.contains("english") && !courses.contains("math") && !courses.contains("maths") && !courses.contains("mathematics") && !courses.contains("science") && !courses.contains("chemistry") && !courses.contains("physics") && !courses.contains("history") && !courses.contains("spanish") && !courses.contains("music")) {
            coursesInput.setError("You must write at least one course from the list: french, english, math, science, chemistry, physics, history, spanish, music. (Check your spelling)");
            everythingOK = false;
        }

        //Check highest degree
        if (degree.isEmpty()){
            degreeInput.setError("Highest degree is a required field");
            everythingOK = false;
        }
        else if(!degree.isEmpty()){
            int degreesFound=0;
            boolean atLeastOne=false;
            String[] validDegrees = {"none", "high school diploma", "bachelor", "master", "md/phd", "phd", "doctorate"};
            for (String validDegree:validDegrees){
                if (degree.contains(validDegree)){
                    degreesFound++;
                    atLeastOne=true;
                }
            }
            if (!atLeastOne){
                degreeInput.setError("You must write at least one degree from the list: none, high school diploma, bachelor, master, md/phd, phd, doctorate. (Check your spelling)");
                everythingOK = false;
            }
            else if (degreesFound>1){
                degreeInput.setError("You can only write one degree from the list: none, high school diploma, bachelor, master, md/phd, phd, doctorate. (Check your spelling)");
                everythingOK = false;
            }

        }
        return everythingOK;
    }

    void validateAndRegisterUser(){
        if (!checkForErrors()){
            Toast.makeText(TutorInfoActivity.this, "Registration failed, please correct the fields marked in red", Toast.LENGTH_SHORT).show();
            return;
        }
        String email=emailInput.getText().toString().trim();
        String password=passwordInput.getText().toString().trim();
        String firstName=firstNameInput.getText().toString().trim();
        String lastName=lastNameInput.getText().toString().trim();
        String phone=phoneInput.getText().toString().trim();
        String degree=degreeInput.getText().toString().trim().toLowerCase();
        String courses=coursesInput.getText().toString().trim().toLowerCase();
        //First verify if the user is under the tutors list
        databaseReference.child("tutors").orderByChild("email").equalTo(email).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Log.e("Registration check", "Error checking for existing 'tutors' node", task.getException());
                Toast.makeText(TutorInfoActivity.this, "Could not verify email. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            if (task.getResult().exists()){
                //if the email was found in 'tutors' path in db
                emailInput.setError("This email address is already in use.");
                Toast.makeText(TutorInfoActivity.this, "The email address you entered is already in use. Go to home to log in.", Toast.LENGTH_LONG).show();
            }
            else{
                //if it is not found in tutors path, verify in the 'pending tutors' path
                checkPendingTutors(firstName, lastName, email, phone, degree, courses, password);
            }
        });
        /*
        //mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            //if (task.isSuccessful()) {
                //Log.d("Firebase authentication", "Registration successful for:" + email);
                //Toast.makeText(TutorInfoActivity.this, "Authentification successful", Toast.LENGTH_SHORT).show();

                //Create user's ID
                String pendingId = mAuth.getCurrentUser().getUid();
                //Create a list of courses for the constructor
                List<String> courseList= Arrays.asList(courses.split("\\s*,\\s*"));
                //Create an instance of Tutor class
                Tutor pendingTutor = new Tutor(firstName, lastName, email, phone, degree, courseList, password);

                //Save the pending tutor to the database
                databaseReference.child("pending tutors").child(pendingId).setValue(pendingTutor).addOnCompleteListener(pendingTask -> {

                    if (pendingTask.isSuccessful()) {
                        Log.d("Pending registration", "Tutor request submitted for: "+pendingId);
                        Toast.makeText(TutorInfoActivity.this, "Tutor request submitted. Wait for the administrator to review your registration.", Toast.LENGTH_SHORT).show();

                        //Go back to the home screen after submission
                        Intent intent=new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); //Close the tutor registration UI
                    } else {//If the submission fails, so if task.isSuccessful() is false
                        Log.w("Pending registration", "Failed to submit tutor request", pendingTask.getException());
                        //Toast.makeText(TutorInfoActivity.this, "Failed to submit tutor request", Toast.LENGTH_LONG).show();
                        if(pendingTask.getException() instanceof FirebaseAuthUserCollisionException){
                            emailInput.setError("This email address is already in use");
                            Toast.makeText(TutorInfoActivity.this, "This email address is already in use by another account", Toast.LENGTH_SHORT).show();
                            Toast.makeText(TutorInfoActivity.this, "Click on Go to Home to login", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //If the tutor has trouble w Internet or Firebase has a temporary issue or maybe VPN, etc.
                            Toast.makeText(TutorInfoActivity.this, "Registration failed, try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /*
                //Save the tutor's data to firebase
                databaseReference.child("tutors").child(userId).setValue(tutor).addOnCompleteListener(this, dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Log.d("Firebase database", "Tutor data created for"+userId);
                        Toast.makeText(TutorInfoActivity.this, "Tutor registered successfully", Toast.LENGTH_SHORT).show();
                        //Go to dashboard if registration is successful
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.putExtra("USER_ROLE", "Tutor"); // to display role on Dashboard
                        startActivity(intent);
                        finish(); // Close the registration screen
                    }
                    else{
                        Log.w("Firebase database", "Tutor account creation failed", dbTask.getException());
                        Toast.makeText(TutorInfoActivity.this, "Tutor registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
                */
        /*
            else{ //Authentication failed if task.isSuccessful() is false
                Log.w("Firebase authentification", "Registration failed", task.getException());
                //Check if account already exists
                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                    emailInput.setError("This email address is already in use");
                    Toast.makeText(TutorInfoActivity.this, "This email address is already in use by another account", Toast.LENGTH_SHORT).show();
                    Toast.makeText(TutorInfoActivity.this, "Click on Go to Home to login", Toast.LENGTH_SHORT).show();
                }
                else{
                    //If the tutor has trouble w Internet or Firebase has a temporary issue or maybe VPN, etc.
                    Toast.makeText(TutorInfoActivity.this, "Registration failed, try again", Toast.LENGTH_SHORT).show();
                }
            }
            */

        //});

    }

    private void checkPendingTutors(String firstName, String lastName, String email, String phone, String degree, String courses, String password){
        databaseReference.child("pending tutors").orderByChild("email").equalTo(email).get().addOnCompleteListener(task->{
            if (!task.isSuccessful()){
                Log.e("Registration check", "Error checking 'pending tutors' node", task.getException());
                Toast.makeText(TutorInfoActivity.this, "Could not verify email. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }
            if (task.getResult().exists()){
                //if the email was found in 'pending tutors' path in db
                emailInput.setError("This email has already been submitted for review.");
                Toast.makeText(TutorInfoActivity.this, "This email address is pending approval. For more information, contact 613-724-3361.", Toast.LENGTH_LONG).show();

                //Go back to the home screen
                Intent intent=new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else{
                submitNewPendingRequest(firstName, lastName, email, phone, degree, courses, password);
            }
        });
    }

    private void submitNewPendingRequest(String firstName, String lastName, String email, String phone, String degree, String courses, String password){
        //Create user's ID
        String pendingId = databaseReference.child("pending tutors").push().getKey();
        if (pendingId==null){
            Toast.makeText(TutorInfoActivity.this, "Could not create user ID. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        //Create a list of courses for the constructor
        List<String> courseList= Arrays.asList(courses.split("\\s*,\\s*"));
        //Create an instance of Tutor class
        Tutor pendingTutor = new Tutor(firstName, lastName, email, phone, degree, courseList, password);
        databaseReference.child("pending tutors").child(pendingId).setValue(pendingTutor).addOnCompleteListener(task->{
            if (task.isSuccessful()){
                Log.d("Pending registration", "Tutor request submitted for: "+ email);
                Toast.makeText(TutorInfoActivity.this,"Tutor registration request submitted. The Administrator will review your application shortly.", Toast.LENGTH_LONG).show();

                //Go back to the home screen
                Intent intent=new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else{
                Log.w("Pending registration", "Failed to submit tutor request", task.getException());
                Toast.makeText(TutorInfoActivity.this, "Could not submit request. Please try again.", Toast.LENGTH_LONG).show();
            }
        });


    }
}
