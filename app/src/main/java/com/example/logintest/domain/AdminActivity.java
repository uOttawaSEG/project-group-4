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
        setContentView(R.layout.adminlogin_fancy);

        adminKeyEditText = findViewById(R.id.adminKey);
        adminLoginButton = findViewById(R.id.adminLoginButton);

        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredKey = adminKeyEditText.getText().toString().trim();
                if (enteredKey.equals(ADMIN_KEY)) {
                    Toast.makeText(AdminActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminActivity.this, AdminInboxActivity.class);
                    //intent.putExtra("USER_ROLE", "Admin");
                    startActivity(intent);

                    finish(); // used for debugging
                } else {
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
