
package com.example.logintest.domain;

// this uses the email trigger extension from Firestore

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.logintest.repository.FirebaseRegistrationRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// this class displays the admins inbox, where they can see users who have tried to sign up and
// has the ability to reject or accept a user'''s request to sign up
public class AdminInboxActivity extends AppCompatActivity {
    private static final String TAG = "AdminInboxActivity";
    private LinearLayout containerLayout;
    private TabLayout pendingOrRejectedTab;

    private List<PendingUser> pendingRequests = new ArrayList<>();
    private List<PendingUser> rejectedRequests = new ArrayList<>();
    private FirebaseRegistrationRepository pendingRepository;

    // --- POJO for the email message body ---
    public static class Message {
        private String subject;
        private String text;

        public Message() {}

        public Message(String subject, String text) {
            this.subject = subject;
            this.text = text;
        }

        public String getSubject() { return subject; }
        public String getText() { return text; }
    }

    // --- POJO for the top-level mail document ---
    public static class Mail {
        private String to;
        private Message message;

        public Mail() {}

        public Mail(String to, Message message) {
            this.to = to;
            this.message = message;
        }

        public String getTo() { return to; }
        public Message getMessage() { return message; }
    }


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
                        public void onRequestsLoaded(List<PendingUser> requests) {
                            rejectedRequests = requests;
                            showRejectedRequests();
                        }
                    });
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // loading the request cards
        pendingRepository.getPendingRequests(new FirebaseRegistrationRepository.RegistrationRequestsListener() {
            @Override
            public void onRequestsLoaded(List<PendingUser> requests) {
                pendingRequests = requests;
                showPendingRequests();
            }
        });
    }

    private void showPendingRequests() {
        containerLayout.removeAllViews();
        for (PendingUser request: pendingRequests) {
            View requestCard = createPendingRequestCard(request);
            containerLayout.addView(requestCard);
        }
    }
    private void showRejectedRequests() {
        containerLayout.removeAllViews();
        for (PendingUser request:rejectedRequests) {
            View requestCard = createRejectedRequestCard(request);
            containerLayout.addView(requestCard);
        }
    }

    /**
     * Show pending information for a registered account to the Admin for their approval/rejection
     * @param request
     * @return
     */
    private View createPendingRequestCard(final PendingUser request) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.pending_inbox, containerLayout, false);

        TextView pendingName = cardView.findViewById(R.id.pendingName);
        TextView pendingEmail = cardView.findViewById(R.id.pendingEmail);
        TextView pendingRole = cardView.findViewById(R.id.pendingRole);

        pendingName.setText("Name: " + request.getPendingName());
        pendingEmail.setText("Email: " + request.getPendingEmail());
        pendingRole.setText("Type: " + request.getPendingRole());


        Button acceptBtn = cardView.findViewById(R.id.acceptBtn);
        Button rejectBtn = cardView.findViewById(R.id.rejectBtn);

        acceptBtn.setOnClickListener(v -> acceptPending(request, cardView));
        rejectBtn.setOnClickListener(v -> rejectRequest(request, cardView));

        return cardView;
    }

    private View createRejectedRequestCard(final PendingUser request) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.rejected_inbox, containerLayout, false);
        TextView rejectedName = cardView.findViewById(R.id.rejectedName);
        TextView rejectedEmail = cardView.findViewById(R.id.rejectedEmail);
        TextView rejectedRole= cardView.findViewById(R.id.rejectedRole);

        rejectedName.setText("Name: "+ request.getPendingName());
        rejectedEmail.setText("Email: "+ request.getPendingEmail());
        rejectedRole.setText("Type: " +request.getPendingRole());

        Button acceptRejecteeBtn = cardView.findViewById(R.id.acceptRejectionBtn);
        acceptRejecteeBtn.setOnClickListener(v -> approveRejectedRequest(request, cardView));
        return cardView;
    }


    private void acceptPending(PendingUser request, View cardView) {
        pendingRepository.acceptPending(request, new FirebaseRegistrationRepository.AcceptedListener() {
            @Override
            public void onAcceptSuccess() {
                runOnUiThread(() -> {
                    pendingRequests.remove(request); // remove from the view
                    containerLayout.removeView(cardView);
                    // send a message to the Admin
                    Toast.makeText(AdminInboxActivity.this, request.getPendingName() + " has been accepted!", Toast.LENGTH_SHORT).show();
                    if (pendingRequests.isEmpty()) {
                        showPendingRequests();
                    }
                });
                // the subject and body of the email to be sent to the user who has been accepted
                String subject = "[UPDATE] Your OTAMS Account Registration Request";
                String body = "You are receiving this email to let you know that your account has been accepted. Welcome!";
                sendEmailViaTrigger(request.getPendingEmail(), subject, body);
            }
            @Override
            public void onAcceptError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminInboxActivity.this, "The Request could not be approved! " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void rejectRequest(final PendingUser request, final View cardView) {
        pendingRepository.rejectRequest(request, new FirebaseRegistrationRepository.RejectListener() {
            @Override
            public void onRejectionSuccess() {
                runOnUiThread(() -> {
                    pendingRequests.remove(request);
                    rejectedRequests.add(request);
                    containerLayout.removeView(cardView);
                    Toast.makeText(AdminInboxActivity.this, request.getPendingName() + " has been rejected", Toast.LENGTH_SHORT).show();
                    if (pendingRequests.isEmpty()) {
                        showPendingRequests();
                    }
                });
                // send rejection email to the user
                String subject = "[UPDATE] Your OTAMS Account Registration Request";
                String body = "You are receiving this email to let you know that your account has been rejected. Please try again, or contact seg.group4.otams@gmail.com for assistance.";
                sendEmailViaTrigger(request.getPendingEmail(), subject, body);
            }
            @Override
            public void onRejectionError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminInboxActivity.this, "Could not reject " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // helper method based on the extension
    // official documentation on GitHub
    private void sendEmailViaTrigger(String recipientEmail, String subject, String body) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Message message = new Message(subject, body);
        Mail mail = new Mail(recipientEmail, message);

        // add to the mail collection made in the Firebase console
        db.collection("mail")
                .add(mail)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Email trigger document written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing email trigger document", e));
    }


    private void approveRejectedRequest(final PendingUser request, final View cardView) {
        pendingRepository.approveRejectedRequest(request, new FirebaseRegistrationRepository.AcceptedListener() {
            @Override
            public void onAcceptSuccess() {
                runOnUiThread(() -> {
                    rejectedRequests.remove(request);
                    containerLayout.removeView(cardView);
                    Toast.makeText(AdminInboxActivity.this, request.getPendingName() + " has been accepted!", Toast.LENGTH_SHORT).show();
                    if (rejectedRequests.isEmpty()) {
                        showRejectedRequests();
                    }
                });
                // previously rejected account can get an email update when their registration status changes
                String subject = "[UPDATE] Your OTAMS Account Registration Request";
                String body = "You are receiving this email to let you know that your account has been accepted. Welcome!";
                sendEmailViaTrigger(request.getPendingEmail(), subject, body);
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
