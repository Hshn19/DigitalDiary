package com.example.myapp.demo1;

import java.time.LocalDateTime;

public class RecycleBinEntry {
    private DiaryEntry diaryEntry;
    private LocalDateTime deletedAt;

    public RecycleBinEntry(DiaryEntry diaryEntry, LocalDateTime deletedAt) {
        this.diaryEntry = diaryEntry;
        this.deletedAt = deletedAt;
    }

    public DiaryEntry getDiaryEntry() {
        return diaryEntry;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public String toString() {
        return diaryEntry.getDate() + ": " + diaryEntry.getTitle() + " (Deleted on: " + deletedAt.toLocalDate() + ")";
    }
}

