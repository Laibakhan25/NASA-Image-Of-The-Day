package com.example.nasaimageoftheday.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nasaimageoftheday.R;
import com.example.nasaimageoftheday.database.NasaImageDatabaseHelper;
import com.example.nasaimageoftheday.models.NasaImage;
import com.example.nasaimageoftheday.utils.NasaApiTask;
import com.example.nasaimageoftheday.utils.PreferencesHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for searching NASA Astronomy Picture of the Day by date.
 * Allows users to select a date and fetch the corresponding image from NASA's API.
 *
 * @author Your Name
 * @version 1.0
 */
public class ImageSearchActivity extends AppCompatActivity implements NasaApiTask.NasaApiListener {

    /** UI Components */
    private EditText dateEditText;
    private Button searchButton;
    private Button pickDateButton;
    private ProgressBar progressBar;
    private View resultContainer;
    private ImageView resultImage;
    private TextView resultTitle;
    private TextView resultDate;
    private TextView resultExplanation;
    private Button viewHdButton;
    private Button viewUrlButton;
    private FloatingActionButton fabFavorite;

    /** Calendar for date picker */
    private Calendar selectedCalendar;

    /** Date format for display */
    private SimpleDateFormat dateFormat;

    /** Current NASA image */
    private NasaImage currentImage;

    /** Database helper */
    private NasaImageDatabaseHelper databaseHelper;

    /** Preferences helper */
    private PreferencesHelper preferencesHelper;

    /** Flag indicating if current image is a favorite */
    private boolean isFavorite;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        // Initialize helpers
        databaseHelper = NasaImageDatabaseHelper.getInstance(this);
        preferencesHelper = PreferencesHelper.getInstance(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_image_search) + " v1.0");
        }

        // Initialize date format
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedCalendar = Calendar.getInstance();

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Load last searched date if available
        loadLastSearchedDate();
    }

    /**
     * Initializes all UI components.
     */
    private void initializeViews() {
        dateEditText = findViewById(R.id.edit_date);
        searchButton = findViewById(R.id.btn_search);
        pickDateButton = findViewById(R.id.btn_pick_date);
        progressBar = findViewById(R.id.progress_bar);
        resultContainer = findViewById(R.id.result_container);
        resultImage = findViewById(R.id.result_image);
        resultTitle = findViewById(R.id.result_title);
        resultDate = findViewById(R.id.result_date);
        resultExplanation = findViewById(R.id.result_explanation);
        viewHdButton = findViewById(R.id.btn_view_hd);
        viewUrlButton = findViewById(R.id.btn_view_url);
        fabFavorite = findViewById(R.id.fab_favorite);

        // Initially hide result container
        resultContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Sets up click listeners for buttons.
     */
    private void setupClickListeners() {
        // Date picker button
        pickDateButton.setOnClickListener(v -> showDatePicker());

        // Also allow clicking on EditText to show date picker
        dateEditText.setOnClickListener(v -> showDatePicker());
        dateEditText.setFocusable(false);

        // Search button
        searchButton.setOnClickListener(v -> performSearch());

        // View HD button
        viewHdButton.setOnClickListener(v -> {
            if (currentImage != null && currentImage.getHdUrl() != null) {
                openInBrowser(currentImage.getHdUrl());
            }
        });

        // View URL button
        viewUrlButton.setOnClickListener(v -> {
            if (currentImage != null && currentImage.getUrl() != null) {
                openInBrowser(currentImage.getUrl());
            }
        });

        // Favorite FAB
        fabFavorite.setOnClickListener(v -> toggleFavorite());

        // Image click to view details
        resultImage.setOnClickListener(v -> {
            if (currentImage != null) {
                Intent intent = new Intent(this, ImageDetailActivity.class);
                intent.putExtra(ImageDetailActivity.EXTRA_IMAGE, currentImage);
                startActivity(intent);
            }
        });
    }

    /**
     * Shows the date picker dialog.
     */
    private void showDatePicker() {
        // Set max date to today
        Calendar maxDate = Calendar.getInstance();

        // Set min date to June 16, 1995 (first APOD)
        Calendar minDate = Calendar.getInstance();
        minDate.set(1995, Calendar.JUNE, 16);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(year, month, dayOfMonth);
                    String dateString = dateFormat.format(selectedCalendar.getTime());
                    dateEditText.setText(dateString);
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    /**
     * Performs the NASA API search.
     */
    private void performSearch() {
        String date = dateEditText.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(this, R.string.error_no_date, Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the search date
        preferencesHelper.saveLastSearchedDate(date);

        // Execute API task
        new NasaApiTask(this).execute(date);
    }

    /**
     * Loads the last searched date from preferences.
     */
    private void loadLastSearchedDate() {
        String lastDate = preferencesHelper.getLastSearchedDate();
        if (lastDate != null && !lastDate.isEmpty()) {
            dateEditText.setText(lastDate);
            // Parse the date into calendar
            try {
                selectedCalendar.setTime(dateFormat.parse(lastDate));
            } catch (Exception e) {
                // Use current date if parsing fails
            }
        } else {
            // Set to today's date
            dateEditText.setText(dateFormat.format(Calendar.getInstance().getTime()));
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
     * Toggles the favorite status of the current image.
     */
    private void toggleFavorite() {
        if (currentImage == null) return;

        if (isFavorite) {
            // Remove from favorites
            int deleted = databaseHelper.deleteFavoriteByDate(currentImage.getDate());
            if (deleted > 0) {
                isFavorite = false;
                updateFavoriteButton();
                Snackbar.make(fabFavorite, R.string.removed_from_favorites, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            // Add to favorites
            long id = databaseHelper.insertFavorite(currentImage);
            if (id > 0) {
                isFavorite = true;
                updateFavoriteButton();
                Snackbar.make(fabFavorite, R.string.added_to_favorites, Snackbar.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_saving_favorite, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Updates the favorite button appearance based on favorite status.
     */
    private void updateFavoriteButton() {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_outline);
        }
    }

    /**
     * Displays the search result.
     *
     * @param image The NasaImage to display
     */
    private void displayResult(NasaImage image) {
        currentImage = image;

        // Check if it's a favorite
        isFavorite = databaseHelper.isFavorite(image.getDate());
        updateFavoriteButton();

        // Show result container
        resultContainer.setVisibility(View.VISIBLE);
        fabFavorite.setVisibility(View.VISIBLE);

        // Set title and date
        resultTitle.setText(image.getTitle());
        resultDate.setText(image.getDate());

        // Set explanation (truncated for list view)
        String explanation = image.getExplanation();
        if (explanation.length() > 200) {
            explanation = explanation.substring(0, 200) + "...";
        }
        resultExplanation.setText(explanation);

        // Load image
        if ("video".equals(image.getMediaType())) {
            resultImage.setImageResource(R.drawable.ic_video_placeholder);
            viewUrlButton.setText(R.string.watch_video);
        } else {
            Glide.with(this)
                    .load(image.getUrl())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(resultImage);
            viewUrlButton.setText(R.string.view_image);
        }

        // Show/hide HD button
        if (image.getHdUrl() != null && !image.getHdUrl().isEmpty()) {
            viewHdButton.setVisibility(View.VISIBLE);
        } else {
            viewHdButton.setVisibility(View.GONE);
        }

        // Save last viewed image
        preferencesHelper.saveLastViewedImage(image.getUrl(), image.getTitle());
    }

    // NasaApiListener implementation

    /**
     * Called before the API request starts.
     */
    @Override
    public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        resultContainer.setVisibility(View.GONE);
        fabFavorite.setVisibility(View.GONE);
        searchButton.setEnabled(false);
    }

    /**
     * Called to update progress.
     *
     * @param progress The progress value (0-100)
     */
    @Override
    public void onProgressUpdate(int progress) {
        progressBar.setProgress(progress);
    }

    /**
     * Called when the API request succeeds.
     *
     * @param image The retrieved NasaImage
     */
    @Override
    public void onSuccess(NasaImage image) {
        progressBar.setVisibility(View.GONE);
        searchButton.setEnabled(true);
        displayResult(image);
        Toast.makeText(this, R.string.image_loaded, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the API request fails.
     *
     * @param error The error message
     */
    @Override
    public void onError(String error) {
        progressBar.setVisibility(View.GONE);
        searchButton.setEnabled(true);
        Toast.makeText(this, getString(R.string.error_loading_image, error), Toast.LENGTH_LONG).show();
    }

    /**
     * Creates the options menu.
     *
     * @param menu The menu to inflate
     * @return true if the menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
        } else if (id == R.id.action_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
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
                .setMessage(R.string.help_search_message)
                .setPositiveButton(R.string.ok, null)
                .setIcon(R.drawable.ic_help)
                .show();
    }
}
