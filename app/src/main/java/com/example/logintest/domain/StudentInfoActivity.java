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

    void validateAndRegisterUser() {
        if (checkForErrors()) {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String program = programInput.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase authentication", "Registration successful for:" + email);
                    Toast.makeText(StudentInfoActivity.this, "Registration request successful", Toast.LENGTH_SHORT).show();

                    //Create user's ID
                    String userId = mAuth.getCurrentUser().getUid();
                    //Create an instance of Student class
                    Student student = new Student(firstName, lastName, email, phone, program);

                    //Save the student's data to firebase
                    databaseReference.child("pending").child(userId).setValue(student).addOnCompleteListener(this, dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Log.d("Firebase database", "Student data created for"+userId);
                            Toast.makeText(StudentInfoActivity.this, "Student request submitted successfully", Toast.LENGTH_SHORT).show();
                            //Go to the welcome screen if registration request is successful
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close the registration screen
                        }
                        else{
                            Log.w("Firebase database", "Tutor account creation failed", dbTask.getException());
                            Toast.makeText(StudentInfoActivity.this, "Tutor registration request failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{ //Authentication (registration request) failed if task.isSuccessful() is false
                    Log.w("Firebase authentification", "Registration request failed", task.getException());
                    //Check if account already exists
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        //Check under which path it is in the database
                        databaseReference.child("pending").orderByChild("email").equalTo(email).get().addOnCompleteListener(pendingTask->{
                            if (pendingTask.isSuccessful() && pendingTask.getResult().exists()){
                                emailInput.setError("This email has already been submitted for review.");
                                Toast.makeText(StudentInfoActivity.this, "This email address is pending approval.", Toast.LENGTH_LONG).show();
                                Toast.makeText(StudentInfoActivity.this, "Contact the Administrator at 613-724-3361 to resolve this matter.", Toast.LENGTH_LONG).show();
                            }else{
                                databaseReference.child("denied").orderByChild("email").equalTo(email).get().addOnCompleteListener(deniedTask->{
                                    if (deniedTask.isSuccessful() && deniedTask.getResult().exists()){
                                        emailInput.setError("The Administrator denied the registration request for this email address.");
                                        Toast.makeText(StudentInfoActivity.this, "The Administrator denied the registration request for this email.", Toast.LENGTH_LONG).show();
                                        Toast.makeText(StudentInfoActivity.this, "To resolve this matter, contact 613-724-3361.", Toast.LENGTH_LONG).show();
                                    }else{ //that means the user is either under tutors or students path, which means this email address is already in use
                                        emailInput.setError("This email address is already in use.");
                                        Toast.makeText(StudentInfoActivity.this, "The email address you entered is already in use. Go to home to log in.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                    else{
                        //If the tutor has trouble w Internet or Firebase has a temporary issue or vpn is on
                        Toast.makeText(StudentInfoActivity.this, "Registration failed, try again", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } else { //if checkForErrors returns false
            Toast.makeText(StudentInfoActivity.this, "Registration failed, please correct the fields marked in red", Toast.LENGTH_SHORT).show();
        }
    }
}