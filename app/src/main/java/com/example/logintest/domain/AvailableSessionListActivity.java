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
            if(role.equals("Student")) {
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


                DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("sessionRequests");
                //requestsReference.child(session.getSessionId()).setValue(studentRequester);

                //preventing student from booking times that overlap with their exisitng bookings
                requestsReference.orderByChild("studentEmail").equalTo(student.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnap: snapshot.getChildren()) {
                            SessionRequester timeBooked = dataSnap.getValue(SessionRequester.class);

                            if(timeBooked != null && (timeBooked.getSessionStatus().equalsIgnoreCase("pending")|| timeBooked.getSessionStatus().equalsIgnoreCase("accepted"))) {
                                if(timeBooked.getSessionDate().equals(session.getDate())) {
                                    if(isOverlapping(timeBooked.getSessionTime(), session.getTimeSlot())) {
                                        Toast.makeText(AvailableSessionListActivity.this, "This session time overlaps with your existing bookings", Toast.LENGTH_SHORT).show();
                                        return;

                                    }
                                }
                            }
                        }
                        // if the session does not have an overlapping time, continue with booking selection
                        session.setAvailable(false);
                        SessionRequester studentRequester = new SessionRequester(student, session);

                        requestsReference.child(session.getSessionId()).setValue(studentRequester)
                                .addOnSuccessListener(aVoid -> {
                                    sessionPath.child(session.getSessionId()).removeValue();
                                    Toast.makeText(AvailableSessionListActivity.this, "Session request successful", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AvailableSessionListActivity.this, "Could not request session", Toast.LENGTH_SHORT).show();
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

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

    //helper method to make sure Student can't book overlapping sessions
    private boolean isOverlapping(String t1, String t2) {
        String[] t1Intervals = t1.split("-");
        String[] t2Intervals = t2.split("-");

        int s1 = timeToMinutes(t1Intervals[0].trim());
        int e1   = timeToMinutes(t1Intervals[1].trim());
        int s2 = timeToMinutes(t2Intervals[0].trim());
        int e2   = timeToMinutes(t2Intervals[1].trim());
        return s1 < e2 && s2 < e1;
    }
    //helper method for isOverlapping(), converts given time to interger version (in minutes)
    private int timeToMinutes(String time) {
        String[] times = time.split(":");
        return Integer.parseInt(times[0]) * 60 +Integer.parseInt(times[1]);
    }

}