package com.example.nasaimageoftheday.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nasaimageoftheday.models.NasaImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper class for managing NASA image favorites.
 * Handles all SQLite database operations including create, read, update, and delete.
 * This implements the Data Access Layer pattern.
 *
 * @author Your Name
 * @version 1.0
 */
public class NasaImageDatabaseHelper extends SQLiteOpenHelper {

    /** Database name */
    private static final String DATABASE_NAME = "nasa_images.db";

    /** Database version */
    private static final int DATABASE_VERSION = 1;

    /** Table name for favorites */
    public static final String TABLE_FAVORITES = "favorites";

    /** Column names */
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_EXPLANATION = "explanation";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_HD_URL = "hd_url";
    public static final String COLUMN_MEDIA_TYPE = "media_type";
    public static final String COLUMN_COPYRIGHT = "copyright";

    /** SQL statement to create the favorites table */
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_FAVORITES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DATE + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_EXPLANATION + " TEXT, " +
                    COLUMN_URL + " TEXT, " +
                    COLUMN_HD_URL + " TEXT, " +
                    COLUMN_MEDIA_TYPE + " TEXT, " +
                    COLUMN_COPYRIGHT + " TEXT" +
                    ");";

    /** Singleton instance */
    private static NasaImageDatabaseHelper instance;

    /**
     * Gets the singleton instance of the database helper.
     *
     * @param context The application context
     * @return The database helper instance
     */
    public static synchronized NasaImageDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NasaImageDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Private constructor for singleton pattern.
     *
     * @param context The application context
     */
    private NasaImageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db The database instance
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         The database instance
     * @param oldVersion The old database version
     * @param newVersion The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    /**
     * Inserts a new NASA image into the favorites table.
     *
     * @param image The NasaImage to insert
     * @return The row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertFavorite(NasaImage image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, image.getTitle());
        values.put(COLUMN_DATE, image.getDate());
        values.put(COLUMN_EXPLANATION, image.getExplanation());
        values.put(COLUMN_URL, image.getUrl());
        values.put(COLUMN_HD_URL, image.getHdUrl());
        values.put(COLUMN_MEDIA_TYPE, image.getMediaType());
        values.put(COLUMN_COPYRIGHT, image.getCopyright());

        long result = db.insert(TABLE_FAVORITES, null, values);
        return result;
    }

    /**
     * Retrieves all favorite images from the database.
     *
     * @return A list of all NasaImage favorites
     */
    public List<NasaImage> getAllFavorites() {
        List<NasaImage> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_ID, COLUMN_TITLE, COLUMN_DATE, COLUMN_EXPLANATION,
                COLUMN_URL, COLUMN_HD_URL, COLUMN_MEDIA_TYPE, COLUMN_COPYRIGHT
        };

        Cursor cursor = db.query(
                TABLE_FAVORITES,
                columns,
                null,
                null,
                null,
                null,
                COLUMN_DATE + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                NasaImage image = cursorToNasaImage(cursor);
                favorites.add(image);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return favorites;
    }

    /**
     * Retrieves a favorite image by its date.
     *
     * @param date The date to search for
     * @return The NasaImage if found, null otherwise
     */
    public NasaImage getFavoriteByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_FAVORITES,
                null,
                COLUMN_DATE + " = ?",
                new String[]{date},
                null,
                null,
                null
        );

        NasaImage image = null;
        if (cursor != null && cursor.moveToFirst()) {
            image = cursorToNasaImage(cursor);
            cursor.close();
        }

        return image;
    }

    /**
     * Checks if an image with the given date is already in favorites.
     *
     * @param date The date to check
     * @return true if the image is a favorite, false otherwise
     */
    public boolean isFavorite(String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_FAVORITES,
                new String[]{COLUMN_ID},
                COLUMN_DATE + " = ?",
                new String[]{date},
                null,
                null,
                null
        );

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    /**
     * Deletes a favorite image by its ID.
     *
     * @param id The ID of the image to delete
     * @return The number of rows affected
     */
    public int deleteFavorite(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FAVORITES, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Deletes a favorite image by its date.
     *
     * @param date The date of the image to delete
     * @return The number of rows affected
     */
    public int deleteFavoriteByDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FAVORITES, COLUMN_DATE + " = ?",
                new String[]{date});
    }

    /**
     * Gets the count of favorite images.
     *
     * @return The number of favorites
     */
    public int getFavoritesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FAVORITES, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }

    /**
     * Converts a cursor row to a NasaImage object.
     *
     * @param cursor The cursor positioned at the row to convert
     * @return The NasaImage object
     */
    private NasaImage cursorToNasaImage(Cursor cursor) {
        NasaImage image = new NasaImage();

        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
        int explanationIndex = cursor.getColumnIndex(COLUMN_EXPLANATION);
        int urlIndex = cursor.getColumnIndex(COLUMN_URL);
        int hdUrlIndex = cursor.getColumnIndex(COLUMN_HD_URL);
        int mediaTypeIndex = cursor.getColumnIndex(COLUMN_MEDIA_TYPE);
        int copyrightIndex = cursor.getColumnIndex(COLUMN_COPYRIGHT);

        if (idIndex >= 0) image.setId(cursor.getLong(idIndex));
        if (titleIndex >= 0) image.setTitle(cursor.getString(titleIndex));
        if (dateIndex >= 0) image.setDate(cursor.getString(dateIndex));
        if (explanationIndex >= 0) image.setExplanation(cursor.getString(explanationIndex));
        if (urlIndex >= 0) image.setUrl(cursor.getString(urlIndex));
        if (hdUrlIndex >= 0) image.setHdUrl(cursor.getString(hdUrlIndex));
        if (mediaTypeIndex >= 0) image.setMediaType(cursor.getString(mediaTypeIndex));
        if (copyrightIndex >= 0) image.setCopyright(cursor.getString(copyrightIndex));

        return image;
    }
}
