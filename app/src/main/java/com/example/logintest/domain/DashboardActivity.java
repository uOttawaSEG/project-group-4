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
    Button inboxButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Find the inbox button
        inboxButton = findViewById(R.id.inboxButton);

        // Set the click listener programmatically
        inboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This explicitly starts the InboxActivity
                Intent intent = new Intent(DashboardActivity.this, InboxActivity.class);
                startActivity(intent);
            }
        });

        // Display user's role on dashboard and check if admin
        userRole = findViewById(R.id.user_role);
        String role = getIntent().getStringExtra("USER_ROLE");

        if (role != null) {
            userRole.setText(role);
            // If the user is an Admin, show the inbox button
            if (role.equals("Admin")) {
                inboxButton.setVisibility(View.VISIBLE);
            }
        } else {
            userRole.setText("Unknown");
        }
    }

    // This method is now handled by the OnClickListener above
    // public void goToInbox(View view) { ... }

    public void goToLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(DashboardActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LogOutPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
