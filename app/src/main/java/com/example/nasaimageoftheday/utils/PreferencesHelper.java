package com.example.nasaimageoftheday.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for managing SharedPreferences.
 * Stores and retrieves user preferences and application state.
 *
 * @author Your Name
 * @version 1.0
 */
public class PreferencesHelper {

    /** Name of the SharedPreferences file */
    private static final String PREFS_NAME = "nasa_image_prefs";

    /** Key for last searched date */
    private static final String KEY_LAST_DATE = "last_date";

    /** Key for last viewed image URL */
    private static final String KEY_LAST_URL = "last_url";

    /** Key for last viewed image title */
    private static final String KEY_LAST_TITLE = "last_title";

    /** Key for first launch flag */
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    /** Key for language preference */
    private static final String KEY_LANGUAGE = "language";

    /** SharedPreferences instance */
    private SharedPreferences preferences;

    /** SharedPreferences editor */
    private SharedPreferences.Editor editor;

    /** Singleton instance */
    private static PreferencesHelper instance;

    /**
     * Gets the singleton instance of PreferencesHelper.
     *
     * @param context The application context
     * @return The PreferencesHelper instance
     */
    public static synchronized PreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Private constructor for singleton pattern.
     *
     * @param context The application context
     */
    private PreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * Saves the last searched date.
     *
     * @param date The date string in YYYY-MM-DD format
     */
    public void saveLastSearchedDate(String date) {
        editor.putString(KEY_LAST_DATE, date);
        editor.apply();
    }

    /**
     * Gets the last searched date.
     *
     * @return The last searched date, or empty string if not set
     */
    public String getLastSearchedDate() {
        return preferences.getString(KEY_LAST_DATE, "");
    }

    /**
     * Saves the last viewed image information.
     *
     * @param url   The URL of the image
     * @param title The title of the image
     */
    public void saveLastViewedImage(String url, String title) {
        editor.putString(KEY_LAST_URL, url);
        editor.putString(KEY_LAST_TITLE, title);
        editor.apply();
    }

    /**
     * Gets the last viewed image URL.
     *
     * @return The URL, or empty string if not set
     */
    public String getLastViewedUrl() {
        return preferences.getString(KEY_LAST_URL, "");
    }

    /**
     * Gets the last viewed image title.
     *
     * @return The title, or empty string if not set
     */
    public String getLastViewedTitle() {
        return preferences.getString(KEY_LAST_TITLE, "");
    }

    /**
     * Checks if this is the first launch of the app.
     *
     * @return true if first launch, false otherwise
     */
    public boolean isFirstLaunch() {
        return preferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    /**
     * Sets the first launch flag to false.
     */
    public void setFirstLaunchComplete() {
        editor.putBoolean(KEY_FIRST_LAUNCH, false);
        editor.apply();
    }

    /**
     * Saves the language preference.
     *
     * @param languageCode The language code (e.g., "en", "fr")
     */
    public void saveLanguage(String languageCode) {
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    /**
     * Gets the saved language preference.
     *
     * @return The language code, or empty string for default
     */
    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, "");
    }

    /**
     * Clears all saved preferences.
     */
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}
