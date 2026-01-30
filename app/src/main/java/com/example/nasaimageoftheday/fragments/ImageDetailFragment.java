package com.example.nasaimageoftheday.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nasaimageoftheday.R;
import com.example.nasaimageoftheday.models.NasaImage;

/**
 * Fragment for displaying detailed information about a NASA image.
 * Shows the image, title, date, explanation, and links to view HD version.
 *
 * @author Your Name
 * @version 1.0
 */
public class ImageDetailFragment extends Fragment {

    /** Argument key for passing NasaImage */
    private static final String ARG_IMAGE = "nasa_image";

    /** The NASA image to display */
    private NasaImage nasaImage;

    /** UI components */
    private ImageView imageView;
    private TextView titleText;
    private TextView dateText;
    private TextView explanationText;
    private TextView copyrightText;
    private Button viewHdButton;
    private Button viewUrlButton;

    /**
     * Creates a new instance of ImageDetailFragment with the specified image.
     *
     * @param image The NasaImage to display
     * @return A new instance of ImageDetailFragment
     */
    public static ImageDetailFragment newInstance(NasaImage image) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Default constructor required for fragment.
     */
    public ImageDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-created, this contains previous state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nasaImage = (NasaImage) getArguments().getSerializable(ARG_IMAGE);
        }
    }

    /**
     * Called to create the fragment's view hierarchy.
     *
     * @param inflater           The LayoutInflater object
     * @param container          The parent view
     * @param savedInstanceState Previous state
     * @return The fragment's root view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_detail, container, false);
    }

    /**
     * Called after the view is created.
     *
     * @param view               The fragment's root view
     * @param savedInstanceState Previous state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        imageView = view.findViewById(R.id.detail_image);
        titleText = view.findViewById(R.id.detail_title);
        dateText = view.findViewById(R.id.detail_date);
        explanationText = view.findViewById(R.id.detail_explanation);
        copyrightText = view.findViewById(R.id.detail_copyright);
        viewHdButton = view.findViewById(R.id.btn_view_hd);
        viewUrlButton = view.findViewById(R.id.btn_view_url);

        // Populate views with data
        if (nasaImage != null) {
            displayImageDetails();
        }
    }

    /**
     * Displays the image details in the UI components.
     */
    private void displayImageDetails() {
        // Set title
        titleText.setText(nasaImage.getTitle());

        // Set date
        dateText.setText(nasaImage.getDate());

        // Set explanation
        explanationText.setText(nasaImage.getExplanation());

        // Set copyright if available
        if (nasaImage.getCopyright() != null && !nasaImage.getCopyright().isEmpty()) {
            copyrightText.setVisibility(View.VISIBLE);
            copyrightText.setText(getString(R.string.copyright_format, nasaImage.getCopyright()));
        } else {
            copyrightText.setVisibility(View.GONE);
        }

        // Load image
        if ("video".equals(nasaImage.getMediaType())) {
            imageView.setImageResource(R.drawable.ic_video_placeholder);
        } else if (nasaImage.getUrl() != null && !nasaImage.getUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(nasaImage.getUrl())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }

        // Setup HD button
        if (nasaImage.getHdUrl() != null && !nasaImage.getHdUrl().isEmpty()) {
            viewHdButton.setVisibility(View.VISIBLE);
            viewHdButton.setOnClickListener(v -> openInBrowser(nasaImage.getHdUrl()));
        } else {
            viewHdButton.setVisibility(View.GONE);
        }

        // Setup URL button
        if (nasaImage.getUrl() != null && !nasaImage.getUrl().isEmpty()) {
            viewUrlButton.setVisibility(View.VISIBLE);
            viewUrlButton.setOnClickListener(v -> openInBrowser(nasaImage.getUrl()));
        } else {
            viewUrlButton.setVisibility(View.GONE);
        }
    }

    /**
     * Opens a URL in the device's default browser.
     *
     * @param url The URL to open
     */
    private void openInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /**
     * Updates the displayed image.
     *
     * @param image The new NasaImage to display
     */
    public void updateImage(NasaImage image) {
        this.nasaImage = image;
        if (getView() != null) {
            displayImageDetails();
        }
    }
}
