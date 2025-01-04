package com.example.myapp.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DiaryPersistence {
    private static final String DATA_DIR = "data";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Load diary entries from a file for the specified user.
     *
     * @param username The username of the user whose entries are to be loaded.
     * @return An ObservableList of diary entries.
     */
    public static ObservableList<DiaryEntryWithImage> loadEntriesFromFile(String username) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Create the directory if it doesn't exist
        }

        File file = new File(dataDir, username + "_diary_entries.csv");
        ObservableList<DiaryEntryWithImage> entries = FXCollections.observableArrayList();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", -1); // Split by commas, allow empty values
                    if (parts.length >= 5) { // Ensure all necessary fields are present
                        LocalDateTime entryTime = LocalDateTime.parse(parts[0], DATE_TIME_FORMATTER);
                        String title = parts[1];
                        String content = parts[2];
                        String mood = parts[3];
                        String[] imagePaths = parts[4].isEmpty() ? new String[0] : parts[4].split(";");

                        DiaryEntryWithImage entry = new DiaryEntryWithImage(title, content, mood, List.of(imagePaths));
                        entry.setEntryTime(entryTime); // Set the loaded date
                        entries.add(entry);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: Unable to load diary entries. Details: " + e.getMessage());
            }
        }

        return entries;
    }

    /**
     * Save diary entries to a file for the specified user.
     *
     * @param username The username of the user whose entries are to be saved.
     * @param entries  The list of diary entries to save.
     */
    public static void saveEntriesToFile(String username, List<DiaryEntryWithImage> entries) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Ensure the directory exists
        }

        File file = new File(dataDir, username + "_diary_entries.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (DiaryEntryWithImage entry : entries) {
                String imagePaths = String.join(";", entry.getImagePaths()); // Join image paths with semicolons
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                        entry.getFormattedEntryTime(), // Save the entry date
                        sanitize(entry.getTitle()),
                        sanitize(entry.getContent()),
                        sanitize(entry.getMood()),
                        sanitize(imagePaths)
                ));
            }
        } catch (IOException e) {
            System.out.println("Error: Unable to save diary entries. Details: " + e.getMessage());
        }
    }

    /**
     * Sanitize input to avoid issues with commas and newlines in CSV.
     *
     * @param input The string to sanitize.
     * @return A sanitized string.
     */
    private static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(",", "\\,").replace("\n", "\\n");
    }
}



















