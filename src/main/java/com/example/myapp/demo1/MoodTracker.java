package com.example.myapp.demo1;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoodTracker {
    private final ObservableList<DiaryEntryWithImage> diaryEntries;
    private Canvas heatMapCanvas;

    public MoodTracker(ObservableList<DiaryEntryWithImage> diaryEntries) {
        this.diaryEntries = diaryEntries;
    }

    public void showMoodTrackerDialog() {
        Stage stage = new Stage();
        stage.setTitle("Mood Tracker");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        HBox dateRangeBox = new HBox(10);
        dateRangeBox.getChildren().addAll(
                new Label("Start Date:"), startDatePicker,
                new Label("End Date:"), endDatePicker
        );

        heatMapCanvas = new Canvas(400, 200);

        Button trackButton = new Button("Track Mood");
        trackButton.setOnAction(e -> updateHeatMap(startDatePicker.getValue(), endDatePicker.getValue()));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(dateRangeBox, trackButton, heatMapCanvas);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double width = bounds.getWidth();
        double height = bounds.getHeight();

    }

    private void updateHeatMap(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Date Error", "Please select both start and end dates.");
            return;
        }

        List<DiaryEntryWithImage> filteredEntries = diaryEntries.stream()
                .filter(entry -> {
                    LocalDate entryDate = entry.getEntryTime().toLocalDate();
                    return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        drawHeatMap(filteredEntries);
    }

    private void drawHeatMap(List<DiaryEntryWithImage> entries) {
        GraphicsContext gc = heatMapCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, heatMapCanvas.getWidth(), heatMapCanvas.getHeight());

        int entryCount = entries.size();
        double squareSize = Math.min(heatMapCanvas.getWidth() / entryCount, heatMapCanvas.getHeight());

        for (int i = 0; i < entryCount; i++) {
            DiaryEntryWithImage entry = entries.get(i);
            int score = getMoodScore(entry.getMood());
            Color color = getMoodColor(score);

            gc.setFill(color);
            gc.fillRect(i * squareSize, 0, squareSize, squareSize);
        }

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(10));

    }

    private Color getMoodColor(int score) {
        switch (score) {
            case 5: return Color.GREEN;
            case 4: return Color.LIGHTGREEN;
            case 3: return Color.YELLOW;
            case 2: return Color.ORANGE;
            case 1: return Color.RED;
            default: return Color.GRAY;
        }
    }

    private int getMoodScore(String mood) {
        switch (mood.toLowerCase()) {
            case "happy 😊":
            case "excited":
                return 5;
            case "content":
                return 4;
            case "neutral 😐":
                return 3;
            case "tired":
            case "stressed":
                return 2;
            case "sad 😢":
            case "angry":
                return 1;
            default:
                return 0;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}










