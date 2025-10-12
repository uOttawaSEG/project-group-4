package com.example.logintest.domain;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class HomepageActivity extends AppCompatActivity {

    //Variables for each input field
    EditText username;
    EditText password;
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginorregister);

        //Link variables to the XML fields by their ID
        username = findViewById(R.id.user_name);
        password = findViewById(R.id.user_password);
        loginButton = findViewById(R.id.loginButton);

        //When user clicks login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForErrors();
            }
        });
    }

    void checkForErrors() {
        //Get what the user typed and removed extra spaces
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        boolean everythingOK = true;

        //Check username
        if (usernameInput.isEmpty()) {
            username.setError("Username is required");
            everythingOK = false;
        }

        //Check password
        if (passwordInput.isEmpty()) {
            password.setError("Password is required");
            everythingOK = false;
        }

        //Verify if the user exists



    }
}
