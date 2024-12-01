package org.example.newsrecommender.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SysLogin extends LoginHandler {

    @FXML
    private PasswordField password_field;
    @FXML
    private Button loginButton;
    @FXML
    private Button backButton;

    // Encapsulated default password
    private final String defaultPassword = "admin123";

    // This method is called by JavaFX after FXML file is loaded
    @FXML
    private void initialize() {
        // Configure the login button action here, after FXML injection
        loginButton.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String enteredPassword = password_field.getText().trim();
        if (verifyCredentials(enteredPassword)) {
            navigateToAdminPanel();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect password. Please try again.");
        }
    }

    @Override
    protected boolean verifyCredentials(String password) {
        return defaultPassword.equals(password);
    }
    private void navigateToAdminPanel() {
        // Get the current stage from any node in the scene (e.g., password_field)
        Stage currentStage = (Stage) password_field.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/systemAdmin.fxml", "System Admin Panel");
    }
    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/newsrecommender/boot.fxml")));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}