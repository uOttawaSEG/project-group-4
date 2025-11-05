package com.example.logintest.domain;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
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
    List<AvailableSession> availableSessions = new ArrayList<>();
    private DatabaseReference tutorPath;
    private DatabaseReference sessionPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessions_list);

        availableSessionsContainer = findViewById(R.id.availableSessionsContainer);

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

    private void displaySessions() {
        // Refresh by clearing the cards
        availableSessionsContainer.removeAllViews();

        // Then reload/make each session into a visible card
        for (AvailableSession session: availableSessions) {
            CardView sessionInfoCard = makeCard(session);
            availableSessionsContainer.addView(sessionInfoCard);
        }

        // Show message if no sessions
        if (availableSessions.isEmpty()) {
            TextView noSessionsText = new TextView(this);
            noSessionsText.setText("No available sessions");
            noSessionsText.setTextSize(16);
            noSessionsText.setGravity(Gravity.CENTER);
            availableSessionsContainer.addView(noSessionsText);
        }
    }

    // Helper method to set up a card for the session
    private CardView makeCard(AvailableSession session) {

        LayoutInflater inflater = LayoutInflater.from(this);
        CardView sessionCard = (CardView) inflater.inflate(R.layout.available_session, null);

        TextView tutorName = sessionCard.findViewById(R.id.sessionTutorName);
        TextView tutorCourses = sessionCard.findViewById(R.id.tutorCourses);
        TextView sessionDate = sessionCard.findViewById(R.id.sessionDate);
        TextView sessionTime = sessionCard.findViewById(R.id.sessionTime);
        Button cancelSessionBtn = sessionCard.findViewById(R.id.cancelSessionBtn);
        Button studentRegisterSessionBtn = sessionCard.findViewById(R.id.studentRegisterSessionBtn);

        tutorName.setText(session.getTutorName());
        tutorCourses.setText(session.getTutorCourses());
        sessionDate.setText(session.getDate());
        sessionTime.setText(session.getTimeSlot());

        studentRegisterSessionBtn.setOnClickListener(v -> {
            // sessions is not available anymore
            session.setAvailable(false);
            sessionPath.child(session.getSessionId()).setValue(session)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AvailableSessionListActivity.this, "Session request successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AvailableSessionListActivity.this, "Session request failed", Toast.LENGTH_SHORT).show();
                    });
        });

        //cancel the session (to do: make it only available to tutors)
        cancelSessionBtn.setOnClickListener(v -> {
            sessionPath.child(session.getSessionId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AvailableSessionListActivity.this, "Session cancelled", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AvailableSessionListActivity.this, "Failed to cancel session", Toast.LENGTH_SHORT).show();
                    });
        });

        return sessionCard;
    }
}