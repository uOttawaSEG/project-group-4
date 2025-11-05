package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {
    TextView userRole;
    Button adminInboxButton;

    Button viewCalendarButton;
    Button toSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        adminInboxButton = findViewById(R.id.inboxButton);
        // Admin inbox access button
        adminInboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.admin_inbox);
                //Intent intent = new Intent(Inboxy.this, InboxActivity.class);
                //startActivity(intent);
            }
        });

        viewCalendarButton = findViewById(R.id.viewCalendarButton);
        viewCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, CalendarViewActivity.class);
                startActivity(intent);
            }
        });

        // go to sessions list button
        toSessions = findViewById(R.id.viewSessionsBtn);
        toSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, AvailableSessionListActivity.class);
                startActivity(intent);
            }
        });

        userRole = findViewById(R.id.user_role);
        String role = getIntent().getStringExtra("USER_ROLE");

        if (role != null) {
            userRole.setText(role);
            if (role.equals("Admin")) {
                adminInboxButton.setVisibility(View.VISIBLE);
            }
        } else {
            userRole.setText("Unknown");
        }
    }

    public void goToLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(DashboardActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LogOutPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
