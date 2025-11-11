package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AvailableSessionListActivity extends AppCompatActivity {
    LinearLayout availableSessionsContainer;
    LinearLayout cardContainer;
    List<AvailableSession> availableSessions = new ArrayList<>();
    private DatabaseReference tutorPath;
    private DatabaseReference sessionPath;
    Button toDash;
    Student student;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessions_list);

        role = getIntent().getStringExtra("USER_ROLE");
        student = (Student)getIntent().getSerializableExtra("CURR_STUDENT");

        toDash = findViewById(R.id.fromSessionsToDashBtn);
        toDash.setOnClickListener(v -> {
            Intent intent = new Intent(AvailableSessionListActivity.this, DashboardActivity.class);
            if("Student".equals(role)) {
                intent.putExtra("USER_ROLE", "Student");
            } else {
                intent.putExtra("USER_ROLE", "Tutor");
            }
            startActivity(intent);
        });

        availableSessionsContainer = findViewById(R.id.availableSessionsContainer);
        cardContainer = findViewById(R.id.cardContainer);

        //linking up firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tutorPath = database.getReference("tutors");
        sessionPath = database.getReference("sessions");

        setupSessionListener();
    }

    private void setupSessionListener() {
        sessionPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                availableSessions.clear();
                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                    AvailableSession session = sessionSnapshot.getValue(AvailableSession.class);
                    if (session != null && session.isAvailable()) {
                        availableSessions.add(session);
                    }
                }
                displaySessions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AvailableSessionListActivity.this, "Error loading sessions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //display the session cards
    private void displaySessions() {
        // first refresh by clearing the cards
        cardContainer.removeAllViews();

        LayoutInflater inflater= LayoutInflater.from(this);

        // and then reload/make each session into a visible card
        for (AvailableSession session: availableSessions) {
            CardView sessionInfoCard = (CardView) inflater.inflate(R.layout.available_session, cardContainer, false);

            TextView tutorName = sessionInfoCard.findViewById(R.id.sessionTutorName);
            TextView tutorCourses = sessionInfoCard.findViewById(R.id.tutorCourses);
            TextView sessionDate = sessionInfoCard.findViewById(R.id.sessionDate);
            TextView sessionTime = sessionInfoCard.findViewById(R.id.sessionTime);
            Button cancelSessionBtn = sessionInfoCard.findViewById(R.id.cancelSessionBtn);
            Button studentRegisterSessionBtn = sessionInfoCard.findViewById(R.id.studentRegisterSessionBtn);

            tutorName.setText(session.getTutorName());
            tutorCourses.setText(session.getTutorCourses());
            sessionDate.setText(session.getDate());
            sessionTime.setText(session.getTimeSlot());

            if("Student".equalsIgnoreCase(role)) {
                studentRegisterSessionBtn.setVisibility(View.VISIBLE);
                cancelSessionBtn.setVisibility(View.GONE);
            } else if ("Tutor".equalsIgnoreCase(role)) {
                studentRegisterSessionBtn.setVisibility(View.GONE);
                cancelSessionBtn.setVisibility(View.VISIBLE);
            }

            studentRegisterSessionBtn.setOnClickListener(v -> {
                // sessions is not available anymore
                // set availabiltiy to false and then add it to pending list

                session.setAvailable(false);

                SessionRequester studentRequester = new SessionRequester(student, session);
                DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("sessionRequests");
                requestsReference.child(session.getSessionId()).setValue(studentRequester);


                requestsReference.child(session.getSessionId()).setValue(studentRequester)
                    .addOnSuccessListener(aVoid -> {
                        sessionPath.child(session.getSessionId()).removeValue();
                        Toast.makeText(AvailableSessionListActivity.this, "Session request successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AvailableSessionListActivity.this, "Could not request session", Toast.LENGTH_SHORT).show();
                    });

//                sessionPath.child(session.getSessionId()).setValue(session)
//                        .addOnSuccessListener(aVoid -> {
//                            // Logging the session request in the database
//                            //FirebaseDatabase.getInstance().getReference("sessionRequests").child(session.getSessionId()).setValue(new SessionRequester(sessionStudent, session));
//                            Toast.makeText(AvailableSessionListActivity.this, "Session request successful", Toast.LENGTH_SHORT).show();
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(AvailableSessionListActivity.this, "Failed to request session", Toast.LENGTH_SHORT).show();
//                        });
            });

            //cancel the session (to do: make it only available to tutors)
            //removes the card from the firebase
            cancelSessionBtn.setOnClickListener(v -> {
                sessionPath.child(session.getSessionId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AvailableSessionListActivity.this, "Session cancelled", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AvailableSessionListActivity.this, "Failed to cancel session", Toast.LENGTH_SHORT).show();
                        });
            });
            cardContainer.addView(sessionInfoCard);
        }

        // change title to"no sessions" if the list is empty
        if (availableSessions.isEmpty()) {
            TextView sessionListTitle = findViewById(R.id.sessionListTItle);
            sessionListTitle.setText("No available sessions");
            sessionListTitle.setGravity(Gravity.CENTER);
            availableSessionsContainer.addView(sessionListTitle);
        }
    }

}