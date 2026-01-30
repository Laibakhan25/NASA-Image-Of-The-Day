package com.example.nasaimageoftheday.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nasaimageoftheday.R;
import com.example.nasaimageoftheday.models.NasaImage;

import java.util.List;

/**
 * Custom ArrayAdapter for displaying NasaImage items in a ListView.
 * Uses the ViewHolder pattern for efficient view recycling.
 *
 * @author Your Name
 * @version 1.0
 */
public class NasaImageAdapter extends ArrayAdapter<NasaImage> {

    /** Layout inflater for creating views */
    private LayoutInflater inflater;

    /** Resource ID for the list item layout */
    private int resourceId;

    /**
     * ViewHolder class for efficient view recycling.
     */
    private static class ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView date;
    }

    /**
     * Constructor for NasaImageAdapter.
     *
     * @param context    The context
     * @param resourceId The resource ID for the list item layout
     * @param images     The list of NasaImage objects
     */
    public NasaImageAdapter(@NonNull Context context, int resourceId, @NonNull List<NasaImage> images) {
        super(context, resourceId, images);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resourceId;
    }

    /**
     * Gets a View that displays the data at the specified position.
     *
     * @param position    The position of the item
     * @param convertView The old view to reuse, if possible
     * @param parent      The parent ViewGroup
     * @return A View corresponding to the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(resourceId, parent, false);

            holder = new ViewHolder();
            holder.thumbnail = convertView.findViewById(R.id.item_thumbnail);
            holder.title = convertView.findViewById(R.id.item_title);
            holder.date = convertView.findViewById(R.id.item_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NasaImage image = getItem(position);

        if (image != null) {
            // Set title
            holder.title.setText(image.getTitle());

            // Set date
            holder.date.setText(image.getDate());

            // Load thumbnail using Glide
            if (image.getUrl() != null && !image.getUrl().isEmpty()) {
                // Check if it's a video or image
                if ("video".equals(image.getMediaType())) {
                    // For videos, show a placeholder
                    holder.thumbnail.setImageResource(R.drawable.ic_video_placeholder);
                } else {
                    Glide.with(getContext())
                            .load(image.getUrl())
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_error)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(holder.thumbnail);
                }
            } else {
                holder.thumbnail.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        return convertView;
    }

    /**
     * Updates the adapter data with a new list of images.
     *
     * @param newImages The new list of images
     */
    public void updateData(List<NasaImage> newImages) {
        clear();
        addAll(newImages);
        notifyDataSetChanged();
    }
}
