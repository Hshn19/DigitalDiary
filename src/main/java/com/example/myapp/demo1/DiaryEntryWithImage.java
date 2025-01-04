package com.example.myapp.demo1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DiaryEntryWithImage implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime entryTime; // Timestamp of the diary entry
    private String title;
    private String content;
    private String mood;
    private List<String> imagePaths;

    public DiaryEntryWithImage(String title, String content, String mood, List<String> imagePaths, LocalDateTime entryTime) {
        this.entryTime = entryTime != null ? entryTime : LocalDateTime.now(); // Use provided entryTime or fallback to current time
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.imagePaths = imagePaths != null ? imagePaths : List.of();
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public String getFormattedEntryTime() {
        return entryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
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
        this.imagePaths = imagePaths != null ? imagePaths : List.of();
    }

    @Override
    public String toString() {
        return title + " (Date: " + getFormattedEntryTime() + ")";
    }
}














