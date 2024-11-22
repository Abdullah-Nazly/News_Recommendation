package org.example.newsrecommender.user;

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

public class SignUp {

    @FXML
    private Button button_signup;
    @FXML
    private Button button_login;
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField tf_password;
    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_contact;

    @FXML
    public void openLogin() {
        try {
            // Load the login.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/newsrecommender/UserLogin.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) button_login.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   @FXML
    public void signUpAction() {
        String username = tf_username.getText().trim();
        String password = tf_password.getText().trim();
        String email = tf_email.getText().trim();
        String contact = tf_contact.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "All fields are required. Please fill out all fields.");
            return;
        }

        if (!contact.matches("\\d{10}")) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Contact number must be exactly 10 digits.");
            return;
        }

        if (isDuplicateUser(username, email, contact)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "Username, email, or contact number already exists. Please choose different values.");
            return;
        }

        // Create a new User object and set properties incrementally
        User newUser = new User("", "", "", ""); // Or use an empty constructor if available
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setContact(contact);

        newUser.saveToDatabase();

        showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User " + username + " successfully registered.");

        tf_username.clear();
        tf_password.clear();
        tf_email.clear();
        tf_contact.clear();
    }

    @FXML
    private void cancelAction() {
        // Exit the application
        System.exit(0);
    }
     // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper method to check if the username, email, or contact already exists in the database
    private boolean isDuplicateUser(String username, String email, String contact) {
        var database = DB.getDatabase();
        var usersCollection = database.getCollection("users");

        // Check if any user has the same username, email, or contact
        Document usernameQuery = new Document("username", username);
        Document emailQuery = new Document("email", email);
        Document contactQuery = new Document("contact", contact);

        // Query the database to find matching documents for each field
        boolean isUsernameTaken = usersCollection.find(usernameQuery).first() != null;
        boolean isEmailTaken = usersCollection.find(emailQuery).first() != null;
        boolean isContactTaken = usersCollection.find(contactQuery).first() != null;

        // If any of the fields are already taken, return true
        return isUsernameTaken || isEmailTaken || isContactTaken;
    }
}
