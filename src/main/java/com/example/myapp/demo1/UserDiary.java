package com.example.myapp.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserDiary {
    private final ObservableList<DiaryEntryWithImage> diaryEntries;
    private final ObservableList<RecycleBinEntry> recycleBin;

    public UserDiary() {
        this.diaryEntries = FXCollections.observableArrayList();
        this.recycleBin = FXCollections.observableArrayList();
    }

    public ObservableList<DiaryEntryWithImage> getDiaryEntries() {
        return diaryEntries;
    }

    public ObservableList<RecycleBinEntry> getRecycleBin() {
        return recycleBin;
    }
}

