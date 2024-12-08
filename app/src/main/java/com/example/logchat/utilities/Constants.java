package com.example.logchat.utilities;

/**
 * Defines constant values used throughout the application.
 * This class centralizes key strings and values to avoid duplication
 * and ensure consistency across the codebase.
 *
 * Constants typically correspond to Firestore field names, shared preferences keys,
 * or other static configuration values.
 */
public class Constants {

    // ------------------- Firestore Collections -------------------

    /** Firestore collection name for storing user information. */
    public static final String KEY_COLLECTION_USERS = "User";

    /** Firestore collection name for storing chat messages. */
    public static final String KEY_COLLECTION_CHAT = "chat";

    /** Firestore collection name for storing conversations. */
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";

    // ------------------- Firestore Document Fields -------------------

    /** Key for the user's name field. */
    public static final String KEY_NAME = "name";

    /** Key for the user's email field. */
    public static final String KEY_EMAIL = "email";

    /** Key for the user's password field. */
    public static final String KEY_PASSWORD = "password";

    /** Key for the user's profile image field. */
    public static final String KEY_IMAGE = "image";

    /** Key for storing the user's Firebase Cloud Messaging (FCM) token. */
    public static final String KEY_FCM_TOKEN = "fcmToken";

    /** Key for storing the user's unique identifier (e.g., Firestore document ID). */
    public static final String KEY_USER_ID = "userid";

    /** Key for the user's availability status. */
    public static final String KEY_AVAILABILITY = "availability";

    /** Key for the sender ID field in chat messages. */
    public static final String KEY_SENDER_ID = "senderId";

    /** Key for the receiver ID field in chat messages. */
    public static final String KEY_RECEIVER_ID = "receiverId";

    /** Key for the message content field in chat messages. */
    public static final String KEY_MESSAGE = "message";

    /** Key for the timestamp field in chat messages or conversations. */
    public static final String KEY_TIMESTAMP = "timestamp";

    /** Key for the last message field in conversations. */
    public static final String KEY_LAST_MESSAGE = "lastMessage";

    /** Key for the sender's name in conversations. */
    public static final String KEY_SENDER_NAME = "senderName";

    /** Key for the receiver's name in conversations. */
    public static final String KEY_RECEIVER_NAME = "receiverName";

    /** Key for the sender's profile image in conversations. */
    public static final String KEY_SENDER_IMAGE = "senderImage";

    /** Key for the receiver's profile image in conversations. */
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";

    // ------------------- Shared Preferences Keys -------------------

    /** Name of the shared preferences file. */
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";

    /** Key for checking if a user is signed in. */
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    // ------------------- Additional Fields -------------------

    /** Key for storing the user's first name. */
    public static final String KEY_FIRST_NAME = "first_name";

    /** Key for storing the user's last name. */
    public static final String KEY_LAST_NAME = "last_name";

    /** Key for passing user information between activities. */
    public static final String KEY_USER = "user";
}
