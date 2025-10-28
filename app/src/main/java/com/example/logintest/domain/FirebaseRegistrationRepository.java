package com.example.logintest.repository;

import androidx.annotation.NonNull;

import com.example.logintest.domain.pendingUser;
import com.example.logintest.domain.Student;
import com.example.logintest.domain.Tutor;
import com.example.logintest.domain.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRegistrationRepository {
    private DatabaseReference databaseReference;

    public FirebaseRegistrationRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public interface RegistrationRequestsListener {
        void onRequestsLoaded(List<pendingUser> requests);
    }

    // created ApprovalListener interface and RejectionListener interfaces to keep track of acceptance sand rejections that
    // either work or dont work
    public interface AcceptedListener {
        void onAcceptSuccess();
        void onAcceptError(String error);
    }

    public interface RejectListener {
        void onRejectionSuccess();
        void onRejectionError(String error);
    }

    public void getPendingRequests(RegistrationRequestsListener listener) {
        // access tutor/student nodes in "pending" branch of firebase
        databaseReference.child("pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<pendingUser> requests =new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String requestId = snapshot.getKey();

                    // checking if the user is a Tutor or Student
                    User user = snapshot.getValue(Tutor.class);
                    if (user == null) {
                        user = snapshot.getValue(Student.class);
                    }

                    if (user != null && requestId != null) {
                        pendingUser request = new pendingUser(requestId, user);
                        request.setStatus("pending");
                        requests.add(request);
                    }
                }
                listener.onRequestsLoaded(requests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // had to be implemented to use ValueEventListener, empty body for now
            }
        });
    }

    public void getRejectedRequests( RegistrationRequestsListener listener) {
        // added denied to firebase paths
        databaseReference.child("denied").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<pendingUser> requests = new ArrayList<>();

                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String requestId = snapshot.getKey();

                    // Check if it's a Tutor or Student
                     User user = snapshot.getValue(Tutor.class);
                    if (user == null) {
                        user = snapshot.getValue(Student.class);
                    }

                    if (user != null && requestId != null) {
                        pendingUser request = new pendingUser(requestId, user);
                        request.setStatus("rejected");
                        requests.add(request);
                    }
                }

                listener.onRequestsLoaded(requests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // had to be implemented to use ValueEventListener, empty body for now
            }
        });
    }

    public void acceptPending(pendingUser request, AcceptedListener listener) {
        String requestId = request.getRequestId();
        User user = request.getUser();

        // once accepted, remove the user from pending
        databaseReference.child("pending").child(requestId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // keeping track of the students/tutors who were accepted
                    String approvedPath = user.getRole().toLowerCase() + "s"; // I changed the path names, just made them plural
                    String approvedId = databaseReference.child(approvedPath).push().getKey();

                    databaseReference.child(approvedPath).child(approvedId).setValue(user)
                            .addOnSuccessListener(aVoid1 -> {
                                listener.onAcceptSuccess();
                            })
                            .addOnFailureListener(e -> {
                                listener.onAcceptError(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    listener.onAcceptError(e.getMessage());
                });
    }

    public void rejectRequest(pendingUser request, RejectListener listener) {
        String requestId = request.getRequestId();
        User user = request.getUser();

        // Remove from pending and add to denied
        databaseReference.child("pending").child(requestId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    databaseReference.child("denied").child(requestId).setValue(user)
                            .addOnSuccessListener(aVoid1 -> {
                                listener.onRejectionSuccess();
                            })
                            .addOnFailureListener(e -> {
                                listener.onRejectionError(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    listener.onRejectionError(e.getMessage());
                });
    }

    public void approveRejectedRequest(pendingUser request, AcceptedListener listener) {
        String requestId = request.getRequestId();
        User user = request.getUser();

        // switching rejected users to aceepted
        databaseReference.child("denied").child(requestId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    String approvedPath = user.getRole().toLowerCase() + "s";
                    String approvedId = databaseReference.child(approvedPath).push().getKey();

                    databaseReference.child(approvedPath).child(approvedId).setValue(user)
                            .addOnSuccessListener(aVoid1 -> {
                                listener.onAcceptSuccess();
                            })
                            .addOnFailureListener(e -> {
                                listener.onAcceptError(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    listener.onAcceptError(e.getMessage());
                });
    }
}