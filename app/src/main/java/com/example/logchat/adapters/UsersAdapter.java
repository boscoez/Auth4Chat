package com.example.logchat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logchat.databinding.ItemContainerUserBinding;
import com.example.logchat.listeners.UserListener;
import com.example.logchat.models.User;

import java.util.List;

/**
 * Adapter class for displaying a list of users in a RecyclerView.
 * Handles user selection and interaction for initiating chats.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<User> users; // List of users to display
    private final UserListener userListener; // Listener for user click events

    // Constructor for initializing the adapter
    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users; // Assign user list
        this.userListener = userListener; // Assign listener
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for user items using ViewBinding
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserViewHolder holder, int position) {
        // Bind data to the ViewHolder for the current position
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size(); // Return the total number of users
    }

    /**
     * ViewHolder class for managing user item views.
     */
    class UserViewHolder extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding; // Binding for user item layout

        public UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        /**
         * Binds user data to the respective UI elements in the layout.
         *
         * @param user The user object containing data to display
         */
        void setUserData(User user) {
            binding.textName.setText(user.name); // Set user's name
            binding.textEmail.setText(user.email); // Set user's email
            binding.imageProfile.setImageBitmap(getUserImage(user.image)); // Set user's profile image
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user)); // Handle user click
        }
    }

    /**
     * Decodes a Base64-encoded string into a Bitmap for displaying user profile images.
     *
     * @param encodedImage The Base64-encoded string representing the image
     * @return The decoded Bitmap
     */
    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT); // Decode Base64 string
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length); // Convert to Bitmap
    }
}
