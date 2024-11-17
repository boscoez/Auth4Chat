package com.example.logchat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.logchat.databinding.ActivitySignUpBinding;
import com.example.logchat.utilities.Constants;
import com.example.logchat.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
/**
 * Activity for user sign-up functionality.
 */
public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodeImage;
    /**
     * Initializes the activity and sets up UI elements and listeners.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

    }
    /**
     * Sets up click listeners for UI elements.
     */
    private void setListeners() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidateSignUpDetails()) {
                SignUp();
            }
        });
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
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
     * Handles the sign-up process by validating input, storing user data in Firestore,
     * and saving preferences locally upon successful sign-up.
     */
    private void SignUp() {
        // Show the loading state to indicate progress.
        loading(true);
        // Initialize Firebase Firestore instance.
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // Create a user HashMap to store user details.
        HashMap<String, String> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString().trim()); // Store user's first name.
        user.put(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString().trim()); // Store user's last name.
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString().trim()); // Store user's email.
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString().trim()); // Store user's password.
        user.put(Constants.KEY_IMAGE, encodeImage); // Store encoded image of the user.

        // Add the user data to the Firestore 'users' collection.
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    // Save user sign-in state and details to shared preferences.
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true); // Indicate the user is signed in.
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId()); // Save Firestore document ID.
                    preferenceManager.putString(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodeImage); // Save user profile image.

                    // Show a success message.
                    showToast("Sign up successful!");
                    // Navigate to the MainActivity and clear the back stack.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    // Stop the loading state and display the error message.
                    loading(false);
                    showToast(exception.getMessage()); // Show error message to the user.
                });
    }
    /**
     * Encodes a given bitmap image into a Base64 string.
     * @param bitmap The Bitmap to be encoded.
     * @return A Base64 encoded string representation of the bitmap.
     */
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150; // Target width for the preview image.
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth(); // Maintain aspect ratio.
        // Create a scaled-down version of the bitmap for preview purposes.
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        // Compress the preview bitmap into a JPEG format with 50% quality.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        // Convert the compressed image data into a byte array and encode it into Base64.
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    /**
     * ActivityResultLauncher for picking an image from the device gallery.
     * On successful image selection, the image is displayed, and its Base64 encoding is stored.
     */
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) { // Ensure the result is successful.
                    Uri imageUri = result.getData().getData(); // Retrieve the URI of the selected image.
                    try {
                        // Open an input stream to the selected image and decode it into a bitmap.
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        // Display the selected image in the profile ImageView.
                        binding.imageProfile.setImageBitmap(bitmap);
                        // Hide the "Add Image" text since an image is now selected.
                        binding.textAddImage.setVisibility(View.GONE);
                        // Encode the selected image and store it as a Base64 string.
                        encodeImage = encodeImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace(); // Log the exception if the image file is not found.
                    }
                }
            });
    /**
     * Validates the input details provided by the user during sign-up.
     * @return {@code true} if all sign-up details are valid; {@code false} otherwise.
     */
    private Boolean isValidateSignUpDetails() {
        if (encodeImage == null) {
            showToast("Please Select Your Image");
            return false;
        } else if (binding.inputFirstName.getText().toString().trim().isEmpty()) {
            showToast("Please Enter Your First Name");
            return false;
        } else if (binding.inputLastName.getText().toString().trim().isEmpty()) {
            showToast("Please Enter Your Last Name");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please Enter Your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Please Enter a Valid Email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please Enter Your Password");
            return false;
        } else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Please Confirm Your Password");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Password and Confirm Password Must Match");
            return false;
        } else {
            return true; // All inputs are valid.
        }
    }
    /**
     * Toggles the loading state of the sign-up button and progress bar.
     * @param isLoading If {@code true}, show the progress bar and hide the button;
     *                  otherwise, hide the progress bar and show the button.
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE); // Hide the sign-up button.
            binding.progressBar.setVisibility(View.VISIBLE); // Show the progress bar.
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE); // Hide the progress bar.
            binding.buttonSignUp.setVisibility(View.VISIBLE); // Show the sign-up button.
        }
    }
}