package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private EditText adminKeyEditText;
    private Button adminLoginButton;
    private final String ADMIN_KEY = "38u&&^D2j2^67tyd67";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminlogin);

        adminKeyEditText = findViewById(R.id.adminKey);
        adminLoginButton = findViewById(R.id.adminLoginButton);

        /**
         * Listen for admin key during login
         */
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredKey = adminKeyEditText.getText().toString();
                // correct key
                if (enteredKey.equals(ADMIN_KEY)) {
                    Intent intent = new Intent(AdminActivity.this, AdminActivity.class);
                    startActivity(intent);
                    // incorrect key
                } else {
                    Toast.makeText(AdminActivity.this, "Invalid Admin Key", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // go to the home screen
    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
