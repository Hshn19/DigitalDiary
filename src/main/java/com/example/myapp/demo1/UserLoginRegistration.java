package com.example.myapp.demo1;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserLoginRegistration {
    private static final String USER_DATA_FILE = "users.dat";

    private final Map<String, User> users = new HashMap<>();

    public UserLoginRegistration() {
        loadUsers();
    }

    /**
     * Opens the registration screen.
     */
    public void openRegistrationScreen(Stage primaryStage) {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register");

        VBox layout = new VBox(10);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button registerButton = new Button("Register");
        registerButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "All fields are required.");
            } else if (users.containsKey(email) || users.containsKey(username)) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "Email or Username already exists.");
            } else {
                users.put(email, new User(email, username, password));
                users.put(username, new User(email, username, password));
                saveUsers();
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", "User registered successfully.");
                registerStage.close();
            }
        });

        layout.getChildren().addAll(new Label("Register a new account"), emailField, usernameField, passwordField, registerButton);

        Scene scene = new Scene(layout, 300, 200);
        registerStage.setScene(scene);
        registerStage.show();
    }

    /**
     * Opens the login screen.
     */
    public String openLoginScreen(Stage primaryStage) {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        VBox layout = new VBox(10);

        TextField emailOrUsernameField = new TextField();
        emailOrUsernameField.setPromptText("Email or Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");

        final String[] loggedInUser = {null};
        loginButton.setOnAction(event -> {
            String emailOrUsername = emailOrUsernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (emailOrUsername.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Login Error", "All fields are required.");
            } else {
                User user = users.get(emailOrUsername);
                if (user != null && user.getPassword().equals(password)) {
                    showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome, " + user.getUsername() + "!");
                    loggedInUser[0] = user.getUsername(); // Store the username of the logged-in user
                    loginStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid credentials.");
                }
            }
        });

        layout.getChildren().addAll(new Label("Login"), emailOrUsernameField, passwordField, loginButton);

        Scene scene = new Scene(layout, 300, 200);
        loginStage.setScene(scene);
        loginStage.showAndWait();

        return loggedInUser[0]; // Return the logged-in username
    }

    /**
     * Loads users from a file.
     */
    private void loadUsers() {
        File file = new File(USER_DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Map<String, User> loadedUsers = (Map<String, User>) ois.readObject();
                users.putAll(loadedUsers);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves users to a file.
     */
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * User class for storing user data.
     */
    static class User implements Serializable {
        private final String email;
        private final String username;
        private final String password;

        public User(String email, String username, String password) {
            this.email = email;
            this.username = username;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}






