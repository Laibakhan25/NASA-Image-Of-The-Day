package com.example.nasaimageoftheday.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nasaimageoftheday.R;
import com.example.nasaimageoftheday.adapters.NasaImageAdapter;
import com.example.nasaimageoftheday.database.NasaImageDatabaseHelper;
import com.example.nasaimageoftheday.models.NasaImage;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying and managing favorite NASA images.
 * Shows a ListView of saved favorites with the ability to view details or delete items.
 *
 * @author Your Name
 * @version 1.0
 */
public class FavoritesActivity extends AppCompatActivity {

    /** ListView for displaying favorites */
    private ListView listView;

    /** TextView for empty state */
    private TextView emptyText;

    /** Adapter for the ListView */
    private NasaImageAdapter adapter;

    /** List of favorite images */
    private List<NasaImage> favoritesList;

    /** Database helper */
    private NasaImageDatabaseHelper databaseHelper;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Initialize database helper
        databaseHelper = NasaImageDatabaseHelper.getInstance(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_favorites) + " v1.0");
        }

        // Initialize views
        listView = findViewById(R.id.favorites_list);
        emptyText = findViewById(R.id.empty_text);

        // Initialize list and adapter
        favoritesList = new ArrayList<>();
        adapter = new NasaImageAdapter(this, R.layout.item_nasa_image, favoritesList);
        listView.setAdapter(adapter);

        // Set item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            NasaImage image = favoritesList.get(position);
            Intent intent = new Intent(FavoritesActivity.this, ImageDetailActivity.class);
            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE, image);
            intent.putExtra(ImageDetailActivity.EXTRA_FROM_FAVORITES, true);
            startActivity(intent);
        });

        // Set long click listener for delete
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmation(position);
            return true;
        });

        // Load favorites
        loadFavorites();
    }

    /**
     * Loads favorites from the database.
     */
    private void loadFavorites() {
        favoritesList.clear();
        favoritesList.addAll(databaseHelper.getAllFavorites());
        adapter.notifyDataSetChanged();

        // Update empty state
        updateEmptyState();
    }

    /**
     * Updates the visibility of the empty state text.
     */
    private void updateEmptyState() {
        if (favoritesList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows a confirmation dialog for deleting a favorite.
     *
     * @param position The position of the item to delete
     */
    private void showDeleteConfirmation(int position) {
        NasaImage image = favoritesList.get(position);

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_favorite_title)
                .setMessage(getString(R.string.delete_favorite_message, image.getTitle()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    deleteFavorite(position);
                })
                .setNegativeButton(R.string.cancel, null)
                .setIcon(R.drawable.ic_delete)
                .show();
    }

    /**
     * Deletes a favorite from the database.
     *
     * @param position The position of the item to delete
     */
    private void deleteFavorite(int position) {
        NasaImage image = favoritesList.get(position);
        int deleted = databaseHelper.deleteFavorite(image.getId());

        if (deleted > 0) {
            favoritesList.remove(position);
            adapter.notifyDataSetChanged();
            updateEmptyState();

            Snackbar.make(listView, R.string.favorite_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> {
                        // Undo delete
                        long newId = databaseHelper.insertFavorite(image);
                        if (newId > 0) {
                            image.setId(newId);
                            favoritesList.add(position, image);
                            adapter.notifyDataSetChanged();
                            updateEmptyState();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, R.string.error_deleting_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    /**
     * Creates the options menu.
     *
     * @param menu The menu to inflate
     * @return true if the menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
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
        } else if (id == R.id.action_search) {
            startActivity(new Intent(this, ImageSearchActivity.class));
            return true;
        } else if (id == R.id.action_clear_all) {
            showClearAllConfirmation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows confirmation dialog for clearing all favorites.
     */
    private void showClearAllConfirmation() {
        if (favoritesList.isEmpty()) {
            Toast.makeText(this, R.string.no_favorites_to_clear, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_all_title)
                .setMessage(R.string.clear_all_message)
                .setPositiveButton(R.string.clear, (dialog, which) -> {
                    clearAllFavorites();
                })
                .setNegativeButton(R.string.cancel, null)
                .setIcon(R.drawable.ic_delete)
                .show();
    }

    /**
     * Clears all favorites from the database.
     */
    private void clearAllFavorites() {
        for (NasaImage image : favoritesList) {
            databaseHelper.deleteFavorite(image.getId());
        }
        favoritesList.clear();
        adapter.notifyDataSetChanged();
        updateEmptyState();

        Snackbar.make(listView, R.string.all_favorites_cleared, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Shows the help dialog with instructions.
     */
    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_title)
                .setMessage(R.string.help_favorites_message)
                .setPositiveButton(R.string.ok, null)
                .setIcon(R.drawable.ic_help)
                .show();
    }
}
