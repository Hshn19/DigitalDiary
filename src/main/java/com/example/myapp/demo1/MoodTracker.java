package com.example.myapp.demo1;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MoodTracker {

    private final ObservableList<DiaryEntryWithImage> diaryEntries;

    public MoodTracker(ObservableList<DiaryEntryWithImage> diaryEntries) {
        this.diaryEntries = diaryEntries;
    }

    public void openMoodTrackerDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Mood Tracker");

        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Date-Time Pickers
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ComboBox<LocalDateTime> startDateTimePicker = new ComboBox<>();
        ComboBox<LocalDateTime> endDateTimePicker = new ComboBox<>();

        startDateTimePicker.setPromptText("Start Date & Time");
        endDateTimePicker.setPromptText("End Date & Time");

        // Populate available timestamps
        for (DiaryEntryWithImage entry : diaryEntries) {
            startDateTimePicker.getItems().add(entry.getEntryTime());
            endDateTimePicker.getItems().add(entry.getEntryTime());
        }

        Button trackButton = new Button("Track Mood");
        Label resultLabel = new Label();

        // Track Mood Button Action
        trackButton.setOnAction(event -> {
            LocalDateTime startDateTime = startDateTimePicker.getValue();
            LocalDateTime endDateTime = endDateTimePicker.getValue();

            if (startDateTime == null || endDateTime == null) {
                showAlert(Alert.AlertType.ERROR, "Date-Time Error", "Please select both start and end date-times.");
                return;
            }

            if (startDateTime.isAfter(endDateTime)) {
                showAlert(Alert.AlertType.ERROR, "Date-Time Range Error", "Start date-time cannot be after end date-time.");
                return;
            }

            int happyCount = 0;
            int sadCount = 0;
            int neutralCount = 0;

            for (DiaryEntryWithImage entry : diaryEntries) {
                LocalDateTime entryTimestamp = entry.getEntryTime();
                if (!entryTimestamp.isBefore(startDateTime) && !entryTimestamp.isAfter(endDateTime)) {
                    switch (entry.getMood()) {
                        case "Happy 😊" -> happyCount++;
                        case "Sad 😢" -> sadCount++;
                        case "Neutral 😐" -> neutralCount++;
                    }
                }
            }

            resultLabel.setText(String.format("Moods from %s to %s:\nHappy: %d\nSad: %d\nNeutral: %d",
                    startDateTime.format(formatter),
                    endDateTime.format(formatter),
                    happyCount, sadCount, neutralCount));
        });

        layout.getChildren().addAll(new Label("Select Date-Time Range:"), startDateTimePicker, endDateTimePicker, trackButton, resultLabel);

        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


