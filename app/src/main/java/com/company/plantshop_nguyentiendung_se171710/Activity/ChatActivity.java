package com.company.plantshop_nguyentiendung_se171710.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.plantshop_nguyentiendung_se171710.Adapter.MessageAdapter;
import com.company.plantshop_nguyentiendung_se171710.Model.Message;
import com.company.plantshop_nguyentiendung_se171710.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private ImageView btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private boolean isCurrentUserAdmin; // Variable to hold admin status

    private DatabaseReference databaseReference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.backBtn);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(currentUserId, messageList);
        rvMessages.setAdapter(messageAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        // Load messages
        loadMessages();

        // Send message
        btnSend.setOnClickListener(view -> {
            String messageText = etMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(new Message(currentUserId, messageText));
                etMessage.setText("");
            } else {
                Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(view -> finish());
    }

    private void sendMessage(Message message) {
        // Get the current user's role
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(document -> {
                        String role = document.exists() ? document.getString("role") : "user";
                        message.setSenderRole(role);

                        databaseReference.push().setValue(message).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("ChatActivity", "Message sent successfully");
                            } else {
                                Log.e("ChatActivity", "Failed to send message: " + task.getException());
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.w("TAG", "Error getting user role", e);
                    });
        }
    }


    private void loadMessages() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    } else {
                        Log.d("ChatActivity", "Message is null");
                    }
                }
                messageAdapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    rvMessages.scrollToPosition(messageList.size() - 1); // Scroll to the last message
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
                Log.e("ChatActivity", "Error loading messages: " + databaseError.getMessage());
            }
        });
    }

}