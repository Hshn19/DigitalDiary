package com.example.myapp.demo1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiaryEntryWithImage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime entryTime; // Automatically set when the entry is created
    private String title;                 // Title of the diary entry
    private String content;               // Content of the diary entry
    private String mood;                  // Mood associated with the entry
    private List<String> imagePaths;      // List of relative paths to images

    /**
     * Constructor to initialize a diary entry.
     *
     * @param title      The title of the entry.
     * @param content    The content of the entry.
     * @param mood       The mood associated with the entry.
     * @param imagePaths List of image paths (as a List or array).
     */
    public DiaryEntryWithImage(String title, String content, String mood, List<String> imagePaths) {
        this.entryTime = LocalDateTime.now();
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.imagePaths = (imagePaths != null) ? new ArrayList<>(imagePaths) : new ArrayList<>();
    }

    /**
     * Overloaded constructor to accept an array of image paths.
     *
     * @param title      The title of the entry.
     * @param content    The content of the entry.
     * @param mood       The mood associated with the entry.
     * @param imagePaths Array of image paths.
     */
    public DiaryEntryWithImage(String title, String content, String mood, String[] imagePaths) {
        this(title, content, mood, imagePaths != null ? Arrays.asList(imagePaths) : null);
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public String getFormattedEntryTime() {
        return entryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = (imagePaths != null) ? new ArrayList<>(imagePaths) : new ArrayList<>();
    }

    /**
     * Sets image paths from an array.
     *
     * @param imagePaths Array of image paths.
     */
    public void setImagePaths(String[] imagePaths) {
        setImagePaths(imagePaths != null ? Arrays.asList(imagePaths) : null);
    }

    @Override
    public String toString() {
        String imageInfo = (imagePaths.isEmpty()) ? "No images" : imagePaths.size() + " image(s)";
        return String.format("%s (%s) - %s [%s]", title, mood, getFormattedEntryTime(), imageInfo);
    }
}












