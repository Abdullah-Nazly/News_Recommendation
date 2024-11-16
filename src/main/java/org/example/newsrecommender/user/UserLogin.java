package org.example.newsrecommender.user;

import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.newsrecommender.db.DB;

import java.io.IOException;

public class UserLogin {

    @FXML
    private Button signinbutton;
    @FXML
    private Button button_cancel;
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField tf_password;

    @FXML
    public void openSignUp() {
        try {
            // Load the signup.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/newsrecommender/SignUp.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) signinbutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loginAction() {
        // Get the input data from the fields and trim leading/trailing spaces
        String username = tf_username.getText().trim();
        String password = tf_password.getText().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Form Error", "Username and password must not be empty.");
            return;
        }

        // Check if the username and password match in the database
        if (isValidUser(username, password)) {
            // If valid, navigate to the main application
            navigateToApplication();
        } else {
            // If invalid, show an error alert
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    private void cancelAction() {
        // Exit the application
        System.exit(0);
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper method to validate the username and password from the database
    private boolean isValidUser(String username, String password) {
        MongoDatabase database = DB.getDatabase(); // Get the database instance
        if (database == null) {
            showAlert("Error", "Database connection error.");
            return false;
        }

        var usersCollection = database.getCollection("users");

        // Query the database for a matching username and password
        Document query = new Document("username", username)
                .append("password", password);

        // Return true if a matching document is found
        Document result = usersCollection.find(query).first();

        return result != null; // Return true if a user was found, false otherwise
    }

    // Helper method to navigate to the application screen (application.fxml)
    private void navigateToApplication() {
        try {
            // Load the application.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/newsrecommender/Application.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) signinbutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Application");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the application screen.");
        }
    }
}
