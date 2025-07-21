package com.example.donasimakanan.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager - Handles user session management using SharedPreferences
 * Manages login state, user data storage, and logout functionality
 * Simple yet secure implementation for the donation app
 */
public class SessionManager {

    // SharedPreferences keys - konstanta untuk key yang digunakan
    private static final String PREF_NAME = "DonasiMakananSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";

    // SharedPreferences instance
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    /**
     * Constructor - Initialize SharedPreferences
     */
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session - save user data when login successful
     * @param userId User's unique ID
     * @param email User's email
     * @param name User's full name
     */
    public void createLoginSession(String userId, String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.commit(); // Save immediately
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get current logged in user ID
     * @return User ID or null if not logged in
     */
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    /**
     * Get current logged in user email
     * @return User email or null if not logged in
     */
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get current logged in user name
     * @return User name or null if not logged in
     */
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }

    /**
     * Logout user - clear all session data
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }

    /**
     * Update user name in session (when user updates profile)
     * @param newName New user name
     */
    public void updateUserName(String newName) {
        editor.putString(KEY_USER_NAME, newName);
        editor.commit();
    }
}