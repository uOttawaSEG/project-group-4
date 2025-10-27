package com.example.logintest.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// This class is implemented for the request functionality in the admin's inbox
public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.PendingViewHolder> {

    private List<User> pendingRequests;
    private Context context;

    public PendingRequestAdapter(Context context, List<User> pendingRequests) {
        this.context = context;
        this.pendingRequests = pendingRequests;
    }

    @NonNull
    @Override
    public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pending_inbox, parent, false);
        return new PendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingViewHolder holder, int position) {
        User pendingRequest = pendingRequests.get(position);
        String fullName = pendingRequest.getFirstName() + " " + pendingRequest.getLastName();

        holder.pendingName.setText("Name: " + fullName);
        holder.pendingEmail.setText("Email: " + pendingRequest.getEmail());
        holder.pendingRole.setText("Role: " + pendingRequest.getRole());

        // for the accept and decline buttons
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accept logic
            }
        });
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reject logic
            }
        });
    }


    public static class PendingViewHolder extends RecyclerView.ViewHolder {
        TextView pendingName;
        TextView pendingEmail;
        TextView pendingRole;
        Button acceptBtn;
        Button rejectBtn;

        public PendingViewHolder(@NonNull View itemView) {
            super(itemView);
            pendingName = itemView.findViewById(R.id.pendingName);
            pendingEmail = itemView.findViewById(R.id.pendingEmail);
            pendingRole = itemView.findViewById(R.id.pendingRole);
            acceptBtn = itemView.findViewById(R.id.approveButton);
            rejectBtn = itemView.findViewById(R.id.denyButton);

        }
    } //end of PendingViewHolder method

    // the class could not be made instantiable unless this abstract method was implemented
    @Override
    public int getItemCount() {
        return pendingRequests.size();
    }
}

