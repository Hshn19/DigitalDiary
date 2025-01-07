package com.example.myapp.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DiaryPersistence {
    private static final String DATA_DIR = "data";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ObservableList<DiaryEntryWithImage> loadEntriesFromFile(String username) {
        ObservableList<DiaryEntryWithImage> entries = FXCollections.observableArrayList();
        File file = new File(DATA_DIR, username + "_diary_entries.csv");

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] parts = parseCSVLine(line);
                        if (parts.length >= 5) {
                            String title = parts[0];
                            String content = parts[1];
                            String mood = parts[2];
                            String[] imagePaths = parts[3].isEmpty() ? new String[0] : parts[3].split(";");
                            LocalDateTime entryTime = LocalDateTime.parse(parts[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                            entries.add(new DiaryEntryWithImage(title, content, mood, List.of(imagePaths), entryTime));
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing entry: " + line + ". Reason: " + e.getMessage());

                    }
                }
            } catch (IOException e) {
                System.out.println("Error: Unable to load diary entries. Details: " + e.getMessage());
            }
        }

        return entries;
    }

    private static String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inEscape = false;

        for (char c : line.toCharArray()) {
            if (inEscape) {
                currentToken.append(c); // Add escaped character
                inEscape = false;
            } else if (c == '\\') {
                inEscape = true; // Start of escape sequence
            } else if (c == ',') {
                tokens.add(currentToken.toString());
                currentToken.setLength(0); // Reset for the next token
            } else {
                currentToken.append(c);
            }
        }
        tokens.add(currentToken.toString()); // Add the last token

        return tokens.toArray(new String[0]);
    }



    public static void saveEntriesToFile(String username, List<DiaryEntryWithImage> entries) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Ensure the directory exists
        }

        File file = new File(dataDir, username + "_diary_entries.csv");
        backupFile(file); // Backup the current file before saving

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (DiaryEntryWithImage entry : entries) {
                String imagePaths = String.join(";", entry.getImagePaths()); // Join image paths with semicolons
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                        sanitize(entry.getTitle()),
                        sanitize(entry.getContent()),
                        sanitize(entry.getMood()),
                        sanitize(imagePaths),
                        entry.getEntryTime().format(DATE_FORMATTER)
                ));
            }
            System.out.println("Entries saved successfully. Total entries: " + entries.size());
        } catch (IOException e) {
            System.out.println("Error: Unable to save diary entries. Details: " + e.getMessage());
        }
    }

    private static void backupFile(File file) {
        if (file.exists()) {
            File backup = new File(file.getParent(), file.getName() + ".bak");
            try {
                Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backup created: " + backup.getPath());
            } catch (IOException e) {
                System.out.println("Error creating backup: " + e.getMessage());
            }
        }
    }

    private static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(",", "\\,").replace("\n", "\\n");
    }
}
























