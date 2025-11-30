package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CalendarViewActivity extends AppCompatActivity {
    CalendarView calendarView;
    Button selectDateBtn;
    long dateSelected=0;
    Button toDash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_viewfancy);

        calendarView =findViewById(R.id.calendarView);
        dateSelected = calendarView.getDate(); //initial date will just be the day the user is on the app

        //setting min date manually (added this for debugging)
        Calendar minDate = Calendar.getInstance();
        minDate.set(2025, Calendar.NOVEMBER, 4);
        calendarView.setMinDate(minDate.getTimeInMillis());

        selectDateBtn = findViewById(R.id.selectDateBtn);
        toDash = findViewById(R.id.FromCalToDashBtn);


        // store selected date details
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                dateSelected= c.getTimeInMillis();
            }
        });

        selectDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarViewActivity.this, TimeSlotActivity.class);
                intent.putExtra("SELECTED_DATE", dateSelected);
                intent.putExtra("USER_ROLE", "Tutor");
                startActivity(intent);

            }
        });

        toDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarViewActivity.this, DashboardActivity.class);
                intent.putExtra("USER_ROLE", "Tutor");
                startActivity(intent);

            }
        });

    }
}