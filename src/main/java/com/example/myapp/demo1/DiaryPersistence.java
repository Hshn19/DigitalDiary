package com.example.myapp.demo1;

import javafx.collections.FXCollections;

import java.io.*;
import java.util.List;

public class DiaryPersistence {

    private static final String DATA_DIR = "data"; // Directory to store user data

    /**
     * Load diary entries from a file for the specified user.
     *
     * @param username The username of the user whose entries are to be loaded.
     * @return A list of diary entries.
     */
    public static List<DiaryEntryWithImage> loadEntriesFromFile(String username) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Create the directory if it doesn't exist
        }

        File file = new File(dataDir, username + "_diary_entries.dat");

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<DiaryEntryWithImage> entries = (List<DiaryEntryWithImage>) ois.readObject();
                return entries != null ? entries : FXCollections.observableArrayList(); // Return loaded entries or empty list
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error: Unable to load diary entries. Details: " + e.getMessage());
                return FXCollections.observableArrayList(); // Return empty list on error
            }
        } else {
            return FXCollections.observableArrayList(); // Return empty list if file does not exist
        }
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
            dataDir.mkdirs(); // Create the directory if it doesn't exist
        }

        File file = new File(dataDir, username + "_diary_entries.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(entries); // Write entries to file
        } catch (IOException e) {
            System.out.println("Error: Unable to save diary entries. Details: " + e.getMessage());
        }
    }
}







