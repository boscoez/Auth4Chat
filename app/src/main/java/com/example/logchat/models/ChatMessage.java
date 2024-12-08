package com.example.logchat.models;

import java.util.Date;

/**
 * Model class representing a chat message.
 * This class is used to encapsulate the data for a single chat message
 * exchanged between two users.
 */
public class ChatMessage {

    /**
     * The ID of the user who sent the message.
     * Static usage here implies it is shared across all instances, which might cause issues
     * if multiple messages are being handled concurrently.
     */
    public static String senderId;

    /**
     * The ID of the user who received the message.
     */
    public String receiverId;

    /**
     * The content of the message.
     */
    public String message;

    /**
     * A formatted string representing the date and time when the message was sent.
     * Example format: "MMMM dd, yyyy - hh:mm a"
     */
    public String dateTime;

    /**
     * The actual date and time object representing when the message was sent.
     * Used for sorting messages chronologically.
     */
    public Date dateObject;

}
