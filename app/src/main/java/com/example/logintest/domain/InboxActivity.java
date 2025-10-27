package com.example.logintest.domain;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button; // Import the Button class
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    //private Button refreshButton;
    private Button backDash;
    //private PendingRequestAdapter adapter;
    private List<User> pendingList;
    private DatabaseReference pendingReference;
    //private RecyclerView pendingRecycler;
    private ValueEventListener eventListener;

    // At some point we can also implement a refresh button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_layout);

        backDash = findViewById(R.id.backDash);
        backDash.setOnClickListener(new View.OnClickListener() {
            @Override
            //public void onClick(View v) {setContentView(R.layout.inbox_layout);}
            public void onClick(View v) { finish(); }
        });

        /* The following is for the database connection to the inbox xml
        *  */
        //pendingReference = FirebaseDatabase.getInstance().getReference().child("pending"); // read from pending path in firebase
        pendingList =new ArrayList<>();
        RecyclerView pendingRecycler = findViewById(R.id.pendingRequestsRecyclerView);
        PendingRequestAdapter adapter = new PendingRequestAdapter(this, pendingList);

        pendingRecycler.setLayoutManager(new LinearLayoutManager(this));
        pendingRecycler.setAdapter(adapter);

        pendingReference = FirebaseDatabase.getInstance().getReference().child("pending");

        pendingReference.get().addOnSuccessListener(snapshot -> {
            pendingList.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                // if is a tutor
                if (dataSnapshot.hasChild("coursesOffered") || dataSnapshot.hasChild("highestDegree")) {
                    Log.d("InboxActivity", "Snapshot key: " + dataSnapshot.getKey());
                    Tutor pendingUser = dataSnapshot.getValue(Tutor.class);
                    pendingList.add(pendingUser);
                } else { // then is a student
                    Student pendingUser = dataSnapshot.getValue(Student.class);
                    pendingList.add(pendingUser);
                }
            }
            adapter.notifyItemInserted(pendingList.size() + 1); // refresh RecyclerView
        });
        //  to display pending requests
        // mainly gathers the information of the User who clicked submit on the registration
        // page and then adds their Object to our list of all users who have requested
//        pendingReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                pendingList.clear();
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                    User pendingUser = null;
//                    // if is a tutor
//                    if (dataSnapshot.hasChild("coursesOffered") || dataSnapshot.hasChild("highestDegree")) {
//                        pendingUser = dataSnapshot.getValue(Tutor.class);
//                    } else { // then is a student
//                        pendingUser = dataSnapshot.getValue(Student.class);
//                    }
//                    if (pendingUser != null) {
//                        pendingList.add(pendingUser);
//                    }
//                }
//                //updating RecyclerView adaptor
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("InboxActivity", "Couldn't load user requests", error.toException());
//            }
//        });
    }
}
