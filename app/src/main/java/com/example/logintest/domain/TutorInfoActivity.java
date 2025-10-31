package com.example.logintest.domain;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase authentication", "Registration successful for:" + email);
                    Toast.makeText(TutorInfoActivity.this, "Registration request successful", Toast.LENGTH_SHORT).show();

                    //Create user's ID
                    String userId = mAuth.getCurrentUser().getUid();
                    //Create a list of courses for the constructor
                    List<String> courseList= Arrays.asList(courses.split("\\s*,\\s*"));
                    //Create an instance of Tutor class
                    Tutor tutor = new Tutor(firstName, lastName, email, phone, degree, courseList, password);

                    //Save the tutor's data to firebase
                    databaseReference.child("pending").child(userId).setValue(tutor).addOnCompleteListener(this, dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Log.d("Firebase database", "Tutor data created for"+userId);
                            Toast.makeText(TutorInfoActivity.this, "Tutor request submitted successfully", Toast.LENGTH_SHORT).show();
                            //Go to the welcome screen if registration request is successful
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close the registration screen
                        }
                        else{
                            Log.w("Firebase database", "Tutor account creation failed", dbTask.getException());
                            Toast.makeText(TutorInfoActivity.this, "Tutor registration request failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{ //Authentication failed if task.isSuccessful() is false
                    Log.w("Firebase authentification", "Registration request failed", task.getException());
                    //Check if account already exists
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        //Check under which path it is in the database
                        databaseReference.child("pending").orderByChild("email").equalTo(email).get().addOnCompleteListener(pendingTask->{
                            if (pendingTask.isSuccessful() && pendingTask.getResult().exists()){
                                emailInput.setError("This email has already been submitted for review.");
                                Toast.makeText(TutorInfoActivity.this, "This email address is pending approval.", Toast.LENGTH_LONG).show();
                                Toast.makeText(TutorInfoActivity.this, "Contact the Administrator at 613-724-3361 to resolve this matter.", Toast.LENGTH_LONG).show();
                            }else{
                                databaseReference.child("denied").orderByChild("email").equalTo(email).get().addOnCompleteListener(deniedTask->{
                                    if (deniedTask.isSuccessful() && deniedTask.getResult().exists()){
                                        emailInput.setError("The Administrator denied the registration request for this email address.");
                                        Toast.makeText(TutorInfoActivity.this, "The Administrator denied the registration request for this email.", Toast.LENGTH_LONG).show();
                                        Toast.makeText(TutorInfoActivity.this, "To resolve this matter, contact 613-724-3361.", Toast.LENGTH_LONG).show();
                                    }else{ //that means the user is either under tutors or students path, which means this email address is already in use
                                        emailInput.setError("This email address is already in use.");
                                        Toast.makeText(TutorInfoActivity.this, "The email address you entered is already in use. Go to home to log in.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                    else{
                        //If the tutor has trouble w Internet or Firebase has a temporary issue
                        Toast.makeText(TutorInfoActivity.this, "Registration failed, try again", Toast.LENGTH_SHORT).show();
                    }
                }

            });
    }
}
