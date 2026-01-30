package com.example.nasaimageoftheday.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.nasaimageoftheday.models.NasaImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask for fetching NASA Astronomy Picture of the Day (APOD) data from the API.
 * This class handles the network operations in a background thread to avoid blocking the UI.
 *
 * @author Your Name
 * @version 1.0
 */
public class NasaApiTask extends AsyncTask<String, Integer, NasaImage> {

    /** Tag for logging */
    private static final String TAG = "NasaApiTask";

    /** Base URL for NASA APOD API */
    private static final String BASE_URL = "https://api.nasa.gov/planetary/apod";

    /** API Key - Get your own from https://api.nasa.gov/ */
    private static final String API_KEY = "4ZNvHHjOOpcSIVk3zCHw4jyYeGfT2rABWB3LZloz";

    /** Listener interface for API callbacks */
    private NasaApiListener listener;

    /** Error message if something goes wrong */
    private String errorMessage;

    /**
     * Interface for receiving API results.
     */
    public interface NasaApiListener {
        /**
         * Called before the API request starts.
         */
        void onPreExecute();

        /**
         * Called to update progress during download.
         *
         * @param progress Progress value (0-100)
         */
        void onProgressUpdate(int progress);

        /**
         * Called when the API request completes successfully.
         *
         * @param image The retrieved NasaImage object
         */
        void onSuccess(NasaImage image);

        /**
         * Called when the API request fails.
         *
         * @param error The error message
         */
        void onError(String error);
    }

    /**
     * Constructor with listener.
     *
     * @param listener The listener to receive callbacks
     */
    public NasaApiTask(NasaApiListener listener) {
        this.listener = listener;
    }

    /**
     * Called before the background task starts.
     * Notifies the listener to show loading UI.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null) {
            listener.onPreExecute();
        }
    }

    /**
     * Performs the API request in the background.
     *
     * @param params The date string to fetch (YYYY-MM-DD format)
     * @return The NasaImage object, or null if an error occurred
     */
    @Override
    protected NasaImage doInBackground(String... params) {
        if (params.length == 0) {
            errorMessage = "No date provided";
            return null;
        }

        String date = params[0];
        String urlString = BASE_URL + "?api_key=" + API_KEY + "&date=" + date;

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Update progress - Starting connection
            publishProgress(10);

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.connect();

            // Update progress - Connected
            publishProgress(30);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                errorMessage = "HTTP Error: " + responseCode;
                
                // Try to read error message from response
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    reader = new BufferedReader(new InputStreamReader(errorStream));
                    StringBuilder errorBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorBuilder.append(line);
                    }
                    try {
                        JSONObject errorJson = new JSONObject(errorBuilder.toString());
                        if (errorJson.has("msg")) {
                            errorMessage = errorJson.getString("msg");
                        }
                    } catch (JSONException e) {
                        // Use default error message
                    }
                }
                return null;
            }

            // Update progress - Reading data
            publishProgress(50);

            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Update progress - Parsing JSON
            publishProgress(70);

            String jsonResponse = response.toString();
            NasaImage image = parseJsonResponse(jsonResponse);

            // Update progress - Complete
            publishProgress(100);

            return image;

        } catch (IOException e) {
            Log.e(TAG, "Network error: " + e.getMessage());
            errorMessage = "Network error: " + e.getMessage();
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            errorMessage = "Error parsing response: " + e.getMessage();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing reader: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Called when progress updates are published.
     *
     * @param values The progress values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (listener != null && values.length > 0) {
            listener.onProgressUpdate(values[0]);
        }
    }

    /**
     * Called when the background task completes.
     *
     * @param result The NasaImage result, or null if an error occurred
     */
    @Override
    protected void onPostExecute(NasaImage result) {
        super.onPostExecute(result);
        if (listener != null) {
            if (result != null) {
                listener.onSuccess(result);
            } else {
                listener.onError(errorMessage != null ? errorMessage : "Unknown error occurred");
            }
        }
    }

    /**
     * Parses the JSON response from the NASA API.
     *
     * @param jsonString The JSON response string
     * @return The parsed NasaImage object
     * @throws JSONException If parsing fails
     */
    private NasaImage parseJsonResponse(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);

        NasaImage image = new NasaImage();
        image.setTitle(json.optString("title", "No Title"));
        image.setDate(json.optString("date", ""));
        image.setExplanation(json.optString("explanation", ""));
        image.setUrl(json.optString("url", ""));
        image.setHdUrl(json.optString("hdurl", ""));
        image.setMediaType(json.optString("media_type", "image"));
        image.setCopyright(json.optString("copyright", ""));

        return image;
    }
}
