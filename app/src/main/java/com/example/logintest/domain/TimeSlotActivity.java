package com.example.logintest.domain;

import static java.lang.String.format;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TimeSlotActivity extends AppCompatActivity {
    TextView dateTextView;
    Button toCal;
    Button selectTimeSlotBtn;
    Spinner startTimeSpinner;
    Spinner endTimeSpinner;
    Button selectTimeBtn;
    Set<String> alreadySelectedTimeSlots;
    long selectedDate;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;



    @Override

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot_fancy);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //setting up access variables
        dateTextView = findViewById(R.id.dateTextView);
        toCal = findViewById(R.id.toCalBtn);
        selectTimeSlotBtn = findViewById(R.id.selectTimeSlotBtn);
        startTimeSpinner = findViewById(R.id.startTimeSpinner);
        endTimeSpinner = findViewById(R.id.endTimeSpinner);
        selectTimeBtn = findViewById(R.id.selectTimeSlotBtn);

        //alreadySelectedTimeSlots = new HashSet<>();

        //go back to Calender button
        toCal.setOnClickListener(v -> {
            Intent intent = new Intent(TimeSlotActivity.this, CalendarViewActivity.class);
            startActivity(intent);
        });

        // getting the date we passed with intent
        Intent intent = getIntent();
        long date = intent.getLongExtra("SELECTED_DATE", 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dateString = dateFormat.format(new Date(date));

        dateTextView.setText("The Date you Selected is: " + dateString);

        //setting up the spinners to be able to select the times
        setTimes();
        // validating the chosen time slot
        selectTimeBtn.setOnClickListener(v -> {
            validateTimeSlot();
        });
    }

    //helper method to set up the times (in 30 minute intervals) for the drop down(Spinner) selection.
    // follows the 24 hour clock
    private void setTimes() {
        List<String> times = new ArrayList<>();

        //adding all times from 0-23, including the halves (the 30min intervals)
        for (int hour = 0; hour < 24; hour++) {
            String t;
            if(hour< 10) {
                t = "0" + hour;
            } else {
                t = String.valueOf(hour);
            }
            times.add(t+ ":00"); //hr:00 mins
            times.add(t + ":30"); //andhr:30 minutes
        }

        // spinners need these adapters
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, times); //No longer using the default android.R.layout.simple_spinner_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(adapter);
        endTimeSpinner.setAdapter(adapter);
    }



    private void validateTimeSlot() {
        String start = (String) startTimeSpinner.getSelectedItem();
        String end = (String) endTimeSpinner.getSelectedItem();
        String interval = start + "-" + end;

        // end shouldn't be before start time
        if (start.compareTo(end) > 0) {
            Toast.makeText(this, "The end time cannot be before start time", Toast.LENGTH_SHORT).show();
            return;
        }
//        // start time cannot equal end time
//        if (start.equals(end)) {
//            Toast.makeText(this, "The start time and end time cannot be the same", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        // check if the time slot has already been selected
//        if (alreadySelectedTimeSlots.contains(interval)) {
//            Toast.makeText(this, "This time slot has already been selected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if(Overlaps(start, end, alreadySelectedTimeSlots)) {
//            Toast.makeText(this, "This time slot overlaps with previously selected time slots", Toast.LENGTH_SHORT).show();
//            return;
//        }


        // Get the date from the intent
        Intent intent = getIntent();
        selectedDate= intent.getLongExtra("SELECTED_DATE", 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString= dateFormat.format(new Date(selectedDate));

        FirebaseUser sessionTutor = mAuth.getCurrentUser();
        String userId = sessionTutor.getUid();
        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");

        sessionsRef.orderByChild("tutorId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> existingTimeSlots = new HashSet<>(); // keeping track of time sets

                // adding all session intervals into the set
                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    AvailableSession session = sessionSnapshot.getValue(AvailableSession.class);
                    if (session != null && dateString.equals(session.getDate())) {
                        existingTimeSlots.add(session.getTimeSlot());
                    }
                }
                // check if any of times in the set overlap or are equal
                if (Overlaps(start, end, existingTimeSlots)) {
                    Toast.makeText(TimeSlotActivity.this, "This time slot overlaps with another session.", Toast.LENGTH_LONG).show();
                    return;
                }

                databaseReference.child("tutors").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot tutorSnapshot) {
                        Tutor tutor = tutorSnapshot.getValue(Tutor.class);

                        if (tutor != null) {
                            String timeInterval = start + "-" + end;
                            createAndUploadSession(tutor, dateString, timeInterval);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(TimeSlotActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TimeSlotActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAndUploadSession(Tutor tutor, String date, String timeSlot) {
        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        String currentTutorId=currentUser.getUid();

        AvailableSession session = new AvailableSession(currentTutorId, tutor, date, timeSlot);

        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
        sessionsRef.child(session.getSessionId()).setValue(session)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TimeSlotActivity.this, "Session successfully created!", Toast.LENGTH_SHORT).show();
                    // go back to sessions list
                    Intent intent = new Intent(TimeSlotActivity.this, AvailableSessionListActivity.class);
                    intent.putExtra("USER_ROLE", "Tutor");
                    intent.putExtra("TUTOR_ID", currentTutorId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TimeSlotActivity.this, "Could not create session: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    //helper method to check if time slot overlaps with other sessions
    private boolean Overlaps(String start, String end, Set<String> timeSlots) {
        int startMins = toMinutes(start);
        int endMins = toMinutes(end);

        for(String time: timeSlots) {
            String[] allTimeIntervals = time.split("-");
            int s = toMinutes(allTimeIntervals[0]);
            int e = toMinutes(allTimeIntervals[1]);

            if(startMins < e && endMins > s) {
                return true;
            }
        }
        return false;
    }

    //helper method to convert time string into integer of mintues
    private int toMinutes(String time) {
        String [] times = time.split(":");

        int hr = Integer.parseInt(times[0]);
        int min = Integer.parseInt(times[1]);

        return hr * 60 + min;
    }

} // end of TimeSlot.java
