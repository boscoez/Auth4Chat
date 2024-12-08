package com.example.logchat.listeners;

import com.example.logchat.models.User;

/**
 * Interface for handling user click events.
 * This is used to define a callback for when a user is clicked in the list of users.
 */
public interface UserListener {
    /**
     * Callback method invoked when a user is clicked.
     *
     * @param user The user object that was clicked
     */
    void onUserClicked(User user);
}
