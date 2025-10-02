package com.example.logintest.domain;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class StudentInfoActivity extends AppCompatActivity {

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

        // When user clicks submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForErrors();
            }
        });
    }

    // Check if all fields are filled correctly
    void checkForErrors() {
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

        // If everything is good
        if (everythingOK) {
            finish();
        }
    }
}