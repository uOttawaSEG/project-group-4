package com.example.logintest.domain;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loginorregister);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayout4), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Login button
    public void goToDashboard(View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    // Tutor Register button
    public void goToTutorRegistration(View view) {
        Intent intent = new Intent(MainActivity.this, TutorInfoActivity.class);
        startActivity(intent);
    }

    // Student Register button
    public void goToStudentRegistration(View view) {
        Intent intent = new Intent(this, StudentInfoActivity.class);
        startActivity(intent);
    }

    //Admin login button
    public void goToAdminLogin(View view) {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    //Log out page
    public void goToLogOut(View view){
        Intent intent = new Intent(this, LogOutPageActivity.class);
        startActivity(intent);
    }

    public void goToHome(View view){
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }
}
