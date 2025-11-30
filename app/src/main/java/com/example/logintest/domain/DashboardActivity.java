package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {
    TextView userRole;
    Button adminInboxButton;

    Button viewCalendarButton;
    Button toSessions;
    Button toInbox;
    Button toStudentInbox;
    private Student studentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_fancy);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();

        adminInboxButton = findViewById(R.id.inboxButton);
        userRole = findViewById(R.id.user_role);
        String role = getIntent().getStringExtra("USER_ROLE");

        if ("Student".equals(role)) {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser == null) {
                return;
            }
            String studId = firebaseUser.getUid();
            databaseRef.child("students").child(studId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        studentUser = snapshot.getValue(Student.class);

                        // go to sessions list button
                        toSessions = findViewById(R.id.viewSessionsBtn);
                        toSessions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DashboardActivity.this, AvailableSessionListActivity.class);
                                intent.putExtra("USER_ROLE", role);
                                intent.putExtra("CURR_STUDENT", studentUser); // pass the student object
                                startActivity(intent);
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else { //is tutor
            FirebaseUser fbUser =mAuth.getCurrentUser();
            if (fbUser == null) {
                return;
            }
            String tutorID = fbUser.getUid();

            // go to sessions list button
            toSessions = findViewById(R.id.viewSessionsBtn);
            toSessions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, AvailableSessionListActivity.class);
                    intent.putExtra("USER_ROLE", role);
                    //intent.putExtra("CURR_STUDENT", studentUser); // pass the student object
                    intent.putExtra("TUTOR_ID", tutorID);
                    startActivity(intent);
                }
            });

            toInbox = findViewById(R.id.viewInbox);
            toInbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, TutorInboxActivity.class);
                    intent.putExtra("CURR_STUDENT", studentUser); // pass the student object
                    intent.putExtra("TUTOR_ID", tutorID);
                    startActivity(intent);
                }
            });

        }

        viewCalendarButton = findViewById(R.id.viewCalendarButton);
        viewCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, CalendarViewActivity.class);
                startActivity(intent);
            }
        });


        // only for students, inbox button
        toStudentInbox = findViewById(R.id.viewStudentInbox);
        toStudentInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, StudentInboxActivity.class);
                intent.putExtra("CURR_STUDENT", studentUser); // pass the student object
                startActivity(intent);
            }
        });

        if (role != null) {
            userRole.setText(role);
            if (role.equals("Admin")) {
                adminInboxButton.setVisibility(View.VISIBLE);
            } else if (role.equals("Tutor")) {
                toInbox.setVisibility(View.VISIBLE);
                viewCalendarButton.setVisibility(View.VISIBLE);
            }else if (role.equals("Student")) {
                toStudentInbox.setVisibility(View.VISIBLE);
            }
        } else {
            userRole.setText("Unknown");
        }


        // Admin inbox access button
        adminInboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.admin_inbox);
                //Intent intent = new Intent(Inboxy.this, InboxActivity.class);
                //startActivity(intent);
            }
        });
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
