package com.example.nasaimageoftheday.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nasaimageoftheday.R;

/**
 * Activity displaying information about the application.
 * Shows app version, developer info, and links to NASA API.
 *
 * @author Your Name
 * @version 1.0
 */
public class AboutActivity extends AppCompatActivity {

    /** URL for NASA API */
    private static final String NASA_API_URL = "https://api.nasa.gov/";

    /** URL for NASA APOD website */
    private static final String NASA_APOD_URL = "https://apod.nasa.gov/apod/";

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_about) + " v1.0");
        }

        // Setup version text
        TextView versionText = findViewById(R.id.about_version);
        if (versionText != null) {
            versionText.setText(getString(R.string.version_format, "1.0"));
        }

        // Setup NASA API button
        Button nasaApiButton = findViewById(R.id.btn_nasa_api);
        if (nasaApiButton != null) {
            nasaApiButton.setOnClickListener(v -> openInBrowser(NASA_API_URL));
        }

        // Setup NASA APOD button
        Button nasaApodButton = findViewById(R.id.btn_nasa_apod);
        if (nasaApodButton != null) {
            nasaApodButton.setOnClickListener(v -> openInBrowser(NASA_APOD_URL));
        }
    }

    /**
     * Opens a URL in the device's browser.
     *
     * @param url The URL to open
     */
    private void openInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /**
     * Creates the options menu.
     *
     * @param menu The menu to inflate
     * @return true if the menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    /**
     * Handles options menu item selection.
     *
     * @param item The selected menu item
     * @return true if the event was handled
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the help dialog with instructions.
     */
    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_title)
                .setMessage(R.string.help_about_message)
                .setPositiveButton(R.string.ok, null)
                .setIcon(R.drawable.ic_help)
                .show();
    }
}
