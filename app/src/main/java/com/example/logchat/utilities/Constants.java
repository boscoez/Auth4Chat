package com.example.logchat.utilities;
/**
 * Defines constant values used throughout the application.
 * This class centralizes key strings and values to avoid duplication
 * and ensure consistency across the codebase.
 */
public class Constants {

    /** Name of the Firestore collection for storing user information. */
    public static final String KEY_COLLECTION_USERS = "User";

    /** Key for the user's name field in Firestore or shared preferences. */
    public static final String KEY_NAME = "name";

    /** Key for the user's email field in Firestore or shared preferences. */
    public static final String KEY_EMAIL = "email";

    /** Key for the user's password field in Firestore or shared preferences. */
    public static final String KEY_PASSWORD = "password";

    /** Key for storing the user ID in Firestore or shared preferences. */
    public static final String KEY_USER_ID = "userid";

    /** Key indicating if the user is signed in, stored in shared preferences. */
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    /** Name of the shared preferences file for storing app preferences. */
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";

    /** Key for the user's profile image field in Firestore or shared preferences. */
    // Key for storing the user's profile image URL or Base64-encoded image string in Firestore or shared preferences.
    public static final String KEY_IMAGE = "image";

    // Key for storing the user's first name in Firestore or shared preferences.
    public static final String KEY_FIRST_NAME = "first_name";

    // Key for storing the user's last name in Firestore or shared preferences.
    public static final String KEY_LAST_NAME = "last_name";

}
