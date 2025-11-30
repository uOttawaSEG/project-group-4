package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentInboxActivity extends AppCompatActivity {

    private LinearLayout sessionCardLayout;
    private TabLayout sessionTab;
    private List<SessionRequester> upcomingSessions = new ArrayList<>();
    private List<SessionRequester> notAcceptedSessions = new ArrayList<>();
    private List<SessionRequester> completedSessions = new ArrayList<>();
    private Button inboxToDashButton;
    private DatabaseReference sessionsFirebaseReference;
    private Student dashStudent;
    private String dashStudentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_inbox_fancy);

        dashStudent = (Student)getIntent().getSerializableExtra("CURR_STUDENT");
        dashStudentEmail = dashStudent.getEmail();

        inboxToDashButton = findViewById(R.id.studentInboxToDash);
        inboxToDashButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentInboxActivity.this, DashboardActivity.class);
            intent.putExtra("USER_ROLE", "Student");
            intent.putExtra("CURR_STUDENT", dashStudent);
            startActivity(intent);
        });

        sessionCardLayout = findViewById(R.id.studentCardLayout);
        sessionTab = findViewById(R.id.tabLayout);
        sessionsFirebaseReference = FirebaseDatabase.getInstance().getReference("sessionRequests");

        Query sessionsQuery = sessionsFirebaseReference.orderByChild("studentEmail").equalTo(dashStudentEmail);
        sessionsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                upcomingSessions.clear();
                notAcceptedSessions.clear();
                completedSessions.clear();

                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                    SessionRequester session = sessionSnapshot.getValue(SessionRequester.class);
                    if (session != null) {
                        if (session.getSessionStatus().equalsIgnoreCase("accepted")) {
                            long sessionMillis = convertTime(session.getSessionDate(), session.getSessionTime().split("-")[0].trim());
                            if (sessionMillis > System.currentTimeMillis()) {
                                upcomingSessions.add(session);
                            } else {
                                completedSessions.add(session);
                            }
                        } else {
                            notAcceptedSessions.add(session);
                        }
                    }
                }
                displaySessions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentInboxActivity.this, "Failed to load sessions.", Toast.LENGTH_SHORT).show();
            }
        });

        sessionTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                displaySessions();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void displaySessions() {
        sessionCardLayout.removeAllViews();
        int selectedTabNum = sessionTab.getSelectedTabPosition();

        List<SessionRequester> displayList;

        if (selectedTabNum == 0) {
            displayList = upcomingSessions;
        } else if (selectedTabNum == 1) {
            displayList = notAcceptedSessions;
        } else {
            displayList = completedSessions;
        }

        for (SessionRequester session : displayList) {
            View card = makeSessionCard(session);
            sessionCardLayout.addView(card);
        }
    }

    private View makeSessionCard(SessionRequester sessionCard) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.student_inbox_card_fancy, sessionCardLayout, false);

        TextView tutorName = cardView.findViewById(R.id.sessionTutorName);
        TextView sessionDate = cardView.findViewById(R.id.studentInboxTime);
        TextView sessionTime = cardView.findViewById(R.id.studentInboxDate);
        TextView sessionStatus = cardView.findViewById(R.id.sessionStatus);
        Button cancelBtn = cardView.findViewById(R.id.studentCancelBtn);
        Button calendarBtn = cardView.findViewById(R.id.addToCalendarBtn);

        tutorName.setText("Tutor Name: " + sessionCard.getTutorName());
        sessionDate.setText("Date: " + sessionCard.getSessionDate());
        sessionTime.setText("Time: " + sessionCard.getSessionTime());
        sessionStatus.setText("Status: " + sessionCard.getSessionStatus().toUpperCase());

        long sessionMillis = convertTime(sessionCard.getSessionDate(), sessionCard.getSessionTime().split("-")[0].trim());
        boolean isCompleted = sessionCard.getSessionStatus().equalsIgnoreCase("accepted") && sessionMillis <= System.currentTimeMillis();
        boolean isUpcoming = sessionCard.getSessionStatus().equalsIgnoreCase("accepted") && sessionMillis > System.currentTimeMillis();

        if (isCompleted) {
            cancelBtn.setText("Rate");
            if (sessionCard.isRated()) {
                cancelBtn.setText("Rated");
                cancelBtn.setEnabled(false);
            } else {
                cancelBtn.setOnClickListener(v -> showRatingDialog(sessionCard));
            }
        } else if (isUpcoming) {
            calendarBtn.setVisibility(View.VISIBLE);
            calendarBtn.setOnClickListener(v -> addEventToCalendar(sessionCard));
            long hrCount = (sessionMillis - System.currentTimeMillis()) / (1000 * 60 * 60);
            if (hrCount < 24) {
                cancelBtn.setEnabled(false);
                cancelBtn.setAlpha(0.4f);
            }
            cancelBtn.setOnClickListener(v -> {
                sessionsFirebaseReference.child(sessionCard.getSessionId()).child("sessionStatus").setValue("cancelled");
                Toast.makeText(StudentInboxActivity.this, "Session cancelled", Toast.LENGTH_SHORT).show();
            });
        } else { // Not accepted yet
            if(sessionCard.getSessionStatus().equals("rejected") || sessionCard.getSessionStatus().equals("cancelled")) {
                cancelBtn.setVisibility(View.GONE);
            }
            cancelBtn.setOnClickListener(v -> {
                sessionsFirebaseReference.child(sessionCard.getSessionId()).child("sessionStatus").setValue("cancelled");
                Toast.makeText(StudentInboxActivity.this, "Session cancelled", Toast.LENGTH_SHORT).show();
            });
        }

        return cardView;
    }

    private void addEventToCalendar(SessionRequester session) {
        String[] timeParts = session.getSessionTime().split("-");
        long beginTimeMillis = convertTime(session.getSessionDate(), timeParts[0].trim());
        long endTimeMillis = convertTime(session.getSessionDate(), timeParts[1].trim());

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTimeMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMillis)
                .putExtra(CalendarContract.Events.TITLE, "Tutoring Session with " + session.getTutorName())
                .putExtra(CalendarContract.Events.DESCRIPTION, "Tutoring session for course: [Course Name]"); // Consider adding course if available

        startActivity(intent);
    }

    private void showRatingDialog(final SessionRequester session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);
        builder.setView(dialogView).setTitle("Rate Tutor: " + session.getTutorName());

        final RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            float rating = ratingBar.getRating();
            if (rating > 0) {
                updateTutorRating(session, rating);
            } else {
                Toast.makeText(StudentInboxActivity.this, "Please select a rating", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void updateTutorRating(final SessionRequester session, final float newRating) {
        DatabaseReference tutorRef = FirebaseDatabase.getInstance().getReference("tutors").child(session.getTutorId());

        tutorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Tutor tutor = dataSnapshot.getValue(Tutor.class);
                    if (tutor != null) {
                        double currentRating = tutor.getRating();
                        int numberOfRatings = tutor.getNumberOfRatings();
                        double newAverageRating = ((currentRating * numberOfRatings) + newRating) / (numberOfRatings + 1);

                        dataSnapshot.getRef().child("rating").setValue(newAverageRating);
                        dataSnapshot.getRef().child("numberOfRatings").setValue(numberOfRatings + 1);
                        sessionsFirebaseReference.child(session.getSessionId()).child("rated").setValue(true);

                        Toast.makeText(StudentInboxActivity.this, "Tutor rated successfully!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StudentInboxActivity.this, "Could not find tutor to rate.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentInboxActivity.this, "Failed to update rating.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long convertTime(String date, String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date dateObj = format.parse(date + " " + time.trim());
            return dateObj.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
