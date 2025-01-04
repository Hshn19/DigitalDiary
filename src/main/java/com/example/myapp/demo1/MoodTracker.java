package com.example.myapp.demo1;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoodTracker {

    private final ObservableList<DiaryEntryWithImage> diaryEntries;
    private final Map<String, String> moodColors; // Map for mood-color association

    public MoodTracker(ObservableList<DiaryEntryWithImage> diaryEntries) {
        this.diaryEntries = diaryEntries;
        this.moodColors = initializeMoodColors(); // Initialize mood-color mapping
    }

    public void showMoodTrackerDialog() {
        Stage stage = new Stage();
        stage.setTitle("Mood Tracker - Bubble Chart");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Mood Tracker - Select Date Range");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        HBox datePickers = new HBox(10);
        datePickers.getChildren().addAll(
                new Label("Start Date:"), startDatePicker,
                new Label("End Date:"), endDatePicker
        );

        Button trackButton = new Button("Generate Mood Chart");
        trackButton.setOnAction(e -> generateMoodBubbleChart(startDatePicker.getValue(), endDatePicker.getValue(), stage));

        layout.getChildren().addAll(titleLabel, datePickers, trackButton);

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void generateMoodBubbleChart(LocalDate startDate, LocalDate endDate, Stage stage) {
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Date Selection Error", "Both start and end dates must be selected.");
            return;
        }

        // Filter diary entries within the date range
        List<DiaryEntryWithImage> filteredEntries = diaryEntries.stream()
                .filter(entry -> {
                    LocalDate entryDate = entry.getEntryTime().toLocalDate();
                    return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        if (filteredEntries.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Entries", "No diary entries found for the selected date range.");
            return;
        }

        // Count occurrences of each mood
        Map<String, Integer> moodCounts = new HashMap<>();
        for (DiaryEntryWithImage entry : filteredEntries) {
            moodCounts.put(entry.getMood(), moodCounts.getOrDefault(entry.getMood(), 0) + 1);
        }

        // Create Bubble Chart
        NumberAxis xAxis = new NumberAxis(0, moodCounts.size() + 1, 1); // Space bubbles dynamically
        xAxis.setLabel("Mood Categories");

        NumberAxis yAxis = new NumberAxis(0, moodCounts.values().stream().max(Integer::compare).orElse(10) + 2, 1);
        yAxis.setLabel("Mood Frequency");

        BubbleChart<Number, Number> bubbleChart = new BubbleChart<>(xAxis, yAxis);
        bubbleChart.setTitle("Mood Tracker Bubble Chart");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Mood Frequency");

        int xIndex = 1; // Used to space bubbles along the x-axis
        for (Map.Entry<String, Integer> moodEntry : moodCounts.entrySet()) {
            String mood = moodEntry.getKey();
            int count = moodEntry.getValue();

            // Create a data point for each mood
            XYChart.Data<Number, Number> bubble = new XYChart.Data<>(xIndex++, count, count * 2); // Radius scales with frequency
            styleBubble(bubble, mood); // Apply color based on mood
            series.getData().add(bubble);
        }

        bubbleChart.getData().add(series);

        VBox chartLayout = new VBox(10);
        chartLayout.setPadding(new Insets(10));
        chartLayout.getChildren().add(bubbleChart);

        Scene chartScene = new Scene(chartLayout, 800, 600);
        stage.setScene(chartScene);
        stage.show();
    }

    private void styleBubble(XYChart.Data<Number, Number> bubble, String mood) {
        String color = moodColors.getOrDefault(mood, "#cccccc"); // Default gray if mood not found
        bubble.getNode().setStyle("-fx-background-color: " + color + ", white; -fx-border-color: black;");
    }

    private Map<String, String> initializeMoodColors() {
        Map<String, String> colors = new HashMap<>();
        colors.put("Happy 😊", "#ffeb3b"); // Yellow
        colors.put("Sad 😢", "#2196f3");  // Blue
        colors.put("Neutral 😐", "#9e9e9e"); // Gray
        colors.put("Content", "#4caf50"); // Green
        colors.put("Stressed", "#f44336"); // Red
        colors.put("Excited", "#ff9800"); // Orange
        colors.put("Tired", "#3f51b5"); // Indigo
        colors.put("Angry", "#d32f2f"); // Deep Red
        return colors;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}















