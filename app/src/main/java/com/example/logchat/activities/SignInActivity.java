package com.example.logchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.logchat.databinding.ActivitySignInBinding;
import com.example.logchat.utilities.Constants;
import com.example.logchat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles the user sign-in process, including input validation,
 * interaction with Firebase Firestore, and navigation to the main activity.
 */
public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    /**
     * Initializes the activity, sets up UI elements, and prepares the listeners.
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind the layout to the activity
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize the preference manager for shared preferences
        preferenceManager = new PreferenceManager(getApplicationContext());
        // Set up click listeners for UI interactions
        setListeners();
    }
    /**
     * Sets up click listeners for the "Create New Account" and "Sign In" buttons.
     */
    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v -> {
            // Navigate to the SignUpActivity
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        });

        binding.buttonSignIn.setOnClickListener(v -> {
            // Validate input and attempt sign-in
            if (isValidateSignUpDetails()) {
                SignIn();
            }
        });
    }
    /**
     * Displays a toast message with the given text.
     * @param message The message to display in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    /**
     * Initiates the sign-in process by querying Firebase Firestore with user credentials.
     * On successful sign-in, user data is saved in shared preferences, and the main activity is launched.
     */
    private void SignIn() {
        loading(true);
        // Get an instance of Firebase Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // Query Firestore for user with matching email and password
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        // Retrieve user document and save preferences
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        // Navigate to the MainActivity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        showToast("Signed in successfully!");
                    } else {
                        loading(false);
                        showToast("Unable to Sign In");
                    }
                });
    }
    /**
     * Toggles the visibility of the sign-in button and loading indicator.
     * @param isLoading Whether to show the loading indicator or the sign-in button.
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Validates user input for email and password fields.
     * @return {@code true} if the input is valid; {@code false} otherwise.
     */
    private boolean isValidateSignUpDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please Enter Your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Please Enter a Valid Email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please Enter Your Password");
            return false;
        } else {
            return true;
        }
    }
}
