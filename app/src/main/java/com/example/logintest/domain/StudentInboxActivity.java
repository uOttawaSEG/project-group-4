package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentInboxActivity extends AppCompatActivity {

    private LinearLayout sessionCardLayout;
    private TabLayout sessionTab;
    private List<SessionRequester> upcomingSessions = new ArrayList<>();
    private List<SessionRequester> notAcceptedSessions = new ArrayList<>();
    //to better reflect the session status
    private Button inboxToDashButton;
    private DatabaseReference sessionsFirebaseReference;
    private Student dashStudent;
    private String dashStudentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_inbox);

        dashStudent = (Student)getIntent().getSerializableExtra("CURR_STUDENT");
        dashStudentEmail = dashStudent.getEmail();

        // go back to dashboard button
        inboxToDashButton = findViewById(R.id.studentInboxToDash);
        inboxToDashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentInboxActivity.this, DashboardActivity.class);
                intent.putExtra("USER_ROLE", "Student");
                intent.putExtra("CURR_STUDENT", dashStudent);
                startActivity(intent);
            }
        });

        sessionCardLayout = findViewById(R.id.studentCardLayout);
        sessionTab = findViewById(R.id.tabLayout);
        sessionsFirebaseReference = FirebaseDatabase.getInstance().getReference("sessionRequests");

        // using firebase Queries just cause' its easier to read data than lists
        Query sessionsQuery =sessionsFirebaseReference.orderByChild("studentEmail").equalTo(dashStudentEmail);

        //linking firebase
        sessionsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                upcomingSessions.clear();
                notAcceptedSessions.clear();

                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                    SessionRequester session = sessionSnapshot.getValue(SessionRequester.class);
                    if (session != null) {
                        if (session.getSessionStatus().equalsIgnoreCase("accepted")) {
                             //status=="accepted"
                            long sessionMillis = convertTime(session.getSessionDate(), session.getSessionTime().split("-")[0].trim());
                            long currentTime = System.currentTimeMillis();

                            if (sessionMillis > currentTime) {
                                upcomingSessions.add(session);
                            }
                        } else {
                            notAcceptedSessions.add(session);
                        }
                    }
                }
                //display past sessions as default
                displaySessions();
                //sessionCardLayout.removeAllViews();
            }//end of onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //had to implement this to use ValueEventListener
            }
        });


        //tab functionality
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



    //helepr method to load all the cards in the inbox
    private void displaySessions() {
        sessionCardLayout.removeAllViews();
        int selectedTabNum = sessionTab.getSelectedTabPosition();

        List<SessionRequester> displayList = new ArrayList<>();

        if (selectedTabNum == 0) {
            displayList = upcomingSessions;
        } else if (selectedTabNum== 1) {
            displayList = notAcceptedSessions;
        }

        // display cards according to status
        for (SessionRequester session : displayList) {
            View card = makeSessionCard(session);
            sessionCardLayout.addView(card);
        }
    }


    // helper method to create the session cards
    private View  makeSessionCard (SessionRequester sessionCard) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.student_inbox_card, sessionCardLayout, false);

        TextView tutorName = cardView.findViewById(R.id.sessionTutorName);
        TextView sessionDate = cardView.findViewById(R.id.studentInboxTime);
        TextView sessionTime = cardView.findViewById(R.id.studentInboxDate);
        TextView sessionStatus = cardView.findViewById(R.id.sessionStatus);

        tutorName.setText("Tutor Name: " + sessionCard.getTutorName());
        sessionDate.setText("Date: " + sessionCard.getSessionDate());
        sessionTime.setText("Time: " + sessionCard.getSessionTime());
        sessionStatus.setText("Status: " + sessionCard.getSessionStatus().toUpperCase());

        Button cancelBtn = cardView.findViewById(R.id.studentCancelBtn);

        //changing Cancel button visibility depending on status
        if (sessionCard.getSessionStatus().equals("rejected") || sessionCard.getSessionStatus().equals("cancelled")) {
            cancelBtn.setVisibility(View.GONE);
        }
        // user can't cancel a session 24hrs before it is scheduled
        if(sessionCard.getSessionStatus().equals("accepted")) {
            long sessionMillisConversion = convertTime(sessionCard.getSessionDate(), sessionCard.getSessionTime().split("-")[0].trim());
            long currentMillisTime = System.currentTimeMillis();
            long hrCount =(sessionMillisConversion - currentMillisTime)/(1000*60*60);

            if (hrCount < 24) {
                cancelBtn.setEnabled(false);
                cancelBtn.setAlpha(0.4f);
                cancelBtn.setText("Cannot cancel 24hrs before session");
            }
        }

        // have to make sure student can't cancel 24hrs before the session is scheduled
        cancelBtn.setOnClickListener(v -> {
            sessionsFirebaseReference.child(sessionCard.getSessionId()).child("sessionStatus").setValue("cancelled");
            //sessionsFirebaseReference.child(studentCard.getSessionId()).removeValue();
            Toast.makeText(StudentInboxActivity.this, "Session cancelled", Toast.LENGTH_SHORT).show();
        });
        return cardView;
    }

    // helper method to convert time, used to check if a session time and date is before the current time and date
    private long convertTime(String date, String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date dateObj = format.parse(date + " " + time.trim());
            return dateObj.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
