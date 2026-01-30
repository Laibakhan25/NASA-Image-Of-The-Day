package com.example.nasaimageoftheday.models;

import java.io.Serializable;

/**
 * Model class representing a NASA Astronomy Picture of the Day (APOD).
 * This class holds all the information retrieved from the NASA APOD API.
 * Implements Serializable to allow passing between activities.
 *
 * @author Your Name
 * @version 1.0
 */
public class NasaImage implements Serializable {

    /** Unique identifier for database storage */
    private long id;

    /** Title of the astronomy picture */
    private String title;

    /** Date of the picture in YYYY-MM-DD format */
    private String date;

    /** Description/explanation of the picture */
    private String explanation;

    /** Standard resolution URL of the image */
    private String url;

    /** High definition URL of the image */
    private String hdUrl;

    /** Media type (image or video) */
    private String mediaType;

    /** Copyright information if available */
    private String copyright;

    /**
     * Default constructor for NasaImage.
     */
    public NasaImage() {
    }

    /**
     * Constructor with all fields.
     *
     * @param title       The title of the image
     * @param date        The date of the image
     * @param explanation The explanation/description
     * @param url         The standard URL
     * @param hdUrl       The HD URL
     * @param mediaType   The media type (image/video)
     * @param copyright   The copyright information
     */
    public NasaImage(String title, String date, String explanation, String url, 
                     String hdUrl, String mediaType, String copyright) {
        this.title = title;
        this.date = date;
        this.explanation = explanation;
        this.url = url;
        this.hdUrl = hdUrl;
        this.mediaType = mediaType;
        this.copyright = copyright;
    }

    // Getters and Setters

    /**
     * Gets the unique identifier.
     * @return The database ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier.
     * @param id The database ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the title of the image.
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the image.
     * @param title The title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the date of the image.
     * @return The date in YYYY-MM-DD format
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the image.
     * @param date The date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the explanation of the image.
     * @return The explanation text
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Sets the explanation of the image.
     * @param explanation The explanation to set
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /**
     * Gets the standard URL of the image.
     * @return The URL string
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the standard URL of the image.
     * @param url The URL to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the HD URL of the image.
     * @return The HD URL string
     */
    public String getHdUrl() {
        return hdUrl;
    }

    /**
     * Sets the HD URL of the image.
     * @param hdUrl The HD URL to set
     */
    public void setHdUrl(String hdUrl) {
        this.hdUrl = hdUrl;
    }

    /**
     * Gets the media type.
     * @return The media type (image or video)
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Sets the media type.
     * @param mediaType The media type to set
     */
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Gets the copyright information.
     * @return The copyright string
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Sets the copyright information.
     * @param copyright The copyright to set
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * Returns a string representation of the NasaImage.
     * @return String containing title and date
     */
    @Override
    public String toString() {
        return title + " (" + date + ")";
    }
}
