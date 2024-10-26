package com.company.plantshop_nguyentiendung_se171710.Adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.company.plantshop_nguyentiendung_se171710.Model.Message;
import com.company.plantshop_nguyentiendung_se171710.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messages;
    private final String currentUserId;

    public MessageAdapter(String currentUserId, List<Message> messages) {
        this.currentUserId = currentUserId;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getMessage());

        if (message.getSenderId().equals(currentUserId)) {
            holder.senderText.setText("You");
            holder.senderText.setVisibility(View.VISIBLE); // Show sender's name
            holder.messageText.setBackgroundResource(R.drawable.message_background_right);
            ((LinearLayout.LayoutParams) holder.messageText.getLayoutParams()).gravity = Gravity.END;
            ((LinearLayout.LayoutParams) holder.senderText.getLayoutParams()).gravity = Gravity.END;
        } else {
            String senderRole = message.getSenderRole(); // Assuming Message class has getSenderRole method
            if ("admin".equals(senderRole)) {
                holder.senderText.setText("Admin");
            } else {
                holder.senderText.setText("Customer"); // Adjust based on your requirements
            }
            holder.senderText.setVisibility(View.VISIBLE); // Show sender's name
            holder.messageText.setBackgroundResource(R.drawable.message_background_left);
            ((LinearLayout.LayoutParams) holder.messageText.getLayoutParams()).gravity = Gravity.START;
            ((LinearLayout.LayoutParams) holder.senderText.getLayoutParams()).gravity = Gravity.START;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView senderText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            senderText = itemView.findViewById(R.id.senderText);
        }
    }
}
