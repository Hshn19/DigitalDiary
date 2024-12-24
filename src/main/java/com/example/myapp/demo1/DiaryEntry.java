package com.example.myapp.demo1;

import java.time.LocalDate;

public class DiaryEntry {
    private LocalDate date;
    private String title;
    private String content;

    public DiaryEntry(LocalDate date, String title, String content) {
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
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

    @Override
    public String toString() {
        return date + ": " + title;
    }
}

