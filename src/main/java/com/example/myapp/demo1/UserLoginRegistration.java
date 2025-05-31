package com.example.myapp.demo1;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

    public void openRegistrationScreen(Stage primaryStage) {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label registerLabel = new Label("Register a new account");
        registerLabel.getStyleClass().add("title-label");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("styled-button");
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

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("styled-button");
        cancelButton.setOnAction(event -> registerStage.close());

        HBox buttonBox = new HBox(10, registerButton, cancelButton);
        layout.getChildren().addAll(registerLabel, emailField, usernameField, passwordField, buttonBox);

        Scene scene = new Scene(layout, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/themes/light.css").toExternalForm());
        registerStage.setScene(scene);
        registerStage.show();
    }

    public String openLoginScreen(Stage primaryStage) {
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label loginLabel = new Label("Login");
        loginLabel.getStyleClass().add("title-label");

        TextField emailOrUsernameField = new TextField();
        emailOrUsernameField.setPromptText("Email or Username");
        emailOrUsernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("styled-button");
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
                    loggedInUser[0] = user.getUsername();
                    loginStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid credentials.");
                }
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("styled-button");
        cancelButton.setOnAction(event -> loginStage.close());

        HBox buttonBox = new HBox(10, loginButton, cancelButton);
        layout.getChildren().addAll(loginLabel, emailOrUsernameField, passwordField, buttonBox);

        Scene scene = new Scene(layout, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/themes/light.css").toExternalForm());
        loginStage.setScene(scene);
        loginStage.showAndWait();

        return loggedInUser[0];
    }

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

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

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







