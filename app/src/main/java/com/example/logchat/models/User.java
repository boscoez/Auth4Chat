package com.example.logchat.models;

import java.io.Serializable;

/**
 * Model class representing a user in the chat application.
 * Extends `ChatMessage` to inherit sender and receiver information,
 * though this relationship might need reconsideration for clarity.
 * Implements `Serializable` to allow easy passing of user objects between activities.
 */
public class User extends ChatMessage implements Serializable {

    /**
     * The name of the user.
     */
    public String name;

    /**
     * Base64-encoded string representing the user's profile image.
     * This string can be decoded to display the image.
     */
    public String image;

    /**
     * The email address of the user.
     */
    public String email;

    /**
     * The FCM (Firebase Cloud Messaging) token for the user.
     * Used for sending push notifications to the user.
     */
    public String token;

    /**
     * The unique identifier for the user.
     * Typically corresponds to the document ID in the Firestore collection.
     */
    public String id;
}
