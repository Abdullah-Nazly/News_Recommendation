package org.example.newsrecommender.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.newsrecommender.BaseController;
import org.example.newsrecommender.Session;
import org.example.newsrecommender.db.DB;

import java.io.IOException;
import java.util.Objects;

public class EditProfile implements BaseController {
    public Button saveButton;
    public Button backButton;
    public TextField userName;
    public TextField password;
    public TextField email;
    public TextField contact;
    private User currentUser;


    @FXML
    public void initialize() {
        // Fetch the current user from the session
        if (!Session.isLoggedIn()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user is logged in.");
            return;
        }

        currentUser = Session.getCurrentUser();

        // Populate fields with current user details
        userName.setText(currentUser.getUsername());
        password.setText(currentUser.getPassword());
        email.setText(currentUser.getEmail());
        contact.setText(currentUser.getContact());
    }
    @FXML
    public void handleSaveButton(ActionEvent event) {
        String newUsername = userName.getText().trim();
        String newPassword = password.getText().trim();
        String newEmail = email.getText().trim();
        String newContact = contact.getText().trim();

        // Validate input
        if (newUsername.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty() || newContact.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "All fields are required. Please fill out all fields.");
            return;
        }

        if (!newContact.matches("\\d{10}")) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Contact number must be exactly 10 digits.");
            return;
        }
        if (!isValidEmail(newEmail)) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Invalid email format. Please enter a valid email address.");
            return;
        }


        if (isDuplicateUser(newUsername, newEmail, newContact)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "Username, email, or contact number already exists. Please choose different values.");
            return;
        }

        // Update user object
        currentUser.setUsername(newUsername);
        currentUser.setPassword(newPassword);
        currentUser.setEmail(newEmail);
        currentUser.setContact(newContact);

        // Update user in database
        updateUserInDatabase();

        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
    }

    // Utility method to validate email format
    private boolean isValidEmail(String email) {
        // Regular expression for validating email addresses
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    // Helper method to update the user in the database
    private void updateUserInDatabase() {
        var database = DB.getDatabase();
        var usersCollection = database.getCollection("users");

        // Create an update document
        Document updateDoc = new Document("$set", new Document("username", currentUser.getUsername())
                .append("password", currentUser.getPassword())
                .append("email", currentUser.getEmail())
                .append("contact", currentUser.getContact()));

        // Update the user document in the database
        usersCollection.updateOne(new Document("_id", currentUser.getUserId()), updateDoc);
    }

    // Helper method to check for duplicate entries in the database
    private boolean isDuplicateUser(String username, String email, String contact) {
        var database = DB.getDatabase();
        var usersCollection = database.getCollection("users");

        // Query for duplicates, excluding the current user's document
        Document usernameQuery = new Document("username", username).append("_id", new Document("$ne", currentUser.getUserId()));
        Document emailQuery = new Document("email", email).append("_id", new Document("$ne", currentUser.getUserId()));
        Document contactQuery = new Document("contact", contact).append("_id", new Document("$ne", currentUser.getUserId()));

        // Check for duplicates
        boolean isUsernameTaken = usersCollection.find(usernameQuery).first() != null;
        boolean isEmailTaken = usersCollection.find(emailQuery).first() != null;
        boolean isContactTaken = usersCollection.find(contactQuery).first() != null;

        return isUsernameTaken || isEmailTaken || isContactTaken;
    }
    @FXML
    private void handleBackButton() {
        // Get the current stage (window) from the back button's scene
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        // Use navigateToView method to navigate to the Profile screen
        navigateToView(currentStage, "/org/example/newsrecommender/Profile.fxml", null);
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlFile));
            javafx.scene.Parent root = loader.load();

            currentStage.setScene(new javafx.scene.Scene(root));

            if (title != null) {
                currentStage.setTitle(title);
            }

            // Remove window decorations if required
            if (noDecoration) {
                currentStage.initStyle(javafx.stage.StageStyle.UTILITY); // Removes title bar, close, minimize buttons
                currentStage.setOpacity(1.0); // Ensures the window is fully opaque
            }

            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
