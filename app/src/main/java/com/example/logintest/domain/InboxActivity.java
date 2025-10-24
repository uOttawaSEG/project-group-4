package com.example.logintest.domain;

import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Import the Button class

import androidx.appcompat.app.AppCompatActivity;

public class InboxActivity extends AppCompatActivity {

    //private Button refreshButton;
    private Button backDash;

    // At some point we can also implement a refresh button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.inbox_layout);

        backDash = findViewById(R.id.backDash);
        backDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.dashboard);
            }
        });
    }
}
