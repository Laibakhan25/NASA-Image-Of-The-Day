package com.example.nasaimageoftheday.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nasaimageoftheday.R;
import com.example.nasaimageoftheday.database.NasaImageDatabaseHelper;
import com.example.nasaimageoftheday.fragments.ImageDetailFragment;
import com.example.nasaimageoftheday.models.NasaImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * Activity for displaying detailed information about a NASA image.
 * Uses a Fragment to display the image details.
 *
 * @author Your Name
 * @version 1.0
 */
public class ImageDetailActivity extends AppCompatActivity {

    /** Intent extra key for NasaImage */
    public static final String EXTRA_IMAGE = "extra_image";

    /** Intent extra key for indicating if opened from favorites */
    public static final String EXTRA_FROM_FAVORITES = "extra_from_favorites";

    /** The NASA image being displayed */
    private NasaImage nasaImage;

    /** Flag indicating if this was opened from favorites */
    private boolean fromFavorites;

    /** Database helper */
    private NasaImageDatabaseHelper databaseHelper;

    /** Flag indicating if image is a favorite */
    private boolean isFavorite;

    /** FAB for favorite action */
    private FloatingActionButton fabFavorite;

    /** Fragment container */
    private View fragmentContainer;

    /** Detail views (used when fragment is not showing) */
    private ScrollView detailScrollView;
    private ImageView detailImage;
    private TextView detailTitle;
    private TextView detailDate;
    private TextView detailExplanation;
    private TextView detailCopyright;
    private Button btnViewHd;
    private Button btnViewUrl;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        // Initialize database helper
        databaseHelper = NasaImageDatabaseHelper.getInstance(this);

        // Get intent extras
        nasaImage = (NasaImage) getIntent().getSerializableExtra(EXTRA_IMAGE);
        fromFavorites = getIntent().getBooleanExtra(EXTRA_FROM_FAVORITES, false);

        if (nasaImage == null) {
            Toast.makeText(this, R.string.error_no_image, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_image_detail) + " v1.0");
        }

        // Initialize views
        initializeViews();

        // Check if using fragment or direct views
        fragmentContainer = findViewById(R.id.fragment_container);
        if (fragmentContainer != null) {
            // Use fragment for tablet layout
            loadFragment();
        } else {
            // Use direct views for phone layout
            displayImageDetails();
        }

        // Check favorite status
        isFavorite = databaseHelper.isFavorite(nasaImage.getDate());
        updateFavoriteButton();
    }

    /**
     * Initializes UI components.
     */
    private void initializeViews() {
        fabFavorite = findViewById(R.id.fab_favorite);
        detailScrollView = findViewById(R.id.detail_scroll_view);
        detailImage = findViewById(R.id.detail_image);
        detailTitle = findViewById(R.id.detail_title);
        detailDate = findViewById(R.id.detail_date);
        detailExplanation = findViewById(R.id.detail_explanation);
        detailCopyright = findViewById(R.id.detail_copyright);
        btnViewHd = findViewById(R.id.btn_view_hd);
        btnViewUrl = findViewById(R.id.btn_view_url);

        // Setup FAB click listener
        if (fabFavorite != null) {
            fabFavorite.setOnClickListener(v -> toggleFavorite());
        }
    }

    /**
     * Loads the ImageDetailFragment.
     */
    private void loadFragment() {
        ImageDetailFragment fragment = ImageDetailFragment.newInstance(nasaImage);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Displays image details directly (non-fragment mode).
     */
    private void displayImageDetails() {
        if (detailTitle != null) {
            detailTitle.setText(nasaImage.getTitle());
        }

        if (detailDate != null) {
            detailDate.setText(nasaImage.getDate());
        }

        if (detailExplanation != null) {
            detailExplanation.setText(nasaImage.getExplanation());
        }

        if (detailCopyright != null) {
            if (nasaImage.getCopyright() != null && !nasaImage.getCopyright().isEmpty()) {
                detailCopyright.setVisibility(View.VISIBLE);
                detailCopyright.setText(getString(R.string.copyright_format, nasaImage.getCopyright()));
            } else {
                detailCopyright.setVisibility(View.GONE);
            }
        }

        // Load image
        if (detailImage != null) {
            if ("video".equals(nasaImage.getMediaType())) {
                detailImage.setImageResource(R.drawable.ic_video_placeholder);
            } else if (nasaImage.getUrl() != null && !nasaImage.getUrl().isEmpty()) {
                Glide.with(this)
                        .load(nasaImage.getUrl())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(detailImage);
            }
        }

        // Setup buttons
        if (btnViewHd != null) {
            if (nasaImage.getHdUrl() != null && !nasaImage.getHdUrl().isEmpty()) {
                btnViewHd.setVisibility(View.VISIBLE);
                btnViewHd.setOnClickListener(v -> openInBrowser(nasaImage.getHdUrl()));
            } else {
                btnViewHd.setVisibility(View.GONE);
            }
        }

        if (btnViewUrl != null) {
            if (nasaImage.getUrl() != null && !nasaImage.getUrl().isEmpty()) {
                btnViewUrl.setVisibility(View.VISIBLE);
                if ("video".equals(nasaImage.getMediaType())) {
                    btnViewUrl.setText(R.string.watch_video);
                } else {
                    btnViewUrl.setText(R.string.view_image);
                }
                btnViewUrl.setOnClickListener(v -> openInBrowser(nasaImage.getUrl()));
            } else {
                btnViewUrl.setVisibility(View.GONE);
            }
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
        if (nasaImage == null) return;

        if (isFavorite) {
            // Remove from favorites
            int deleted = databaseHelper.deleteFavoriteByDate(nasaImage.getDate());
            if (deleted > 0) {
                isFavorite = false;
                updateFavoriteButton();
                Snackbar.make(fabFavorite, R.string.removed_from_favorites, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            // Add to favorites
            long id = databaseHelper.insertFavorite(nasaImage);
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
     * Updates the favorite button appearance.
     */
    private void updateFavoriteButton() {
        if (fabFavorite != null) {
            if (isFavorite) {
                fabFavorite.setImageResource(R.drawable.ic_favorite_filled);
            } else {
                fabFavorite.setImageResource(R.drawable.ic_favorite_outline);
            }
        }
    }

    /**
     * Creates the options menu.
     *
     * @param menu The menu to inflate
     * @return true if the menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        // Update delete visibility based on where we came from
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (deleteItem != null) {
            deleteItem.setVisible(fromFavorites);
        }

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
        } else if (id == R.id.action_delete) {
            showDeleteConfirmation();
            return true;
        } else if (id == R.id.action_share) {
            shareImage();
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
                .setMessage(R.string.help_detail_message)
                .setPositiveButton(R.string.ok, null)
                .setIcon(R.drawable.ic_help)
                .show();
    }

    /**
     * Shows confirmation dialog for deleting the favorite.
     */
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_favorite_title)
                .setMessage(getString(R.string.delete_favorite_message, nasaImage.getTitle()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    deleteFavorite();
                })
                .setNegativeButton(R.string.cancel, null)
                .setIcon(R.drawable.ic_delete)
                .show();
    }

    /**
     * Deletes the current image from favorites.
     */
    private void deleteFavorite() {
        int deleted = databaseHelper.deleteFavoriteByDate(nasaImage.getDate());
        if (deleted > 0) {
            Toast.makeText(this, R.string.favorite_deleted, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.error_deleting_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shares the image via Android's share intent.
     */
    private void shareImage() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, nasaImage.getTitle());
        
        String shareText = nasaImage.getTitle() + "\n\n" +
                getString(R.string.date_label) + ": " + nasaImage.getDate() + "\n\n" +
                nasaImage.getExplanation() + "\n\n" +
                getString(R.string.view_at) + ": " + nasaImage.getUrl();
        
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
    }
}
