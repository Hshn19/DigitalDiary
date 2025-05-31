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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class demo1 extends Application {

    // Observable lists
    private static final String IMAGE_STORAGE_DIR = "data/images/";
    private final ObservableList<DiaryEntryWithImage> diaryEntries = FXCollections.observableArrayList();
    private final ObservableList<RecycleBinEntry> recycleBin = FXCollections.observableArrayList();

    // Key features
    private final UserLoginRegistration userSystem = new UserLoginRegistration();
    private final MotivationalQuotes motivationalQuotes = new MotivationalQuotes();
    private final RecycleBinFeature recycleBinFeature = new RecycleBinFeature(recycleBin);
    private final SearchManager searchManager = new SearchManager(diaryEntries);
    private final MoodTracker moodTracker = new MoodTracker(diaryEntries);

    private String currentUser;
    private String currentThemeKey = "Light"; // Default theme is Light
    private Scene currentScene; // Reference to current scene for theme updates
    private Map<String, String> themeCssFiles; // theme name to CSS resource path mapping

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ensureImageStorageDirectoryExists();

        // Initialize theme CSS mappings (add new themes)
        themeCssFiles = new HashMap<>();
        themeCssFiles.put("Light", "/themes/light.css");
        themeCssFiles.put("Dark", "/themes/dark.css");
        themeCssFiles.put("Space", "/themes/space.css");
        themeCssFiles.put("JMetro", "/themes/jmetro.css");
        themeCssFiles.put("Materialfx", "/themes/materialfx.css");
        themeCssFiles.put("Ghibli", "/themes/ghibli.css");


        primaryStage.setTitle("Digital Diary");

        // Welcome layout without theme selector
        VBox welcomeLayout = new VBox(15);
        welcomeLayout.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome to Your Digital Diary");
        welcomeLabel.getStyleClass().add("welcome-label");

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("styled-button");
        registerButton.setOnAction(event -> {
            userSystem.openRegistrationScreen(primaryStage);
        });

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("styled-button");
        loginButton.setOnAction(event -> {
            currentUser = userSystem.openLoginScreen(primaryStage);
            if (currentUser != null && !currentUser.isEmpty()) {
                loadDiaryEntries(primaryStage);
            }
        });

        welcomeLayout.getChildren().addAll(welcomeLabel, registerButton, loginButton);
        Scene welcomeScene = new Scene(welcomeLayout, 800, 600);
        welcomeScene.getStylesheets().add(getClass().getResource("/themes/light.css").toExternalForm());
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void loadDiaryEntries(Stage primaryStage) {
        diaryEntries.clear();
        diaryEntries.addAll(DiaryPersistence.loadEntriesFromFile(currentUser));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Digital Diary");
        titleLabel.getStyleClass().add("title-label");

        TextField searchField = new TextField();
        searchField.setPromptText("Search entries...");
        searchField.getStyleClass().add("text-field");

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("styled-button");
        searchButton.setOnAction(event -> searchManager.updateSearch(searchField.getText()));

        HBox searchLayout = new HBox(10, searchField, searchButton);

        ListView<DiaryEntryWithImage> diaryListView = new ListView<>(searchManager.getFilteredEntries());
        diaryListView.setPrefHeight(400);
        diaryListView.getStyleClass().add("list-view");

        diaryListView.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(DiaryEntryWithImage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
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

        Button addButton = new Button("Add Entry");
        addButton.getStyleClass().add("styled-button");
        addButton.setOnAction(event -> openAddEntryDialog());

        Button editButton = new Button("Edit Entry");
        editButton.getStyleClass().add("styled-button");
        editButton.setOnAction(event -> openEditEntryDialog(diaryListView.getSelectionModel().getSelectedItem()));

        Button deleteButton = new Button("Delete Entry");
        deleteButton.getStyleClass().add("styled-button");
        deleteButton.setOnAction(event -> deleteSelectedEntry(diaryListView));

        Button recycleBinButton = new Button("Recycle Bin");
        recycleBinButton.getStyleClass().add("styled-button");
        recycleBinButton.setOnAction(event -> recycleBinFeature.openRecycleBinDialog(diaryEntries));

        Button moodTrackerButton = new Button("Mood Tracker");
        moodTrackerButton.getStyleClass().add("styled-button");
        moodTrackerButton.setOnAction(event -> moodTracker.showMoodTrackerDialog());

        ComboBox<String> themeSelector = new ComboBox<>();
        themeSelector.getItems().addAll(themeCssFiles.keySet());
        themeSelector.setValue(currentThemeKey);
        themeSelector.getStyleClass().add("combo-box");
        themeSelector.setOnAction(event -> {
            currentThemeKey = themeSelector.getValue();
            updateTheme();
        });

        Label themeLabel = new Label("Theme:");
        themeLabel.getStyleClass().add("theme-label");
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, recycleBinButton, moodTrackerButton, themeLabel, themeSelector);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        layout.getChildren().addAll(titleLabel, searchLayout, diaryListView, buttonBox);

        currentScene = new Scene(layout, 800, 600);
        updateTheme();

        primaryStage.setScene(currentScene);
        primaryStage.show();
    }

    private void updateTheme() {
        if (currentScene == null) return;
        currentScene.getStylesheets().clear();
        String cssPath = themeCssFiles.get(currentThemeKey);
        if (cssPath != null) {
            currentScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }
    }

    private void openAddEntryDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Entry");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        titleField.getStyleClass().add("text-field");

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Content");
        contentArea.getStyleClass().add("text-area");

        ComboBox<String> moodComboBox = new ComboBox<>();
        moodComboBox.getItems().addAll("Happy 😊", "Sad 😢", "Neutral 😐", "Content", "Stressed", "Excited", "Tired", "Angry");
        moodComboBox.setPromptText("Select Mood");
        moodComboBox.getStyleClass().add("combo-box");

        Label quoteLabel = new Label("Select a mood to see a motivational quote!");
        quoteLabel.setWrapText(true);
        quoteLabel.getStyleClass().add("quote-label");

        moodComboBox.setOnAction(event -> {
            String selectedMood = moodComboBox.getValue();
            if (selectedMood != null) {
                quoteLabel.setText(motivationalQuotes.getQuoteForMood(selectedMood));
            }
        });

        ObservableList<String> selectedImagePaths = FXCollections.observableArrayList();

        Button selectImagesButton = new Button("Select Images");
        selectImagesButton.getStyleClass().add("styled-button");
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

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("styled-button");
        saveButton.setOnAction(event -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            String mood = moodComboBox.getValue();

            if (title.isEmpty() || content.isEmpty() || mood == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
            } else {
                DiaryEntryWithImage newEntry = new DiaryEntryWithImage(title, content, mood, new ArrayList<>(selectedImagePaths), LocalDateTime.now());
                diaryEntries.add(newEntry);
                DiaryPersistence.saveEntriesToFile(currentUser, new ArrayList<>(diaryEntries));
                dialog.close();
            }
        });

        layout.getChildren().addAll(new Label("Title:"), titleField, new Label("Content:"), contentArea, new Label("Mood:"), moodComboBox, quoteLabel, selectImagesButton, saveButton);

        Scene scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(getClass().getResource(themeCssFiles.get(currentThemeKey)).toExternalForm());
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

        TextField titleField = new TextField(entry.getTitle());
        titleField.getStyleClass().add("text-field");

        TextArea contentArea = new TextArea(entry.getContent());
        contentArea.getStyleClass().add("text-area");

        ComboBox<String> moodComboBox = new ComboBox<>();
        moodComboBox.getItems().addAll("Happy 😊", "Sad 😢", "Neutral 😐", "Content", "Stressed", "Excited", "Tired", "Angry");
        moodComboBox.setValue(entry.getMood());
        moodComboBox.getStyleClass().add("combo-box");

        Label quoteLabel = new Label(motivationalQuotes.getQuoteForMood(entry.getMood()));
        quoteLabel.setWrapText(true);
        quoteLabel.getStyleClass().add("quote-label");

        moodComboBox.setOnAction(event -> {
            String selectedMood = moodComboBox.getValue();
            if (selectedMood != null) {
                quoteLabel.setText(motivationalQuotes.getQuoteForMood(selectedMood));
            }
        });

        ObservableList<String> selectedImagePaths = FXCollections.observableArrayList(entry.getImagePaths());

        Button selectImagesButton = new Button("Update Images");
        selectImagesButton.getStyleClass().add("styled-button");
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

        Button saveButton = new Button("Save Changes");
        saveButton.getStyleClass().add("styled-button");
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
                DiaryPersistence.saveEntriesToFile(currentUser, new ArrayList<>(diaryEntries));
                dialog.close();
            }
        });

        layout.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Content:"), contentArea,
                new Label("Mood:"), moodComboBox,
                quoteLabel,
                selectImagesButton,
                saveButton
        );

        Scene scene = new Scene(layout, 400, 500);
        scene.getStylesheets().add(getClass().getResource(themeCssFiles.get(currentThemeKey)).toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    private void deleteSelectedEntry(ListView<DiaryEntryWithImage> diaryListView) {
        DiaryEntryWithImage selectedEntry = diaryListView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            recycleBinFeature.moveToRecycleBin(diaryEntries, selectedEntry);
            DiaryPersistence.saveEntriesToFile(currentUser, new ArrayList<>(diaryEntries));
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

        Label titleLabel = new Label("Title: " + entry.getTitle());
        titleLabel.getStyleClass().add("title-label");

        Label moodLabel = new Label("Mood: " + entry.getMood());
        moodLabel.getStyleClass().add("mood-label");

        Label dateLabel = new Label("Date: " + entry.getFormattedEntryTime());
        dateLabel.getStyleClass().add("date-label");

        TextArea contentArea = new TextArea(entry.getContent());
        contentArea.setWrapText(true);
        contentArea.setEditable(false);
        contentArea.getStyleClass().add("text-area");

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

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("styled-button");
        closeButton.setOnAction(event -> dialog.close());

        Label contentLabel = new Label("Content:");
        contentLabel.getStyleClass().add("content-label");

        Label imagesLabel = new Label("Images:");
        imagesLabel.getStyleClass().add("images-label");

        layout.getChildren().addAll(titleLabel, moodLabel, dateLabel, contentLabel, contentArea, imagesLabel, imageBox, closeButton);

        Scene scene = new Scene(layout, 400, 600);
        scene.getStylesheets().add(getClass().getResource(themeCssFiles.get(currentThemeKey)).toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }
}





































