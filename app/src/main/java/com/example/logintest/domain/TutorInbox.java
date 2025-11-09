package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TutorInbox extends AppCompatActivity {

    private LinearLayout sessionCardLayout;
    private TabLayout sessionTab;
    private List<SessionRequester> pastSessions = new ArrayList<>();
    private List<SessionRequester> upcomingSessions = new ArrayList<>();
    private List<SessionRequester> pendingSessions = new ArrayList<>();
    //to better reflect the session status
    private List<SessionRequester> rejectedSessions = new ArrayList<>();
    private Button inboxToDashButton;
    private DatabaseReference sessionsFirebaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_inbox);

        ToggleButton toggle = findViewById(R.id.toggleBtn);

        // go back to dashboard button
        inboxToDashButton = findViewById(R.id.tutorInboxToDashBtn);
        inboxToDashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorInbox.this, DashboardActivity.class);
                intent.putExtra("USER_ROLE", "Tutor");
                startActivity(intent);
            }
        });

        sessionCardLayout = findViewById(R.id.tutorCardLayout);
        sessionTab = findViewById(R.id.tabLayout);
        sessionsFirebaseReference = FirebaseDatabase.getInstance().getReference("sessionRequests");

        //linking firebase
        sessionsFirebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pastSessions.clear();
                upcomingSessions.clear();
                pendingSessions.clear();
                rejectedSessions.clear();

                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {
                    SessionRequester session = sessionSnapshot.getValue(SessionRequester.class);
                    if (session != null) {
                        if (session.getSessionStatus().equalsIgnoreCase("pending")) {
                            pendingSessions.add(session);
                        }
                        else if(session.getSessionStatus().equalsIgnoreCase("rejected")){
                            rejectedSessions.add(session);
                        }
                        else {
                            long sessionMillis = convertTime(session.getSessionDate(), session.getSessionTime());
                            long currentTime = System.currentTimeMillis();

                            if (sessionMillis > currentTime) {
                                upcomingSessions.add(session);
                            } else { // the request time and date is before current time and date
                                pastSessions.add(session);
                            }
                        }
                    }
                    /*
                    //display past sessions as default
                    sessionCardLayout.removeAllViews();

                    for (SessionRequester ses: pastSessions) {
                        View pastCard = makeSessionCard(ses, "Past");
                        sessionCardLayout.addView(pastCard);
                    }
                    */
                }
                //display past sessions as default
                sessionCardLayout.removeAllViews();

                for (SessionRequester ses: pastSessions) {
                    View pastCard = makeSessionCard(ses, "Past");
                    sessionCardLayout.addView(pastCard);
                }
            }//end of onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //had to implement this to use ValueEventListener
            }
        });


        //toggle button to set automatic session acceptance
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                //approve all pending sessions
                if (pendingSessions.isEmpty()) {
                    Toast.makeText(TutorInbox.this, "There are no sessions", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(SessionRequester session: pendingSessions) {
                    sessionsFirebaseReference.child(session.getSessionId()).child("sessionStatus").setValue("accepted");
                    upcomingSessions.add(session);
                }
                pendingSessions.clear();
                Toast.makeText(TutorInbox.this, "Automatic session approval enabled", Toast.LENGTH_SHORT).show();

                TabLayout.Tab selectedTab = sessionTab.getTabAt(sessionTab.getSelectedTabPosition());
                if (selectedTab != null && "Pending".equalsIgnoreCase(selectedTab.getText().toString())) {
                    sessionCardLayout.removeAllViews();
                    for(SessionRequester session: pendingSessions) {
                        View pendingCard = makeSessionCard(session, "Pending");
                        sessionCardLayout.addView(pendingCard);
                    }
                }

            }
        });

        //tab functionality
        sessionTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()== 0) {
                    //display past sessions
                    sessionCardLayout.removeAllViews();

                    for (SessionRequester session: pastSessions) {
                        View pastCard = makeSessionCard(session, "Past");
                        sessionCardLayout.addView(pastCard);
                    }
                } else if (tab.getPosition()==1) {
                    //display upcoming sessions
                    sessionCardLayout.removeAllViews();
                    for (SessionRequester session: upcomingSessions) {
                        View upcomingCard = makeSessionCard(session, "Upcoming");
                        sessionCardLayout.addView(upcomingCard);

                    }

                } else if (tab.getPosition()== 2) {
                    //display pending sessions
                    sessionCardLayout.removeAllViews();
                    for (SessionRequester session: pendingSessions) {
                        View pendingCard = makeSessionCard(session, "Pending");
                        sessionCardLayout.addView(pendingCard);
                    }

                }
                else if (tab.getPosition()== 3) {
                    //display rejected sessions
                    sessionCardLayout.removeAllViews();
                    for (SessionRequester session: rejectedSessions) {
                        View rejectedCard = makeSessionCard(session, "Rejected");
                        sessionCardLayout.addView(rejectedCard);
                    }

                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


    }

// helper method to create the session cards
    private View  makeSessionCard (SessionRequester studentCard, String status) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.tutor_view_sessions, sessionCardLayout, false);

        TextView studentName = cardView.findViewById(R.id.sessionStudentName);
        TextView studentEmail = cardView.findViewById(R.id.sessionStudentEmail);
        TextView studentPhone= cardView.findViewById(R.id.sessionStudentPhone);
        TextView studentProgram = cardView.findViewById(R.id.sessionStudentProgram);
        TextView sessionDate = cardView.findViewById(R.id.tutorViewTime);
        TextView sessionTime = cardView.findViewById(R.id.tutorViewDate);
        TextView sessionStatus = cardView.findViewById(R.id.sessionStatus);


        studentName.setText("Name: " + studentCard.getStudentName());
        studentEmail.setText("Email: " + studentCard.getStudentEmail());
        studentPhone.setText("Phone #: " + studentCard.getStudentPhone());
        studentProgram.setText("Program: " + studentCard.getStudentProgram());
        sessionDate.setText("Date: " + studentCard.getSessionDate());
        sessionTime.setText("Time: " + studentCard.getSessionTime());

        sessionStatus.setText("Status: PENDING");

        Button acceptBtn = cardView.findViewById(R.id.tutorAcceptBtn);
        Button rejectBtn = cardView.findViewById(R.id.tutorRejectBtn);
        Button cancelBtn = cardView.findViewById(R.id.cancelBtn);

        if (status.equals("Upcoming")) {
            cancelBtn.setVisibility(View.VISIBLE);
            acceptBtn.setVisibility(View.GONE);
            rejectBtn.setVisibility(View.GONE);
            sessionStatus.setText("Status: ACCEPTED");
        }
        if (status.equals("Past")) {
            cancelBtn.setVisibility(View.GONE);
            acceptBtn.setVisibility(View.GONE);
            rejectBtn.setVisibility(View.GONE);
            sessionStatus.setVisibility(View.GONE);
        }
        if (status.equals("Rejected")) {
            cancelBtn.setVisibility(View.GONE);
            acceptBtn.setVisibility(View.GONE);
            rejectBtn.setVisibility(View.GONE);
            sessionStatus.setText("Status: REJECTED");
        }

        acceptBtn.setOnClickListener(v -> {
            upcomingSessions.add(studentCard);
            pendingSessions.remove(studentCard);
            sessionsFirebaseReference.child(studentCard.getSessionId()).child("sessionStatus").setValue("accepted");
            Toast.makeText(TutorInbox.this, "Session accepted", Toast.LENGTH_SHORT).show();
        });
        rejectBtn.setOnClickListener(v -> {
            pendingSessions.remove(studentCard);
            sessionsFirebaseReference.child(studentCard.getSessionId()).child("sessionStatus").setValue("rejected");
            Toast.makeText(TutorInbox.this, "Session rejected", Toast.LENGTH_SHORT).show();
        });
        cancelBtn.setOnClickListener(v -> {
            upcomingSessions.remove(studentCard);
            sessionsFirebaseReference.child(studentCard.getSessionId()).child("sessionStatus").setValue("rejected");
            //sessionsFirebaseReference.child(studentCard.getSessionId()).removeValue();
            Toast.makeText(TutorInbox.this, "Session cancelled", Toast.LENGTH_SHORT).show();
        });
        return cardView;
    }

    // helper method to convert time, used to check if a session time and date is before the current time and date
    private long convertTime(String date, String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date dateObj = format.parse(date + " " + time);
            return dateObj.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
