package com.example.logchat.activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.logchat.R;
/**
 * Main activity of the LogChat application.
 * This activity serves as the entry point of the application, handling UI setup and
 * implementing an immersive edge-to-edge layout for modern user interfaces.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Called when the activity is created for the first time or re-created after being previously destroyed.
     * @param savedInstanceState If the activity is being re-initialized after being previously shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     *                           If null, this is a fresh instance of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge layout to provide an immersive UI experience.
        EdgeToEdge.enable(this);
        // Set the content view to the main activity layout.
        setContentView(R.layout.activity_main);
        // Handle system UI insets (e.g., status bar, navigation bar) for the main view.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Retrieve the insets for system bars such as the status bar and navigation bar.
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to the view to respect system bar areas and avoid content overlap.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // Return the insets unchanged after applying padding.
            return insets;
        });
    }
}
