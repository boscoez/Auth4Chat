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
 * This activity serves as the entry point of the application, handling UI setup and
 * implementing an immersive edge-to-edge layout for modern user interfaces.
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    //private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            database = FirebaseFirestore.getInstance();
            if (database == null) {
                Log.e("MainActivity", "Firestore is null.");
            } else {
                Log.d("MainActivity", "Firestore initialized successfully.");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error initializing Firestore: " + e.getMessage());
        }

        preferenceManager = new PreferenceManager(getApplicationContext());
        //init();
        loadUserDetails();
        getToken();
        setListeners();
        listenConversations();
    }

//    private void init() {
//        conversations = new ArrayList<>();
//        conversationsAdapter = new RecentConversationsAdapter(conversations);
//        binding.conversationRecyclerView.setAdapter(conversationsAdapter);
//    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), userActivity.class)));
    }

    private void loadUserDetails() {
        String name = preferenceManager.getString(Constants.KEY_NAME);
        String image = preferenceManager.getString(Constants.KEY_IMAGE);

        if (name != null) {
            binding.textName.setText(name);
        }

        if (image != null) {
            byte[] bytes = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imageProfile.setImageBitmap(bitmap);
        }
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        if (userId == null) {
            showToast("User ID is null, unable to update token");
            return;
        }

        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId);
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token Updated Successfully"))
                .addOnFailureListener(e -> showToast("Unable To Update Token"));
    }

    private void listenConversations() {
        if (database == null) {
            Log.e("MainActivity", "Firestore database is null. Skipping conversation listener setup.");
            return;
        }

        database.collection(Constants.KEY_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            Log.e("MainActivity", "Error in snapshot listener: " + error.getMessage());
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                    ChatMessage chatMessage = new ChatMessage();
                    ChatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                }
            }
            //conversationsAdapter.notifyDataSetChanged();
            binding.conversationRecyclerView.smoothScrollToPosition(0);
        }
    };

    private void signOut() {
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        if (userId == null) {
            showToast("User ID is null, unable to sign out");
            return;
        }

        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId);
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable To Sign Out"));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
