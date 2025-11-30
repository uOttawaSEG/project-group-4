package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AvailableSessionListActivity extends AppCompatActivity {
    LinearLayout availableSessionsContainer;
    LinearLayout cardContainer;
    List<AvailableSession> availableSessions = new ArrayList<>();
    List<AvailableSession> resultedSessions=new ArrayList<>();
    private DatabaseReference tutorPath;
    private DatabaseReference sessionPath;
    Button toDash;
    Student student;
    String role;
    SearchView sessionSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessions_list_fancy);

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
        sessionSearchView=findViewById(R.id.searchView);

        if ("Tutor".equalsIgnoreCase(role)){
            sessionSearchView.setVisibility(View.GONE);
        }

        //linking up firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tutorPath = database.getReference("tutors");
        sessionPath = database.getReference("sessions");

        setupSessionListener();
        setupSearchListener();
    }

    private void setupSearchListener() {
        sessionSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterSessions(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSessions(newText);
                return true;
            }
        });
    }

    private void filterSessions(String text){
        resultedSessions.clear();
        if(text.isEmpty()){
            resultedSessions.addAll(availableSessions);
        }else{
            text=text.toLowerCase().trim();
            for (AvailableSession session: availableSessions){
                String temp=session.getTutorCourses().toLowerCase();
                String[] courses=temp.replace("[", "").replace("]", "").split(", ");
                for(String course: courses){
                    if (course.trim().startsWith(text)){
                        resultedSessions.add(session);
                        break;
                    }
                }

            }
        }
        displaySessions();
    }

    private void setupSessionListener() {
        String tutorID = getIntent().getStringExtra("TUTOR_ID");
        Query sessionTutor;

        if(role.equalsIgnoreCase("Tutor") && tutorID !=null) {
            sessionTutor = sessionPath.orderByChild("tutorId").equalTo(tutorID);
        } else {
            sessionTutor = sessionPath;
        }
        sessionTutor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                availableSessions.clear();
                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                    AvailableSession session = sessionSnapshot.getValue(AvailableSession.class);
                    if (session != null && session.isAvailable()) {
                        availableSessions.add(session);
                    }
                }
                resultedSessions.clear();
                resultedSessions.addAll(availableSessions);
                displaySessions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AvailableSessionListActivity.this, "Error loading sessions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySessions() {
        cardContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (AvailableSession session: resultedSessions) {
            CardView sessionInfoCard = (CardView) inflater.inflate(R.layout.available_session, cardContainer, false);

            TextView tutorName = sessionInfoCard.findViewById(R.id.sessionTutorName);
            TextView tutorCourses = sessionInfoCard.findViewById(R.id.tutorCourses);
            TextView sessionDate = sessionInfoCard.findViewById(R.id.sessionDate);
            TextView sessionTime = sessionInfoCard.findViewById(R.id.sessionTime);
            TextView tutorRating = sessionInfoCard.findViewById(R.id.tutorRating);
            Button cancelSessionBtn = sessionInfoCard.findViewById(R.id.cancelSessionBtn);
            Button studentRegisterSessionBtn = sessionInfoCard.findViewById(R.id.studentRegisterSessionBtn);

            tutorName.setText(session.getTutorName());
            tutorCourses.setText(session.getTutorCourses());
            sessionDate.setText(session.getDate());
            sessionTime.setText(session.getTimeSlot());

            // Fetch and display tutor rating
            tutorPath.child(session.getTutorId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Tutor tutor = snapshot.getValue(Tutor.class);
                        if (tutor != null) {
                            double averageRating = tutor.getRating();
                            int numberOfRatings = tutor.getNumberOfRatings();

                            if (numberOfRatings>1) {
                                String ratingText = String.format("Rating: %.1f/5.0 (%d ratings)", averageRating, numberOfRatings);
                                tutorRating.setText(ratingText);
                            } else if (numberOfRatings==1){
                                String ratingText = String.format("Rating: %.1f/5.0 (%d rating)", averageRating, numberOfRatings);
                                tutorRating.setText(ratingText);
                            } else {
                                tutorRating.setText("Rating: Not yet rated");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tutorRating.setText("Rating: N/A");
                }
            });

            if("Student".equalsIgnoreCase(role)) {
                studentRegisterSessionBtn.setVisibility(View.VISIBLE);
                cancelSessionBtn.setVisibility(View.GONE);
            } else if ("Tutor".equalsIgnoreCase(role)) {
                studentRegisterSessionBtn.setVisibility(View.GONE);
                cancelSessionBtn.setVisibility(View.VISIBLE);
            }

            studentRegisterSessionBtn.setOnClickListener(v -> {
                DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("sessionRequests");
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

        TextView sessionListTitle = findViewById(R.id.sessionListTItle);
        if (resultedSessions.isEmpty()) {
            if (availableSessions.isEmpty()) {
                sessionListTitle.setText("No Available Sessions");
            } else{
                sessionListTitle.setText("No Sessions Match Your Search");
            }
        }else{
            sessionListTitle.setText("Available Tutoring Sessions");
        }
    }

    private boolean isOverlapping(String t1, String t2) {
        String[] t1Intervals = t1.split("-");
        String[] t2Intervals = t2.split("-");

        int s1 = timeToMinutes(t1Intervals[0].trim());
        int e1   = timeToMinutes(t1Intervals[1].trim());
        int s2 = timeToMinutes(t2Intervals[0].trim());
        int e2   = timeToMinutes(t2Intervals[1].trim());
        return s1 < e2 && s2 < e1;
    }

    private int timeToMinutes(String time) {
        String[] times = time.split(":");
        return Integer.parseInt(times[0]) * 60 +Integer.parseInt(times[1]);
    }
}
