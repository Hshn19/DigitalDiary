package com.example.myapp.demo1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RecycleBinEntry implements Serializable {
    private final DiaryEntryWithImage diaryEntry;
    private final LocalDateTime deletedAt;

    public RecycleBinEntry(DiaryEntryWithImage diaryEntry, LocalDateTime deletedAt) {
        this.diaryEntry = diaryEntry;
        this.deletedAt = deletedAt;
    }

    public DiaryEntryWithImage getDiaryEntry() {
        return diaryEntry;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s - Deleted on: %s", diaryEntry.toString(), deletedAt.format(formatter));
    }
}

