package com.example.nasaimageoftheday.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nasaimageoftheday.R;
import com.example.nasaimageoftheday.database.NasaImageDatabaseHelper;
import com.example.nasaimageoftheday.utils.PreferencesHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * Main Activity with Navigation Drawer for the NASA Image of the Day application.
 * Provides navigation to different features of the app including image search,
 * favorites, and about sections.
 *
 * @author Your Name
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** DrawerLayout for navigation drawer */
    private DrawerLayout drawerLayout;

    /** Toolbar reference */
    private Toolbar toolbar;

    /** NavigationView reference */
    private NavigationView navigationView;

    /** Preferences helper */
    private PreferencesHelper preferencesHelper;

    /** Database helper */
    private NasaImageDatabaseHelper databaseHelper;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after being shut down,
     *                           this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize helpers
        preferencesHelper = PreferencesHelper.getInstance(this);
        databaseHelper = NasaImageDatabaseHelper.getInstance(this);

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name) + " v1.0");
        }

        // Setup drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup navigation view
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Check for first launch
        if (preferencesHelper.isFirstLaunch()) {
            showWelcomeMessage();
            preferencesHelper.setFirstLaunchComplete();
        }

        // Setup main content click listeners
        setupMainContent();
    }

    /**
     * Sets up the main content area with clickable cards.
     */
    private void setupMainContent() {
        // Search card
        View searchCard = findViewById(R.id.card_search);
        if (searchCard != null) {
            searchCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ImageSearchActivity.class);
                startActivity(intent);
            });
        }

        // Favorites card
        View favoritesCard = findViewById(R.id.card_favorites);
        if (favoritesCard != null) {
            favoritesCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
            });
        }

        // About card
        View aboutCard = findViewById(R.id.card_about);
        if (aboutCard != null) {
            aboutCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            });
        }
    }

    /**
     * Updates the favorites count displayed on the main screen.
     */
    private void updateFavoritesCount() {
        TextView favoritesCount = findViewById(R.id.favorites_count);
        if (favoritesCount != null) {
            int count = databaseHelper.getFavoritesCount();
            favoritesCount.setText(getString(R.string.favorites_count_format, count));
        }
    }

    /**
     * Updates the last search date displayed on the main screen.
     */
    private void updateLastSearchDate() {
        TextView lastSearchText = findViewById(R.id.last_search_date);
        if (lastSearchText != null) {
            String lastDate = preferencesHelper.getLastSearchedDate();
            if (lastDate != null && !lastDate.isEmpty()) {
                lastSearchText.setText(getString(R.string.last_search_format, lastDate));
                lastSearchText.setVisibility(View.VISIBLE);
            } else {
                lastSearchText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Shows a welcome message for first-time users.
     */
    private void showWelcomeMessage() {
        Snackbar.make(
                findViewById(R.id.drawer_layout),
                R.string.welcome_message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.got_it, v -> {
            // Dismiss
        }).show();
    }

    /**
     * Called when the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateFavoritesCount();
        updateLastSearchDate();
    }

    /**
     * Called when a navigation item is selected.
     *
     * @param item The selected menu item
     * @return true if the event was handled
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            startActivity(new Intent(this, ImageSearchActivity.class));
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_help) {
            showHelpDialog();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Creates the options menu.
     *
     * @param menu The menu to inflate
     * @return true if the menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.action_search) {
            startActivity(new Intent(this, ImageSearchActivity.class));
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
                .setMessage(R.string.help_main_message)
                .setPositiveButton(R.string.ok, null)
                .setIcon(R.drawable.ic_help)
                .show();
    }

    /**
     * Handles back button press.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
