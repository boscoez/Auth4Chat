package com.example.logchat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.logchat.databinding.ActivityMainBinding;
import com.example.logchat.models.ChatMessage;
import com.example.logchat.utilities.Constants;
import com.example.logchat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Main activity of the LogChat application.
 * Handles user interface initialization, user authentication, and real-time
 * updates for recent conversations using Firestore.
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding; // View Binding for MainActivity layout
    private PreferenceManager preferenceManager; // Shared preferences for managing user data
    private List<ChatMessage> conversations; // List to hold recent conversations
    // private RecentConversationsAdapter conversationsAdapter; // Adapter for RecyclerView (commented out)
    private FirebaseFirestore database; // Firestore database instance for data storage and retrieval

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // Inflate layout with binding
        setContentView(binding.getRoot()); // Set content view

        try {
            // Initialize Firestore database
            database = FirebaseFirestore.getInstance();
            if (database == null) {
                Log.e("MainActivity", "Firestore is null.");
            } else {
                Log.d("MainActivity", "Firestore initialized successfully.");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error initializing Firestore: " + e.getMessage());
        }

        preferenceManager = new PreferenceManager(getApplicationContext()); // Initialize preference manager
        // init(); // Initialize conversations (commented out)
        loadUserDetails(); // Load user details to display in the UI
        getToken(); // Get FCM token for push notifications
        setListeners(); // Set up event listeners for UI interactions
        listenConversations(); // Listen for real-time updates on recent conversations
    }

    // Set up event listeners for user interactions
    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut()); // Sign out when the sign-out button is clicked
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), userActivity.class))); // Start a new chat
    }

    // Load user details (name and profile picture) from shared preferences
    private void loadUserDetails() {
        String name = preferenceManager.getString(Constants.KEY_NAME);
        String image = preferenceManager.getString(Constants.KEY_IMAGE);

        if (name != null) {
            binding.textName.setText(name); // Display user name
        }

        if (image != null) {
            byte[] bytes = Base64.decode(image, Base64.DEFAULT); // Decode profile image
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imageProfile.setImageBitmap(bitmap); // Set profile image in the UI
        }
    }

    // Get FCM token for push notifications
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken); // Fetch and update token
    }

    // Update the FCM token in Firestore
    private void updateToken(String token) {
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        if (userId == null) {
            showToast("User ID is null, unable to update token"); // Show error if user ID is missing
            return;
        }

        // Update the token in Firestore
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId);
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token Updated Successfully")) // Success message
                .addOnFailureListener(e -> showToast("Unable To Update Token")); // Failure message
    }

    // Listen for real-time updates to recent conversations
    private void listenConversations() {
        if (database == null) {
            Log.e("MainActivity", "Firestore database is null. Skipping conversation listener setup.");
            return;
        }

        // Query Firestore for conversations where the user is the sender
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    // Firestore event listener for conversation updates
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            Log.e("MainActivity", "Error in snapshot listener: " + error.getMessage());
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    // Map Firestore document fields to ChatMessage object
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                    ChatMessage chatMessage = new ChatMessage();
                    ChatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE); // Last message
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP); // Timestamp
                    conversations.add(chatMessage); // Add to conversations list
                }
            }
            // Update RecyclerView to display conversations
            // conversationsAdapter.notifyDataSetChanged(); // Uncomment if using an adapter
            binding.conversationRecyclerView.smoothScrollToPosition(0); // Scroll to the top
        }
    };

    // Sign out the user and clear their session
    private void signOut() {
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        if (userId == null) {
            showToast("User ID is null, unable to sign out");
            return;
        }

        // Remove FCM token and update Firestore
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId);
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete()); // Remove token
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear(); // Clear shared preferences
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class)); // Navigate to sign-in screen
                    finish(); // Close the current activity
                })
                .addOnFailureListener(e -> showToast("Unable To Sign Out")); // Show error message
    }

    // Display a Toast message
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
