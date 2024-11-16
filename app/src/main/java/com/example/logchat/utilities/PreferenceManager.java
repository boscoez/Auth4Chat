package com.example.logchat.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences;

    /**
     * @param context
     */
    public PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    /**
     * @param key
     * @param value
     */
    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * @param key
     * @return
     */
    public Boolean getBoolean(String key){

        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * @param key
     * @param value
     */
    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * @param key
     * @return
     */
    public String getString(String key){

        return sharedPreferences.getString(key, null);
    }

    /**
     * CLEAR
     */
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
