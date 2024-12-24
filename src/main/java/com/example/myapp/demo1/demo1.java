package com.example.myapp.demo1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

public class demo1 extends Application {

    private ObservableList<DiaryEntry> diaryEntries = FXCollections.observableArrayList();
    private ObservableList<RecycleBinEntry> recycleBin = FXCollections.observableArrayList();
    private ListView<DiaryEntry> listView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Digital Diary 📔");

        // Main Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // List View
        listView = new ListView<>();
        listView.setItems(diaryEntries);
        listView.setPrefHeight(300);

        // Buttons
        Button addButton = new Button("Add Entry");
        Button editButton = new Button("Edit Entry");
        Button deleteButton = new Button("Delete Entry");
        Button recycleBinButton = new Button("View Recycle Bin");

        HBox buttonLayout = new HBox(10, addButton, editButton, deleteButton, recycleBinButton);

        // Event Handlers
        addButton.setOnAction(event -> openAddEntryDialog());
        editButton.setOnAction(event -> openEditEntryDialog());
        deleteButton.setOnAction(event -> moveToRecycleBin());
        recycleBinButton.setOnAction(event -> openRecycleBinDialog());

        // Layout Assembly
        layout.getChildren().addAll(new Label("Your Diary Entries:"), listView, buttonLayout);

        // Scene
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openAddEntryDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Entry");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Input Fields
        TextField titleField = new TextField();
        titleField.setPromptText("Enter Title");

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Enter Content");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            if (!title.isEmpty() && !content.isEmpty()) {
                diaryEntries.add(new DiaryEntry(LocalDate.now(), title, content));
                dialog.close();
            } else {
                showAlert("Validation Error", "Both Title and Content are required.");
            }
        });

        layout.getChildren().addAll(new Label("Title:"), titleField, new Label("Content:"), contentArea, saveButton);

        Scene scene = new Scene(layout, 300, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    private void openEditEntryDialog() {
        DiaryEntry selectedEntry = listView.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert("Selection Error", "Please select an entry to edit.");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Edit Entry");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Input Fields
        TextField titleField = new TextField(selectedEntry.getTitle());
        TextArea contentArea = new TextArea(selectedEntry.getContent());

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(event -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            if (!title.isEmpty() && !content.isEmpty()) {
                selectedEntry.setTitle(title);
                selectedEntry.setContent(content);
                listView.refresh();
                dialog.close();
            } else {
                showAlert("Validation Error", "Both Title and Content are required.");
            }
        });

        layout.getChildren().addAll(new Label("Title:"), titleField, new Label("Content:"), contentArea, saveButton);

        Scene scene = new Scene(layout, 300, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    private void moveToRecycleBin() {
        DiaryEntry selectedEntry = listView.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert("Selection Error", "Please select an entry to delete.");
            return;
        }
        recycleBin.add(new RecycleBinEntry(selectedEntry, LocalDateTime.now()));
        diaryEntries.remove(selectedEntry);
        showAlert("Recycle Bin", "Entry moved to the Recycle Bin.");
    }

    private void openRecycleBinDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Recycle Bin");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        ListView<RecycleBinEntry> recycleBinView = new ListView<>();
        recycleBinView.setItems(recycleBin);

        Button restoreButton = new Button("Restore Selected Entry");
        Button purgeButton = new Button("Purge Old Entries");

        restoreButton.setOnAction(event -> {
            RecycleBinEntry selectedBinEntry = recycleBinView.getSelectionModel().getSelectedItem();
            if (selectedBinEntry == null) {
                showAlert("Selection Error", "Please select an entry to restore.");
                return;
            }
            diaryEntries.add(selectedBinEntry.getDiaryEntry());
            recycleBin.remove(selectedBinEntry);
            showAlert("Recycle Bin", "Entry restored successfully.");
        });

        purgeButton.setOnAction(event -> purgeOldEntries());

        layout.getChildren().addAll(new Label("Recycle Bin Entries:"), recycleBinView, restoreButton, purgeButton);

        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    private void purgeOldEntries() {
        Iterator<RecycleBinEntry> iterator = recycleBin.iterator();
        LocalDateTime now = LocalDateTime.now();
        while (iterator.hasNext()) {
            RecycleBinEntry entry = iterator.next();
            if (ChronoUnit.DAYS.between(entry.getDeletedAt(), now) > 30) {
                iterator.remove();
            }
        }
        showAlert("Recycle Bin", "Old entries purged successfully.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



