// AdminInboxActivity.java
package com.example.logintest.domain;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.logintest.repository.FirebaseRegistrationRepository;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

// this class displays the admins inbox, where they can see users who have tried to sign up and
// has the ability to reject or accept a user's request to sign up
public class AdminInboxActivity extends AppCompatActivity {
    private LinearLayout containerLayout;
    private TabLayout pendingOrRejectedTab;

    private List<pendingUser> pendingRequests = new ArrayList<>();
    private List<pendingUser> rejectedRequests = new ArrayList<>();
    private FirebaseRegistrationRepository pendingRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_inbox);

        containerLayout = findViewById(R.id.containerLayout);
        pendingOrRejectedTab = findViewById(R.id.tabLayout);

        pendingRepository = new FirebaseRegistrationRepository();

        // to get the Tab widget in the admin_inbox.xml to function
        pendingOrRejectedTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showPendingRequests();
                } else if (tab.getPosition() == 1) {
                    // loading the rejected requests
                    pendingRepository.getRejectedRequests(new FirebaseRegistrationRepository.RegistrationRequestsListener() {
                        @Override
                        public void onRequestsLoaded(List<pendingUser> requests) {
                            rejectedRequests = requests;
                            showRejectedRequests();
                        }
                    });
                }
            }
            @Override
            // had to implement the following 2 methods because they are abstract methods in the interface,
            // leaving body blank
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // loading the request cards
        pendingRepository.getPendingRequests(new FirebaseRegistrationRepository.RegistrationRequestsListener() {
            @Override
            public void onRequestsLoaded(List<pendingUser> requests) {
                pendingRequests = requests;
                showPendingRequests();
            }
        });
    }

    // helper method to display all the users that have tried to sign up
    // frequently called sort of as a "refresher" or "reloader" just so that it's constantly updated with every change
    private void showPendingRequests() {
        containerLayout.removeAllViews(); // All the stuff in the container is cleared first

        // then the pending cards are re-displayed onto the container
        for (pendingUser request: pendingRequests) {
            View requestCard = createPendingRequestCard(request);
            containerLayout.addView(requestCard);
        }
    }

    // helper method to display all the users who signed up but that the Admin has rejected
    // also frequently called sort of as a "refresher" or "reloader" just so that it's constantly updated with every change
    private void showRejectedRequests() {
        containerLayout.removeAllViews(); // All the stuff in the container is cleared first

        // then the rejected cards are re-displayed onto the container
        for (pendingUser request:rejectedRequests) {
            View requestCard = createRejectedRequestCard(request);
            containerLayout.addView(requestCard);
        }
    }

    private View createPendingRequestCard(final pendingUser request) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.pending_inbox, containerLayout, false);

        // getting the card attributes from the pending_inbox.xml so that we can update them with the firebase info
        TextView pendingName = cardView.findViewById(R.id.pendingName);
        TextView pendingEmail = cardView.findViewById(R.id.pendingEmail);
        TextView pendingRole = cardView.findViewById(R.id.pendingRole);

        pendingName.setText("Name: " + request.getPendingName());
        pendingEmail.setText("Email: " + request.getPendingEmail());
        pendingRole.setText("Type: " + request.getPendingRole());


        Button acceptBtn = cardView.findViewById(R.id.acceptBtn);
        Button rejectBtn = cardView.findViewById(R.id.rejectBtn);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptPending(request, cardView);
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectRequest(request, cardView);
            }
        });

        return cardView;
    }

    private View createRejectedRequestCard(final pendingUser request) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.rejected_inbox, containerLayout, false);
        // getting the card attributes from the rejected_inbox.xml so that we can update them with the firebase info
        TextView rejectedName = cardView.findViewById(R.id.rejectedName);
        TextView rejectedEmail = cardView.findViewById(R.id.rejectedEmail);
        TextView rejectedRole= cardView.findViewById(R.id.rejectedRole);

        rejectedName.setText("Name: "+ request.getPendingName());
        rejectedEmail.setText("Email: "+ request.getPendingEmail());
        rejectedRole.setText("Type: " +request.getPendingRole());

        Button acceptRejecteeBtn = cardView.findViewById(R.id.acceptRejectionBtn);
        acceptRejecteeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveRejectedRequest(request, cardView);
            }
        });
        return cardView;
    }


    private void acceptPending(pendingUser request, View cardView) {
        pendingRepository.acceptPending(request, new FirebaseRegistrationRepository.AcceptedListener() {
            @Override
            public void onAcceptSuccess() {
                runOnUiThread(() -> {
                    pendingRequests.remove(request);
                    containerLayout.removeView(cardView);
                    Toast.makeText(AdminInboxActivity.this, request.getPendingName() + " has been accpeted!", Toast.LENGTH_SHORT).show();
                    if (pendingRequests.isEmpty()) {
                        showPendingRequests();
                    }
                });
            }
            @Override
            public void onAcceptError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminInboxActivity.this, "The Request could not be approved! " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void rejectRequest(final pendingUser request, final View cardView) {
        pendingRepository.rejectRequest(request, new FirebaseRegistrationRepository.RejectListener() {
            @Override
            public void onRejectionSuccess() {
                runOnUiThread(() -> {
                    pendingRequests.remove(request);
                    rejectedRequests.add(request);
                    containerLayout.removeView(cardView);
                    Toast.makeText(AdminInboxActivity.this, request.getPendingName() + "has been rejected", Toast.LENGTH_SHORT).show();
                    if (pendingRequests.isEmpty()) {
                        showPendingRequests();
                    }
                });
            }
            @Override
            public void onRejectionError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminInboxActivity.this, "Could not reject " + error, Toast.LENGTH_SHORT).show();
                });
            }

        });
    }

    private void approveRejectedRequest(final pendingUser request, final View cardView) {
        pendingRepository.approveRejectedRequest(request, new FirebaseRegistrationRepository.AcceptedListener() {
            @Override
            public void onAcceptSuccess() {
                runOnUiThread(() -> {
                    rejectedRequests.remove(request);
                    containerLayout.removeView(cardView);
                    Toast.makeText(AdminInboxActivity.this, request.getPendingName() + "has been accepted!", Toast.LENGTH_SHORT).show();
                    if (rejectedRequests.isEmpty()) {
                        showRejectedRequests();
                    }
                });
            }
            @Override
            public void onAcceptError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminInboxActivity.this, "Could not accept User, " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
//End of AdminInboxAcitivty.java