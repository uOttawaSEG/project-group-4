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

public class StudentInfoActivity extends AppCompatActivity {
    // Database services
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // Variables for each input field
    EditText firstNameInput;
    EditText lastNameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText phoneInput;
    EditText programInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentinformation);

        // Link variables to the XML fields by their ID
        firstNameInput = findViewById(R.id.student_name);
        lastNameInput = findViewById(R.id.student_last_name);
        emailInput = findViewById(R.id.student_email);
        passwordInput = findViewById(R.id.student_password);
        phoneInput = findViewById(R.id.student_phone);
        programInput = findViewById(R.id.student_program);
        submitButton = findViewById(R.id.submit_student_btn);

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
    //Home button
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
        String program = programInput.getText().toString().trim();

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

        // Check program
        if (program.isEmpty()){
            programInput.setError("Program is a required field");
            everythingOK = false;
        }
        if (program.length() < 4) {
            programInput.setError("Program must be at least 4 characters");
            everythingOK = false;
        }
        return everythingOK;
    }

    void validateAndRegisterUser(){
        if (!checkForErrors()){
            Toast.makeText(StudentInfoActivity.this, "Registration failed, please correct the fields marked in red", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String program = programInput.getText().toString().trim();

        Log.d("StudentInfoActivity", "Submitting student registration for: " + email);

        // Just submit directly without any checks
        checkPendingStudents(firstName, lastName, email, phone, program, password);
    }

//    void validateAndRegisterUser() {
//        if (checkForErrors()) {
//            String email = emailInput.getText().toString().trim();
//            String password = passwordInput.getText().toString().trim();
//            String firstName = firstNameInput.getText().toString().trim();
//            String lastName = lastNameInput.getText().toString().trim();
//            String phone = phoneInput.getText().toString().trim();
//            String program = programInput.getText().toString().trim();
//            //First verify if the user is under the students list
//            databaseReference.child("students").orderByChild("email").equalTo(email).get().addOnCompleteListener(task -> {
//                if (!task.isSuccessful()){
//                    Log.e("Registration check", "Error checking for existing 'students' node", task.getException());
//                    Toast.makeText(StudentInfoActivity.this, "Could not verify email. Please try again", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (task.getResult().exists()){
//                    //if the email was found in 'students' path in db
//                    emailInput.setError("This email address is already in use.");
//                    Toast.makeText(StudentInfoActivity.this, "The email address you entered is already in use. Go to home to log in", Toast.LENGTH_LONG).show();
//                }
//                else{
//                    //if it is not found in students path, verify in the 'tutors' path: they could have entered an email that is in use for a tutor account
//                    checkIfEmailExists(firstName, lastName, email, phone, program, password);
//                }
//            });
//        } else { //if checkForErrors returns false
//            Toast.makeText(StudentInfoActivity.this, "Registration failed, please correct the fields marked in red", Toast.LENGTH_SHORT).show();
//            return;
//        }
//    }
    private void checkIfEmailExists(String firstName, String lastName, String email, String phone, String program, String password){
        //Verify if the email does not exist for a tutor account
        databaseReference.child("tutors").orderByChild("email").equalTo(email).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Log.e("Registration check", "Error checking for existing 'tutors' node", task.getException());
                Toast.makeText(StudentInfoActivity.this, "Could not verify email. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            if (task.getResult().exists()){
                //if the email was found in 'tutors' path in db
                emailInput.setError("This email address is already in use.");
                //The toast will not precise that it is used for a tutor account for security purposes
                Toast.makeText(StudentInfoActivity.this, "The email address you entered is already in use. Go to home to log in.", Toast.LENGTH_LONG).show();
            }
            else{
                //if it is not found in tutors path, verify in the 'pending' path
                checkIfDenied(firstName, lastName, email, phone, program, password);
            }
        });

    }
    private void checkIfDenied(String firstName, String lastName, String email, String phone, String program, String password){
        //Verify if the email has been denied by the administrator
        databaseReference.child("denied").orderByChild("email").equalTo(email).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Log.e("Registration check", "Error checking for existing 'denied' node", task.getException());
                Toast.makeText(StudentInfoActivity.this, "Could not verify email. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            if (task.getResult().exists()){
                //if the email was found in 'denied' path in db
                emailInput.setError("The Administrator denied the registration request for this email address.");
                //Provides a (fake) phone number for contacting the administrator if they wish to resolve the matter.
                Toast.makeText(StudentInfoActivity.this, "The Administrator denied the registration request for this email address. To resolve this matter, contact 613-724-3361.", Toast.LENGTH_LONG).show();
            }
            else{
                //if it is not found in students path, verify in the 'pending' path
                checkPendingStudents(firstName, lastName, email, phone, program, password);
            }
        });
    }
    private void checkPendingStudents(String firstName, String lastName, String email, String phone, String program, String password){
        databaseReference.child("pending").orderByChild("email").equalTo(email).get().addOnCompleteListener(task->{
            if (!task.isSuccessful()){
                Log.e("Registration check", "Error checking 'pending' node", task.getException());
                Toast.makeText(StudentInfoActivity.this, "Could not verify email. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }
            if (task.getResult().exists()){
                //if the email was found in 'pending' path in db
                emailInput.setError("This email has already been submitted for review.");
                Toast.makeText(StudentInfoActivity.this, "This email address is pending approval. For more information, contact the Administrator at 613-724-3361.", Toast.LENGTH_LONG).show();

                //Go back to the home screen
                Intent intent=new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else{
                submitNewPendingRequest(firstName, lastName, email, phone, program, password);
            }
        });
    }
    private void submitNewPendingRequest(String firstName, String lastName, String email, String phone, String program, String password){
        //Create user's ID
        String pendingId = databaseReference.child("pending").push().getKey();
        if (pendingId==null){
            Toast.makeText(StudentInfoActivity.this, "Could not create user ID. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        //Create an instance of Student class
        Student pendingStudent = new Student(firstName, lastName, email, phone, program, password);
        databaseReference.child("pending").child(pendingId).setValue(pendingStudent).addOnCompleteListener(task->{
            if (task.isSuccessful()){
                Log.d("Pending registration", "Student request submitted for: "+ email);
                Toast.makeText(StudentInfoActivity.this,"Student registration request submitted. The Administrator will review your application. Please come back later.", Toast.LENGTH_LONG).show();

                //Go back to the home screen
                Intent intent=new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else{
                Log.w("Pending registration", "Failed to submit student request", task.getException());
                Toast.makeText(StudentInfoActivity.this, "Could not submit request. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }



}