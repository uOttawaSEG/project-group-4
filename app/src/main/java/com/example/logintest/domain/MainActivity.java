package com.example.logintest.domain;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //Database services
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    //UI elements
    EditText username;
    EditText password;
    Button loginButton;


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

        // Initialize  both firebase services
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //Link UI elements for login
        username = findViewById(R.id.user_name);
        password = findViewById(R.id.user_password);
        loginButton = findViewById(R.id.loginButton);
        //When user clicks login button
        loginButton.setOnClickListener (view -> {
            validateAndLoginUser();
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


    void validateAndLoginUser() {
        //Get what the user typed and removed extra spaces
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();


        //Check username
        if (usernameInput.isEmpty()) {
            username.setError("Username (email) is required");
            username.requestFocus();
            return;
        }
        if (!usernameInput.contains("@") || !usernameInput.contains(".")) {
            username.setError("Please enter a valid email address");
            return;
        }

        //Check password
        if (passwordInput.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        //Verify if the user exists
        mAuth.signInWithEmailAndPassword(usernameInput, passwordInput).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase authentication", "Login successful for:" + username);
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                goToDashboard(null);
            }
            else {
                Log.w("Firebase authentification", "Login failed", task.getException());
                Toast.makeText(MainActivity.this, "Not an account, please register. If you already have an account, try again.", Toast.LENGTH_LONG).show();
            }
        });
    }



}
