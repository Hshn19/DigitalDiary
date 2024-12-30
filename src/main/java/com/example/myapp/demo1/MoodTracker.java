package com.example.myapp.demo1;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MoodTracker {

    private final ObservableList<DiaryEntryWithImage> diaryEntries;

    public enum Mood {
        HAPPY, EXCITED, CONTENT, NEUTRAL, TIRED, STRESSED, SAD, ANGRY;

        public <V, K> Map<K,V> toUpperCase() {
            return Map.of();
        }

        public String toLowerCase() {
            return "";
        }
    }

    public MoodTracker(ObservableList<DiaryEntryWithImage> diaryEntries) {
        this.diaryEntries = diaryEntries;
    }

    public void showMoodTrackerDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Mood Tracker");

        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        ComboBox<LocalDate> startDatePicker = new ComboBox<>();
        ComboBox<LocalDate> endDatePicker = new ComboBox<>();

        startDatePicker.setPromptText("Start Date");
        endDatePicker.setPromptText("End Date");

        // Populate date pickers with available dates from diary entries
        for (DiaryEntryWithImage entry : diaryEntries) {
            LocalDate entryDate = entry.getEntryTime().toLocalDate();
            if (!startDatePicker.getItems().contains(entryDate)) {
                startDatePicker.getItems().add(entryDate);
                endDatePicker.getItems().add(entryDate);
            }
        }

        Button trackButton = new Button("Track Mood");
        trackButton.setOnAction(event -> showMoodChart(startDatePicker.getValue(), endDatePicker.getValue()));

        layout.getChildren().addAll(new Label("Select Date Range:"), startDatePicker, endDatePicker, trackButton);

        Scene scene = new Scene(layout, 300, 200);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showMoodChart(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Date Selection Error", "Please select both start and end dates.");
            return;
        }

        Map<Mood, Integer> moodCounts = countMoods(startDate, endDate);

        Stage chartStage = new Stage();
        chartStage.setTitle("Mood Distribution");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Mood Distribution from " + startDate + " to " + endDate);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Mood Frequency");

        for (Map.Entry<Mood, Integer> entry : moodCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        barChart.getData().add(series);

        Scene scene = new Scene(barChart, 800, 600);
        chartStage.setScene(scene);
        chartStage.show();
    }

    private Map<Mood, Integer> countMoods(LocalDate startDate, LocalDate endDate) {
        Map<Mood, Integer> moodCounts = new HashMap<>();
        for (DiaryEntryWithImage entry : diaryEntries) {
            LocalDate entryDate = entry.getEntryTime().toLocalDate();
            if ((entryDate.isEqual(startDate) || entryDate.isAfter(startDate)) &&
                    (entryDate.isEqual(endDate) || entryDate.isBefore(endDate))) {
                Mood mood = entry.getMood();
                moodCounts.put(mood, moodCounts.getOrDefault(mood, 0) + 1);
            }
        }
        return moodCounts;
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}









