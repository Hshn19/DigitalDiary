package com.example.myapp.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.function.Predicate;

public class SearchManager {

    private final FilteredList<DiaryEntryWithImage> filteredEntries;

    public SearchManager(ObservableList<DiaryEntryWithImage> entries) {
        this.filteredEntries = new FilteredList<>(entries, p -> true);
    }

    public void updateSearch(String query) {
        if (query == null || query.isEmpty()) {
            filteredEntries.setPredicate(p -> true);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredEntries.setPredicate(createPredicate(lowerCaseQuery));
        }
    }

    public FilteredList<DiaryEntryWithImage> getFilteredEntries() {
        return filteredEntries;
    }

    private Predicate<DiaryEntryWithImage> createPredicate(String query) {
        return entry -> entry.getTitle().toLowerCase().contains(query)
                || entry.getContent().toLowerCase().contains(query)
                || (entry.getMood() != null && entry.getMood().toLowerCase().contains(query))
                || entry.getFormattedEntryTime().toLowerCase().contains(query);
    }
}





