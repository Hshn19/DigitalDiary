package com.example.myapp.demo1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiaryEntryWithImage implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDateTime entryTime;
    private String title;
    private String content;
    private MoodTracker.Mood mood;;
    private String imagePath;

    public DiaryEntryWithImage(String title, String content, MoodTracker.Mood mood, String imagePath) {
        this.entryTime = LocalDateTime.now();
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.imagePath = imagePath;
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

    public void setMood(MoodTracker.Mood mood) {
        this.mood = mood;
    }

    public MoodTracker.Mood getMood() {
        return mood;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", title, mood, getFormattedEntryTime());
    }
}







