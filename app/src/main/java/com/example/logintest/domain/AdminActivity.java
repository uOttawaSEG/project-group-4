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

        // Admin login with key
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use .trim() to remove leading/trailing whitespace
                String enteredKey = adminKeyEditText.getText().toString().trim();
                if (enteredKey.equals(ADMIN_KEY)) {
                    // Key is correct, navigate to the dashboard
                    Intent intent = new Intent(AdminActivity.this, DashboardActivity.class);
                    intent.putExtra("USER_ROLE", "Admin"); // to display role on Dashboard
                    startActivity(intent);
                } else {
                    // Key is incorrect, show an error message
                    Toast.makeText(AdminActivity.this, "Invalid Admin Key", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Home button
    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
