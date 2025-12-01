package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CalendarViewActivity extends AppCompatActivity {
    CalendarView calendarView;
    Button selectDateBtn;
    long dateSelected = 0;
    Button toDash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_viewfancy);

        calendarView = findViewById(R.id.calendarView);
        selectDateBtn = findViewById(R.id.selectDateBtn);
        toDash = findViewById(R.id.FromCalToDashBtn);

        // get the current date and update automatically
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        calendarView.setMinDate(today.getTimeInMillis());
        // update daily
        dateSelected = calendarView.getDate();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                dateSelected = c.getTimeInMillis();
                }
        });

        selectDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
            // erropr message if its not allowed
                if (dateSelected < today.getTimeInMillis()) {
                    Toast.makeText(CalendarViewActivity.this, "Cannot select a date in the past.", Toast.LENGTH_SHORT).show();
                    return;
                }


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