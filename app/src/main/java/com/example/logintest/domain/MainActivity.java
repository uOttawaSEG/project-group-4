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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
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

    // helper method for validateAndLoginUser() to display role on Dashboard/Welcome screen
//    public void goToDashboardWithRole(String userRole) {
//        Intent intent = new Intent(this, DashboardActivity.class);
//        intent.putExtra("USER_ROLE", userRole);
//        startActivity(intent);
//        finish();
//    }

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

    boolean checkForErrors() {
        //Get what the user typed and removed extra spaces
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        boolean everythingOK = true;
        if (usernameInput.isEmpty()) {
            username.setError("Username (email) is required");
            username.requestFocus();
            everythingOK = false;
        }

        //Check password
        if (passwordInput.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            everythingOK = false;
        }
        return everythingOK;
    }

    void validateAndLoginUser() {
        if (!checkForErrors()) {
            Toast.makeText(MainActivity.this, "Login failed, please correct the fields marked in red", Toast.LENGTH_SHORT).show();
            return;
        }
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        //Verify if the user exists
        mAuth.signInWithEmailAndPassword(usernameInput, passwordInput).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // display the user's role on Dashboard/Welcome screen
                        FirebaseUser currUser = mAuth.getCurrentUser();
                        if (currUser != null) {
                            String userID = currUser.getUid();
                            DatabaseReference dbUser = databaseReference;

                            dbUser.child("tutors").child(userID).get().addOnCompleteListener(tutorTask -> {
                                if (tutorTask.isSuccessful() && tutorTask.getResult().exists()) {
                                    //goToDashboardWithRole("Tutor");
                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                    intent.putExtra("USER_ROLE", "Tutor");
                                    startActivity(intent);

                                    finish();
                                } else {
                                    dbUser.child("students").child(userID).get().addOnCompleteListener(studentTask -> {
                                        if (studentTask.isSuccessful() && studentTask.getResult().exists()) {
                                            //goToDashboardWithRole("Student");
                                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                            intent.putExtra("USER_ROLE", "Student");
                                            startActivity(intent);

                                            finish();
                                        } else { //is an admin
                                            dbUser.child("admins").child(userID).get().addOnCompleteListener(adminTask -> {
                                                if (adminTask.isSuccessful() && adminTask.getResult().exists()) {
                                                    //goToDashboardWithRole("Admin");
                                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                                    intent.putExtra("USER_ROLE", "Admin");
                                                    startActivity(intent);

                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                Log.d("Firebase authentication", "Login successful for:" + usernameInput);
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                //goToDashboard(null);
            } else{
                Log.w("Firebase authentication", "Login failed", task.getException());
                try {
                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    switch (errorCode) {
                        case "ERROR_INVALID_EMAIL":
                            username.setError("Invalid email address");
                            username.requestFocus();
                            Toast.makeText(MainActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                            break;
                        /*
                        It seems that there isn't a specific error code for user not found (email not in DB) or for wrong password,
                        apparently it was for security reasons so the error case below is used instead.
                         */
                        case "ERROR_INVALID_CREDENTIAL":
                            Toast.makeText(MainActivity.this, "Incorrect email or password. If you don't have an account, please register", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Login failed, please try again", Toast.LENGTH_SHORT).show();

                    }
                }
                catch (Exception e){
                    Log.e("Firebase authentication", "An unexpected error occurred", e);
                    Toast.makeText(MainActivity.this, "An unexpected error occurred, try again", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
