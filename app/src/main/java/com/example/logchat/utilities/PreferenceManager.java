package com.example.logchat.utilities;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * A utility class for managing shared preferences in the application.
 * This class provides methods to store, retrieve, and clear data
 * in shared preferences using key-value pairs.
 */
public class PreferenceManager {
    private final SharedPreferences sharedPreferences;
    /**
     * Constructs a new PreferenceManager instance and initializes the shared preferences.
     * @param context The application context used to access shared preferences.
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
    /**
     * Saves a boolean value in shared preferences.
     * @param key   The key under which the value is stored.
     * @param value The boolean value to store.
     */
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply(); // Asynchronously save changes.
    }
    /**
     * Retrieves a boolean value from shared preferences.
     * @param key The key for the value to retrieve.
     * @return The boolean value associated with the key, or {@code false} if not found.
     */
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false); // Default to false if key is not found.
    }
    /**
     * Saves a string value in shared preferences.
     * @param key   The key under which the value is stored.
     * @param value The string value to store.
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply(); // Asynchronously save changes.
    }
    /**
     * Retrieves a string value from shared preferences.
     * @param key The key for the value to retrieve.
     * @return The string value associated with the key, or {@code null} if not found.
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, null); // Default to null if key is not found.
    }
    /**
     * Clears all data from shared preferences.
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Remove all key-value pairs.
        editor.apply(); // Asynchronously save changes.
    }
}
