package com.example.myapp.demo1;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoodTracker {
    private final ObservableList<DiaryEntryWithImage> diaryEntries;

    public MoodTracker(ObservableList<DiaryEntryWithImage> diaryEntries) {
        this.diaryEntries = diaryEntries;
    }

    public void showMoodTrackerDialog() {
        Stage stage = new Stage();
        stage.setTitle("Mood Tracker - Mood Trends");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label titleLabel = new Label("Mood Tracker");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        HBox dateRangeBox = new HBox(10);
        dateRangeBox.getChildren().addAll(
                new Label("Start Date:"), startDatePicker,
                new Label("End Date:"), endDatePicker
        );

        LineChart<String, Number> moodLineChart = createMoodLineChart();

        Button trackButton = new Button("Track Mood");
        trackButton.setOnAction(e -> updateMoodLineChart(moodLineChart, startDatePicker.getValue(), endDatePicker.getValue()));

        layout.getChildren().addAll(titleLabel, dateRangeBox, trackButton, moodLineChart);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private LineChart<String, Number> createMoodLineChart() {
        CategoryAxis xAxis = new CategoryAxis(); // X-axis for dates
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis(); // Y-axis for mood counts
        yAxis.setLabel("Mood Count");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Mood Occurrences Over Time");
        return lineChart;
    }

    private void updateMoodLineChart(LineChart<String, Number> lineChart, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Date Error", "Please select both start and end dates.");
            return;
        }

        // Filter diary entries based on the selected date range
        List<DiaryEntryWithImage> filteredEntries = diaryEntries.stream()
                .filter(entry -> {
                    LocalDate entryDate = entry.getEntryTime().toLocalDate();
                    return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        if (filteredEntries.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data", "No diary entries found for the selected date range.");
            lineChart.getData().clear(); // Clear the chart if no data
            return;
        }

        // Generate data series for each mood
        Map<String, List<DiaryEntryWithImage>> moodGroups = filteredEntries.stream()
                .collect(Collectors.groupingBy(DiaryEntryWithImage::getMood));

        lineChart.getData().clear(); // Clear the chart before adding new data

        // Create a series for each mood and populate it
        moodGroups.forEach((mood, entries) -> {
            XYChart.Series<String, Number> moodSeries = new XYChart.Series<>();
            moodSeries.setName(mood);

            Map<String, Long> moodCountByDate = entries.stream()
                    .collect(Collectors.groupingBy(
                            entry -> entry.getEntryTime().toLocalDate().toString(),
                            Collectors.counting()
                    ));

            moodCountByDate.forEach((date, count) -> {
                moodSeries.getData().add(new XYChart.Data<>(date, count));
            });

            lineChart.getData().add(moodSeries); // Add the series to the chart
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


















