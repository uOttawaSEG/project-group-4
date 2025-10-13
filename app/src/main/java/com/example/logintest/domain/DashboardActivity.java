package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
    }
    //Logout button
    public void goToLogOut(View view) {
        Intent intent = new Intent(this, LogOutPageActivity.class);
        startActivity(intent);
    }

}