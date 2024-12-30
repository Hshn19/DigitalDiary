package com.example.myapp.demo1;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.time.LocalDateTime;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class RecycleBinFeature {
    private final ObservableList<RecycleBinEntry> recycleBin;

    public RecycleBinFeature(ObservableList<RecycleBinEntry> recycleBin) {
        this.recycleBin = recycleBin;
    }

    public void moveToRecycleBin(ObservableList<DiaryEntryWithImage> diaryEntries, DiaryEntryWithImage selectedEntry) {
        if (selectedEntry != null) {
            recycleBin.add(new RecycleBinEntry(selectedEntry, LocalDateTime.now())); // Add to recycle bin
            diaryEntries.remove(selectedEntry); // Remove from main list
            showAlert(Alert.AlertType.INFORMATION, "Moved to Recycle Bin", "The entry has been moved to the recycle bin.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an entry to delete.");
        }
    }

    public void openRecycleBinDialog(ObservableList<DiaryEntryWithImage> diaryEntries) {
        Stage dialog = new Stage();
        dialog.setTitle("Recycle Bin");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        ListView<RecycleBinEntry> recycleBinView = new ListView<>(recycleBin);

        Button restoreButton = new Button("Restore Selected Entry");
        restoreButton.setOnAction(event -> {
            RecycleBinEntry selectedBinEntry = recycleBinView.getSelectionModel().getSelectedItem();
            if (selectedBinEntry != null) {
                diaryEntries.add(selectedBinEntry.getDiaryEntry());
                recycleBin.remove(selectedBinEntry);
                showAlert(Alert.AlertType.INFORMATION, "Restored", "The entry has been restored to your diary.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an entry to restore.");
            }
        });

        Button purgeButton = new Button("Purge Old Entries");
        purgeButton.setOnAction(event -> purgeOldEntries());

        layout.getChildren().addAll(new Label("Recycle Bin Entries:"), recycleBinView, restoreButton, purgeButton);

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

    public void purgeOldEntries() {
        // Implementation for purging old entries
    }
}








