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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class demo1 extends Application {

    private static final String IMAGE_STORAGE_DIR = "data/images/";
    private final ObservableList<DiaryEntryWithImage> diaryEntries = FXCollections.observableArrayList();
    private final ObservableList<RecycleBinEntry> recycleBin = FXCollections.observableArrayList();

    private final UserLoginRegistration userSystem = new UserLoginRegistration();
    private final MotivationalQuotes motivationalQuotes = new MotivationalQuotes();
    private final RecycleBinFeature recycleBinFeature = new RecycleBinFeature(recycleBin);
    private final SearchManager searchManager = new SearchManager(diaryEntries);
    private final MoodTracker moodTracker = new MoodTracker(diaryEntries);

    private String currentUser;
    private LocalDateTime entryTime;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ensureImageStorageDirectoryExists();

        primaryStage.setTitle("Digital Diary");

        VBox welcomeLayout = new VBox(15);
        welcomeLayout.setPadding(new Insets(20));
        welcomeLayout.setStyle("-fx-background-color: #f2f5f9;");

        Label welcomeLabel = new Label("Welcome to Your Digital Diary");
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        Button registerButton = createStyledButton("Register");
        registerButton.setOnAction(event -> userSystem.openRegistrationScreen(primaryStage));

        Button loginButton = createStyledButton("Login");
        loginButton.setOnAction(event -> {
            currentUser = userSystem.openLoginScreen(primaryStage);
            if (currentUser != null && !currentUser.isEmpty()) {
                loadDiaryEntries(primaryStage);
            }
        });

        welcomeLayout.getChildren().addAll(welcomeLabel, registerButton, loginButton);

        Scene welcomeScene = new Scene(welcomeLayout, 800, 600);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void loadDiaryEntries(Stage primaryStage) {
        diaryEntries.clear();
        diaryEntries.addAll(DiaryPersistence.loadEntriesFromFile(currentUser));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f2f5f9;");

        Label titleLabel = new Label("Digital Diary");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search entries...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        Button searchButton = createStyledButton("Search");
        searchButton.setOnAction(event -> searchManager.updateSearch(searchField.getText()));

        HBox searchLayout = new HBox(10, searchField, searchButton);

        ListView<DiaryEntryWithImage> diaryListView = new ListView<>(searchManager.getFilteredEntries());
        diaryListView.setPrefHeight(400);
        diaryListView.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d4e6f1; -fx-border-radius: 10px;");

        diaryListView.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(DiaryEntryWithImage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Display title and date
                    setText(item.getTitle() + "\nDate: " + item.getFormattedEntryTime());
                    if (!item.getImagePaths().isEmpty()) {
                        File imageFile = new File(IMAGE_STORAGE_DIR, item.getImagePaths().get(0));
                        if (imageFile.exists()) {
                            Image image = new Image(imageFile.toURI().toString());
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            setGraphic(imageView);
                        } else {
                            setGraphic(null);
                        }
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        diaryListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                DiaryEntryWithImage selectedEntry = diaryListView.getSelectionModel().getSelectedItem();
                if (selectedEntry != null) {
                    showFullEntryDialog(selectedEntry);
                }
            }
        });

        Button addButton = createStyledButton("Add Entry");
        addButton.setOnAction(event -> openAddEntryDialog());

        Button editButton = createStyledButton("Edit Entry");
        editButton.setOnAction(event -> openEditEntryDialog(diaryListView.getSelectionModel().getSelectedItem()));

        Button deleteButton = createStyledButton("Delete Entry");
        deleteButton.setOnAction(event -> deleteSelectedEntry(diaryListView));

        Button recycleBinButton = createStyledButton("Recycle Bin");
        recycleBinButton.setOnAction(event -> recycleBinFeature.openRecycleBinDialog(diaryEntries));

        Button moodTrackerButton = createStyledButton("Mood Tracker");
        moodTrackerButton.setOnAction(event -> moodTracker.showMoodTrackerDialog());

        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, recycleBinButton, moodTrackerButton);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        layout.getChildren().addAll(titleLabel, searchLayout, diaryListView, buttonBox);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #d4e6f1; -fx-text-fill: #2e4053; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 15; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #aed6f1; -fx-text-fill: #2e4053; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 15; -fx-border-radius: 10px; -fx-background-radius: 10px;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: #d4e6f1; -fx-text-fill: #2e4053; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 15; -fx-border-radius: 10px; -fx-background-radius: 10px;"));
        return button;
    }

    private void openAddEntryDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Entry");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d4e6f1; -fx-border-radius: 10px;");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Content");

        ComboBox<String> moodComboBox = new ComboBox<>();
        moodComboBox.getItems().addAll("Happy 😊", "Sad 😢", "Neutral 😐", "Content", "Stressed", "Excited", "Tired", "Angry");
        moodComboBox.setPromptText("Select Mood");

        Label quoteLabel = new Label("Select a mood to see a motivational quote!");
        quoteLabel.setWrapText(true);
        quoteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");

        moodComboBox.setOnAction(event -> {
            String selectedMood = moodComboBox.getValue();
            if (selectedMood != null) {
                quoteLabel.setText(motivationalQuotes.getQuoteForMood(selectedMood));
            }
        });

        ObservableList<String> selectedImagePaths = FXCollections.observableArrayList();

        Button selectImagesButton = createStyledButton("Select Images");
        selectImagesButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(dialog);
            if (selectedFiles != null) {
                selectedImagePaths.clear();
                for (File file : selectedFiles) {
                    try {
                        File destFile = new File(IMAGE_STORAGE_DIR, file.getName());
                        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        selectedImagePaths.add(destFile.getName());
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "File Error", "Could not save image: " + file.getName());
                    }
                }
            }
        });

        Button saveButton = createStyledButton("Save");
        saveButton.setOnAction(event -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            String mood = moodComboBox.getValue();

            if (title.isEmpty() || content.isEmpty() || mood == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
            } else {
                diaryEntries.add(new DiaryEntryWithImage(title, content, mood, new ArrayList<>(selectedImagePaths), entryTime));

                // Save entries immediately after adding
                DiaryPersistence.saveEntriesToFile(currentUser, new ArrayList<>(diaryEntries));

                dialog.close();
            }
        });

        layout.getChildren().addAll(new Label("Title:"), titleField, new Label("Content:"), contentArea, new Label("Mood:"), moodComboBox, quoteLabel, selectImagesButton, saveButton);

        Scene scene = new Scene(layout, 400, 500);
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

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d4e6f1; -fx-border-radius: 10px;");

        TextField titleField = new TextField(entry.getTitle());
        TextArea contentArea = new TextArea(entry.getContent());
        ComboBox<String> moodComboBox = new ComboBox<>();
        moodComboBox.getItems().addAll("Happy 😊", "Sad 😢", "Neutral 😐", "Content", "Stressed", "Excited", "Tired", "Angry");
        moodComboBox.setValue(entry.getMood());

        Label quoteLabel = new Label(motivationalQuotes.getQuoteForMood(entry.getMood()));
        quoteLabel.setWrapText(true);
        quoteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");

        moodComboBox.setOnAction(event -> {
            String selectedMood = moodComboBox.getValue();
            if (selectedMood != null) {
                quoteLabel.setText(motivationalQuotes.getQuoteForMood(selectedMood));
            }
        });

        ObservableList<String> selectedImagePaths = FXCollections.observableArrayList(entry.getImagePaths());

        Button selectImagesButton = createStyledButton("Update Images");
        selectImagesButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(dialog);
            if (selectedFiles != null) {
                selectedImagePaths.clear();
                for (File file : selectedFiles) {
                    try {
                        File destFile = new File(IMAGE_STORAGE_DIR, file.getName());
                        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        selectedImagePaths.add(destFile.getName());
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "File Error", "Could not save image: " + file.getName());
                    }
                }
            }
        });

        Button saveButton = createStyledButton("Save Changes");
        saveButton.setOnAction(event -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            String mood = moodComboBox.getValue();

            if (title.isEmpty() || content.isEmpty() || mood == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
            } else {
                entry.setTitle(title);
                entry.setContent(content);
                entry.setMood(mood);
                entry.setImagePaths(new ArrayList<>(selectedImagePaths));

                // Save entries immediately after adding
                DiaryPersistence.saveEntriesToFile(currentUser, new ArrayList<>(diaryEntries));

                dialog.close();
            }
        });

        layout.getChildren().addAll(new Label("Title:"), titleField, new Label("Content:"), contentArea, new Label("Mood:"), moodComboBox, quoteLabel, selectImagesButton, saveButton);

        Scene scene = new Scene(layout, 400, 500);
        dialog.setScene(scene);
        dialog.show();
    }

    private void deleteSelectedEntry(ListView<DiaryEntryWithImage> diaryListView) {
        DiaryEntryWithImage selectedEntry = diaryListView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            recycleBinFeature.moveToRecycleBin(diaryEntries, selectedEntry);
        }
    }

    private void ensureImageStorageDirectoryExists() {
        File storageDir = new File(IMAGE_STORAGE_DIR);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showFullEntryDialog(DiaryEntryWithImage entry) {
        Stage dialog = new Stage();
        dialog.setTitle("Diary Entry Details");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d4e6f1; -fx-border-radius: 10px;");

        Label titleLabel = new Label("Title: " + entry.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label moodLabel = new Label("Mood: " + entry.getMood());
        moodLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        TextArea contentArea = new TextArea(entry.getContent());
        contentArea.setWrapText(true);
        contentArea.setEditable(false);
        contentArea.setStyle("-fx-control-inner-background: #f2f5f9;");

        VBox imageBox = new VBox(10);
        for (String imagePath : entry.getImagePaths()) {
            File imageFile = new File(IMAGE_STORAGE_DIR, imagePath);
            if (imageFile.exists()) {
                ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                imageBox.getChildren().add(imageView);
            }
        }

        Button closeButton = createStyledButton("Close");
        closeButton.setOnAction(event -> dialog.close());

        layout.getChildren().addAll(titleLabel, moodLabel, new Label("Content:"), contentArea, new Label("Images:"), imageBox, closeButton);

        Scene scene = new Scene(layout, 400, 600);
        dialog.setScene(scene);
        dialog.show();
    }
}


































