package com.example.myapp.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DiaryPersistence {
    private static final String DATA_DIR = "data";

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
                    if (parts.length >= 4) {
                        String title = parts[0];
                        String content = parts[1];
                        String mood = parts[2];
                        String[] imagePaths = parts[3].equals("null") ? new String[0] : parts[3].split(";");
                        entries.add(new DiaryEntryWithImage(title, content, mood, List.of(imagePaths)));
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
                writer.write(String.format("%s,%s,%s,%s\n",
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


















