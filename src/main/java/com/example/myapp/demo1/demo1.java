package com.example.myapp.demo1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


public class demo1 extends Application {

    private final ObservableList<DiaryEntryWithImage> diaryEntries = FXCollections.observableArrayList();
    private final ObservableList<RecycleBinEntry> recycleBin = FXCollections.observableArrayList();
    private final UserLoginRegistration userSystem = new UserLoginRegistration();
    private final SearchManager searchManager = new SearchManager(diaryEntries);
    private final RecycleBinFeature recycleBinFeature = new RecycleBinFeature(recycleBin);
    private String currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Digital Diary");


        VBox welcomeLayout = new VBox(10);
        welcomeLayout.setPadding(new Insets(10));

        Label welcomeLabel = new Label("Welcome to the Digital Diary");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button registerButton = new Button("Register");
        registerButton.setOnAction(event -> userSystem.openRegistrationScreen(primaryStage));

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {
            currentUser = userSystem.openLoginScreen(primaryStage);
            if (currentUser != null && !currentUser.isEmpty()) {
                loadDiaryEntries(primaryStage);
            }
        });

        welcomeLayout.getChildren().addAll(welcomeLabel, registerButton, loginButton);

        Scene welcomeScene = new Scene(welcomeLayout, 400, 300);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
        primaryStage.setMaximized(true);

    }

    private void loadDiaryEntries(Stage primaryStage) {
        diaryEntries.addAll(DiaryPersistence.loadEntriesFromFile(currentUser));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label titleLabel = new Label("Digital Diary");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search entries...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        searchButton.setOnAction(event -> searchManager.updateSearch(searchField.getText()));

        HBox searchLayout = new HBox(10, searchField, searchButton);

        ListView<DiaryEntryWithImage> diaryListView = new ListView<>(searchManager.getFilteredEntries());
        diaryListView.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(DiaryEntryWithImage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    if (item.getImagePath() != null) {
                        Image image = new Image(new java.io.File(item.getImagePath()).toURI().toString());
                        imageView.setImage(image);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        Button addButton = new Button("Add Entry");
        addButton.setOnAction(event -> openAddEntryDialog());

        Button editButton = new Button("Edit Entry");
        editButton.setOnAction(event -> openEditEntryDialog(diaryListView.getSelectionModel().getSelectedItem()));

        Button deleteButton = new Button("Delete Entry");
        deleteButton.setOnAction(event -> deleteSelectedEntry(diaryListView));


        Button recycleBinButton = new Button("Recycle Bin");
        recycleBinButton.setOnAction(event -> recycleBinFeature.openRecycleBinDialog(diaryEntries));

        Button moodTrackerButton = new Button("Mood Tracker");
        moodTrackerButton.setOnAction(event -> openMoodTrackerDialog());

        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, recycleBinButton, moodTrackerButton);

        layout.getChildren().addAll(titleLabel, searchLayout, diaryListView, buttonBox);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> DiaryPersistence.saveEntriesToFile(currentUser, new ArrayList<>(diaryEntries)));

    }

    private void openAddEntryDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Entry");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea contentArea = new TextArea(); // Add this line
        contentArea.setPromptText("Content"); // Add this line

        ComboBox<String> moodComboBox = new ComboBox<>();
        moodComboBox.getItems().addAll("Happy 😊", "Excited", "Content", "Neutral 😐", "Tired", "Stressed", "Sad 😢", "Angry");
        moodComboBox.setPromptText("Select Mood");

        Button selectImageButton = new Button("Select Image");
        Label selectedImageLabel = new Label("No image selected");

        Button saveButton = new Button("Save");

        // Variable to hold the selected image path
        String[] selectedImagePath = {null};

        selectImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fileChooser.showOpenDialog(dialog);
            if (file != null) {
                selectedImagePath[0] = file.getAbsolutePath(); // Store the selected image path
                selectedImageLabel.setText("Selected Image: " + file.getName());
            }
        });

        saveButton.setOnAction(event -> {
            try {
                String title = titleField.getText();
                String content = contentArea.getText();
                String selectedMood = moodComboBox.getValue();

                if (title.isEmpty() || content.isEmpty() || selectedMood == null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
                } else {
                    DiaryEntryWithImage newEntry = new DiaryEntryWithImage(title, content, selectedMood, selectedImagePath[0]);
                    diaryEntries.add(newEntry);
                    DiaryPersistence.saveEntriesToFile(currentUser, new ArrayList<>(diaryEntries));
                    dialog.close();
                }

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the entry: " + e.getMessage());
            }
        });

        layout.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Content:"), contentArea,
                new Label("Mood:"), moodComboBox,
                selectImageButton,
                selectedImageLabel,
                saveButton);

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.show();
    }

    private void openEditEntryDialog(DiaryEntryWithImage entry) {
        if (entry == null) {
            showAlert(Alert.AlertType.WARNING, "Edit Error", "Please select an entry to edit.");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Edit Entry");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField titleField = new TextField(entry.getTitle());
        TextArea contentArea = new TextArea(entry.getContent());

        ComboBox<String> moodComboBox = new ComboBox<>();
        moodComboBox.getItems().addAll("Happy 😊", "Excited", "Content", "Neutral 😐", "Tired", "Stressed", "Sad 😢", "Angry");
        moodComboBox.setValue(entry.getMood());

        Button selectImageButton = new Button("Select Image");
        Label selectedImageLabel = new Label("Current Image: " + (entry.getImagePath() != null ? entry.getImagePath() : "No image selected"));

        String[] selectedImagePath = {entry.getImagePath()};

        selectImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fileChooser.showOpenDialog(dialog);
            if (file != null) {
                selectedImagePath[0] = file.getAbsolutePath();
                selectedImageLabel.setText("Selected Image: " + file.getName());
            }
        });

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(event -> {
            try {
                String title = titleField.getText();
                String content = contentArea.getText();
                String selectedMood = moodComboBox.getValue();

                if (title.isEmpty() || content.isEmpty() || selectedMood == null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
                } else {
                    entry.setTitle(title);
                    entry.setContent(content);
                    entry.setMood(selectedMood);
                    entry.setImagePath(selectedImagePath[0]);

                    DiaryPersistence.saveEntriesToFile(currentUser, new ArrayList<>(diaryEntries));
                    dialog.close();
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while editing the entry: " + e.getMessage());
            }
        });

        layout.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Content:"), contentArea,
                new Label("Mood:"), moodComboBox,
                selectImageButton,
                selectedImageLabel,
                saveButton);

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.show();
    }

    private void deleteSelectedEntry(ListView<DiaryEntryWithImage> diaryListView) {
        DiaryEntryWithImage selectedEntry = diaryListView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            recycleBinFeature.moveToRecycleBin(diaryEntries, selectedEntry); // Move to recycle bin instead of deleting permanently
        } else {
            showAlert(Alert.AlertType.WARNING, "Delete Error", "Please select an entry to delete.");
        }
    }

    private void openMoodTrackerDialog() {
        MoodTracker moodTracker = new MoodTracker(diaryEntries);
        moodTracker.showMoodTrackerDialog();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}





























