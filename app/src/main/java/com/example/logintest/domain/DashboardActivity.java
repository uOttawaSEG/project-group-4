package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {
    TextView userRole;
    Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // to display user's role on dashboard
        userRole = findViewById(R.id.user_role);
        String role = getIntent().getStringExtra("USER_ROLE");
        if (role!=null) {
            userRole.setText(role);
        }
        else{
            userRole.setText("Unknown");
        }

        // to prevent app crashing when logging out
        logoutBtn = findViewById(R.id.LogOutButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FirebaseAuth.getInstance().signOut();
               Toast.makeText(DashboardActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
               finish();
            }
        });
    }
}