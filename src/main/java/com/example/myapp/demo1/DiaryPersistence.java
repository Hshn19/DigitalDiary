package com.example.myapp.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.List;

public class DiaryPersistence {

    private static final String DATA_DIR = "data";

    public static ObservableList<DiaryEntryWithImage> loadEntriesFromFile(String username) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(dataDir, username + "_diary_entries.csv");
        ObservableList<DiaryEntryWithImage> entries = FXCollections.observableArrayList();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String title = parts[0];
                        String content = parts[1];
                        String mood = parts[2];
                        String imagePath = parts[3].equals("null") ? null : parts[3];
                        entries.add(new DiaryEntryWithImage(title, content, mood, imagePath));
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: Unable to load diary entries. Details: " + e.getMessage());
            }
        }

        return entries;
    }

    public static void saveEntriesToFile(String username, List<DiaryEntryWithImage> entries) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(dataDir, username + "_diary_entries.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (DiaryEntryWithImage entry : entries) {
                writer.write(String.format("%s,%s,%s,%s\n",
                        entry.getTitle(),
                        entry.getContent(),
                        entry.getMood(),
                        entry.getImagePath() != null ? entry.getImagePath() : "null"
                ));
            }
        } catch (IOException e) {
            System.out.println("Error: Unable to save diary entries. Details: " + e.getMessage());
        }
    }
}










