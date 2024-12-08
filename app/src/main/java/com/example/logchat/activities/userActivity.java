package com.example.logchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.logchat.adapters.UsersAdapter;
import com.example.logchat.databinding.ActivityUsersBinding;
import com.example.logchat.listeners.UserListener;
import com.example.logchat.models.User;
import com.example.logchat.utilities.Constants;
import com.example.logchat.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class userActivity extends AppCompatActivity implements UserListener {
    private ActivityUsersBinding binding; // View binding for accessing layout components
    private PreferenceManager preferenceManager; // Shared preferences for managing user session data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater()); // Initialize view binding
        setContentView(binding.getRoot()); // Set the content view
        preferenceManager = new PreferenceManager(getApplicationContext()); // Initialize the preference manager
        setListeners(); // Set up UI event listeners
        getUsers(); // Fetch the list of users
    }

    /**
     * Sets up listeners for UI interactions, such as the back button.
     */
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed()); // Go back to the previous screen
    }

    /**
     * Fetches the list of users from Firestore and displays them.
     */
    private void getUsers() {
        loading(true); // Show progress bar while fetching users
        FirebaseFirestore database = FirebaseFirestore.getInstance(); // Get Firestore instance
        database.collection(Constants.KEY_COLLECTION_USERS) // Access the "users" collection in Firestore
                .get()
                .addOnCompleteListener(task -> { // Handle the result of the query
                    loading(false); // Hide progress bar after query completes
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID); // Get current user's ID
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>(); // Initialize list to store user data
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue; // Skip the current user's data
                            }
                            User user = new User(); // Create a new User object
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME); // Set user's name
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL); // Set user's email
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE); // Set user's profile image
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN); // Set user's FCM token
                            user.id = queryDocumentSnapshot.getId(); // Set user's Firestore document ID
                            users.add(user); // Add the user to the list
                        }
                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users, this); // Initialize the adapter
                            binding.usersRecyclerView.setAdapter(usersAdapter); // Attach the adapter to the RecyclerView
                            binding.usersRecyclerView.setVisibility(View.VISIBLE); // Make the RecyclerView visible
                        } else {
                            showErrorMessage(); // Show error if no users are found
                        }
                    } else {
                        showErrorMessage(); // Show error if the query fails
                    }
                });
    }

    /**
     * Displays an error message if no users are available or an error occurs.
     */
    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No User Available")); // Set the error message text
        binding.textErrorMessage.setVisibility(View.VISIBLE); // Make the error message visible
    }

    /**
     * Toggles the visibility of the progress bar based on the loading state.
     *
     * @param isLoading true to show the progress bar, false to hide it
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE); // Show progress bar
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
        }
    }

    /**
     * Handles the event when a user is clicked from the list.
     *
     * @param user The user that was clicked
     */
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class); // Start the ChatActivity
        intent.putExtra(Constants.KEY_USER, user); // Pass the selected user's data to the ChatActivity
        startActivity(intent); // Launch the ChatActivity
        finish(); // Close the current activity
    }
}
