package com.example.logintest.domain;

import static java.lang.String.format;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class TimeSlot extends AppCompatActivity {
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
        setContentView(R.layout.activity_time_slot);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //setting up access variables
        dateTextView = findViewById(R.id.dateTextView);
        toCal = findViewById(R.id.toCalBtn);
        selectTimeSlotBtn = findViewById(R.id.selectTimeSlotBtn);
        startTimeSpinner = findViewById(R.id.startTimeSpinner);
        endTimeSpinner = findViewById(R.id.endTimeSpinner);
        selectTimeBtn = findViewById(R.id.selectTimeSlotBtn);

        alreadySelectedTimeSlots = new HashSet<>();

        //go back to Calender button
        toCal.setOnClickListener(v -> {
            Intent intent = new Intent(TimeSlot.this, CalendarViewActivity.class);
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

        // spinenrs need these adapters
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(adapter);
        endTimeSpinner.setAdapter(adapter);
    }

    //helper method to ensure the the time slot makes sense
    // 1) the end time cannot be before the start time
    // 2) the start time and end time cannot be the same
    // 3) the tutor cannot choose a time slot that has already been selected
//    private void validateTimeSlot() {
//        String start = (String) startTimeSpinner.getSelectedItem();
//        String end = (String) endTimeSpinner.getSelectedItem();
//        String interval = start + "-" + end;
//
//        // end shouldn't be before start time
//        if (start.compareTo(end) > 0) {
//            Toast.makeText(this, "The end time cannot be before start time", Toast.LENGTH_SHORT).show();
//            return;
//        }
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
//        // check if the time slot overlaps with previously selected time slots
//        for (String timeInterval : alreadySelectedTimeSlots) {
//            String[] allIntervals = timeInterval.split("-");
//            String alreadySelectedStart = allIntervals[0];
//            String alreadySelectedEnd = allIntervals[1];
//
//            if (start.compareTo(alreadySelectedEnd) < 0 && start.compareTo(alreadySelectedStart) > 0) {
//                Toast.makeText(this, "This time slot overlaps with previously selected time slots", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//        Toast.makeText(this, "Time slot succesfully selected!", Toast.LENGTH_SHORT).show();
//
//        // if everything is valid -> create a session with the time slot
//
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        String userId = currentUser.getUid();
//        databaseReference.child("tutors").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Tutor tutor = dataSnapshot.getValue(Tutor.class);
//                if (tutor != null) {
//                    alreadySelectedTimeSlots.add(interval);
//
//                    String sessionDate = dateTextView.getText().toString();
//                    String sessionTime = start + "-" + end;
//
//                    Intent intent = new Intent(TimeSlot.this, AvailableSessionListActivity.class);
//                    intent.putExtra("tutor_name", tutor.getFirstName() + " " + tutor.getLastName());
//                    intent.putExtra("tutor_courses", tutor.getCoursesOffered().toString());
//                    intent.putExtra("session_date", sessionDate);
//                    intent.putExtra("session_time", sessionTime);
//                    startActivity(intent);
//
//                    Toast.makeText(TimeSlot.this, "Session succesfully created!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(TimeSlot.this, "Tutor not found", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(TimeSlot.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void validateTimeSlot() {
        String start = (String) startTimeSpinner.getSelectedItem();
        String end = (String) endTimeSpinner.getSelectedItem();
        String interval = start + "-" + end;

        // end shouldn't be before start time
        if (start.compareTo(end) > 0) {
            Toast.makeText(this, "The end time cannot be before start time", Toast.LENGTH_SHORT).show();
            return;
        }
        // start time cannot equal end time
        if (start.equals(end)) {
            Toast.makeText(this, "The start time and end time cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if the time slot has already been selected
        if (alreadySelectedTimeSlots.contains(interval)) {
            Toast.makeText(this, "This time slot has already been selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Overlaps(start, end, alreadySelectedTimeSlots)) {
            Toast.makeText(this, "This time slot overlaps with previously selected time slots", Toast.LENGTH_SHORT).show();
            return;
        }


        // Get the date from the intent
        Intent intent = getIntent();
        long date = intent.getLongExtra("SELECTED_DATE", 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString= dateFormat.format(new Date(date));

        FirebaseUser sessionTutor = mAuth.getCurrentUser();
        String userId = sessionTutor.getUid();

        databaseReference.child("tutors").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tutor tutor = dataSnapshot.getValue(Tutor.class);
                if (tutor != null) {
                    alreadySelectedTimeSlots.add(interval);

                    //adding to firebase
                    createAndUploadSession(tutor, dateString, interval);

                } else {
                    Toast.makeText(TimeSlot.this, "Tutor not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TimeSlot.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAndUploadSession(Tutor tutor, String date, String timeSlot) {
        AvailableSession session = new AvailableSession(tutor, date, timeSlot);

        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
        sessionsRef.child(session.getSessionId()).setValue(session)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TimeSlot.this, "Session successfully created!", Toast.LENGTH_SHORT).show();
                    // go back to sessions list
                    Intent intent = new Intent(TimeSlot.this, AvailableSessionListActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TimeSlot.this, "Could not create session: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
