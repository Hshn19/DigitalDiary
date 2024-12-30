package com.example.myapp.demo1;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

public class UserLoginRegistration {

    public String openLoginScreen(Stage stage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Login");
        dialog.setHeaderText("Enter your username");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String username = result.get().trim();
            System.out.println("Logged in as: " + username);
            return username; // Return the username if login is successful
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Username cannot be empty.");
            return null; // Return null if login fails
        }
    }

    public void openRegistrationScreen(Stage stage) {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Register");
        usernameDialog.setHeaderText("Enter your username");
        usernameDialog.setContentText("Username:");

        Optional<String> usernameResult = usernameDialog.showAndWait();
        if (usernameResult.isPresent() && !usernameResult.get().trim().isEmpty()) {
            String username = usernameResult.get().trim();

            TextInputDialog emailDialog = new TextInputDialog();
            emailDialog.setTitle("Register");
            emailDialog.setHeaderText("Enter your email");
            emailDialog.setContentText("Email:");

            Optional<String> emailResult = emailDialog.showAndWait();
            if (emailResult.isPresent() && !emailResult.get().trim().isEmpty()) {
                String email = emailResult.get().trim();
                registerUser(username, email);
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Email cannot be empty.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username cannot be empty.");
        }
    }

    private void registerUser(String username, String email) {
        // Implement your user registration logic here.
        System.out.println("Registered user: " + username + ", Email: " + email);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}





