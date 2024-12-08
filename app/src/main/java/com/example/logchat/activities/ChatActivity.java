package com.example.logchat.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.logchat.adapters.ChatAdapter;
import com.example.logchat.databinding.ActivityChatBinding;
import com.example.logchat.models.ChatMessage;
import com.example.logchat.models.User;
import com.example.logchat.utilities.Constants;
import com.example.logchat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding; // Binding for XML layout views
    private User receiverUser; // Stores information about the recipient user
    private List<ChatMessage> chatMessages; // List to hold chat messages
    private ChatAdapter chatAdapter; // Adapter for RecyclerView to display chat messages
    private PreferenceManager preferenceManager; // Utility to manage shared preferences
    private FirebaseFirestore database; // Firebase Firestore instance for database operations

    // Initialize essential components
    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>(); // Initialize the list for chat messages
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image), // Decode receiver's profile image
                preferenceManager.getString(Constants.KEY_USER_ID) // Sender's ID
        );
        binding.chatRecyclerView.setAdapter(chatAdapter); // Attach adapter to RecyclerView
        database = FirebaseFirestore.getInstance(); // Initialize Firestore database
    }

    // Send a message to Firestore
    private void sendMessages() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID)); // Sender's ID
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id); // Receiver's ID
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString()); // Message content
        message.put(Constants.KEY_TIMESTAMP, new Date()); // Timestamp for the message

        // Add the message to the chat collection in Firestore
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null); // Clear the input field after sending
    }

    // Listen for incoming messages from Firestore
    private void listenMessage() {
        // Query messages sent by the current user to the receiver
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);

        // Query messages sent by the receiver to the current user
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    // Event listener to handle Firestore snapshot changes
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            binding.progressBar.setVisibility(View.GONE); // Hide progress bar on error
            binding.chatRecyclerView.setVisibility(View.GONE); // Hide RecyclerView on error
            return;
        }
        if (value != null) {
            int count = chatMessages.size(); // Track the current size of the chat list
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID); // Extract sender ID
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID); // Extract receiver ID
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE); // Extract message text
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)); // Format timestamp
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP); // Raw timestamp
                    chatMessages.add(chatMessage); // Add message to the list
                }
            }
            // Sort messages by timestamp for proper ordering
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged(); // Notify adapter for a full update
            } else {
                // Notify adapter for partial updates and scroll to the newest message
                chatAdapter.notifyItemRangeInserted(count, chatMessages.size() - count);
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            // Toggle visibility based on whether there are messages
            binding.chatRecyclerView.setVisibility(chatMessages.isEmpty() ? View.GONE : View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE); // Hide progress bar once done
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater()); // Inflate layout using View Binding
        setContentView(binding.getRoot()); // Set the content view
        loadReceiverDetails(); // Load details of the chat receiver
        setListeners(); // Set click listeners for buttons
        init(); // Initialize components
        listenMessage(); // Start listening for chat messages
        binding.chatRecyclerView.setVisibility(View.VISIBLE); // Ensure RecyclerView is visible
    }

    // Decode a Base64 encoded string into a Bitmap
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // Load details of the receiver from the Intent
    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER); // Get user data from Intent
        assert receiverUser != null; // Ensure receiverUser is not null
        binding.textName.setText(receiverUser.name); // Set receiver's name in the UI
    }

    // Set click listeners for UI elements
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed()); // Navigate back when the back button is clicked
        binding.layoutSend.setOnClickListener(v -> sendMessages()); // Send message when the send button is clicked
    }

    // Format a Date object into a readable string
    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    // Display an error message as a Toast
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
