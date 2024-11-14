package org.example.newsrecommender;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public PasswordField tf_password;
    public TextField tf_username;
    @FXML
    private Button cancelButton;
    @FXML
    private Button button_login;
    @FXML
    private Button button_signup;

    @Override
    public void initialize(URL location, ResourceBundle resource){

    }

    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    public void openSignUpForm() {
        try {
            // Load the signup.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) button_signup.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = tf_username.getText().trim();
        String password = tf_password.getText().trim();

        // Validate fields are not empty or whitespace-only
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Username and password cannot be empty.");
            return;
        }

        // Attempt to log in the user
        User user = User.login(username, password);
        if (user == null) {
            // Login failed
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password.");
        } else {
            // Login successful, load application.fxml
            loadApplication();
        }
    }

    // Method to load the main application view
    private void loadApplication() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Application.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) button_login.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Application Error", "Failed to load application.");
        }
    }

    // Utility method to display alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}