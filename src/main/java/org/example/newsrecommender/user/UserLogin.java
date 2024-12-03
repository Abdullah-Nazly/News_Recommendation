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
import org.bson.types.ObjectId;
import org.example.newsrecommender.BaseController;
import org.example.newsrecommender.LoginHandler;
import org.example.newsrecommender.Session;
import org.example.newsrecommender.db.DB;

import java.io.IOException;

public class UserLogin extends LoginHandler implements BaseController {

    @FXML
    private Button signinbutton;
    @FXML
    private Button button_cancel;
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField tf_password;
    private MongoDatabase database;
    private UserLogs userLogs;

    public UserLogin() {
        // Initialize database and UserLogs instance
        this.database = DB.getDatabase();
        this.userLogs = new UserLogs(database);
    }

    @FXML
    public void openSignUp() {
        Stage currentStage = (Stage) signinbutton.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/SignUp.fxml", "Sign Up");
    }

    @FXML
    public void loginAction() {
        String username = tf_username.getText().trim();
        String password = tf_password.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Form Error", "Username and password must not be empty.");
            return;
        }

        if (verifyCredentials(password)) {
            User user = getUserByUsername(username);
            if (user != null) {


                Session.setCurrentUser(user, database);  // Pass database to ensure UserLikes is initialized
                userLogs.recordLogin(user.getUserId(), user.getUsername()); // Using getters from the User class
                navigateToApplication();
            } else {
                showAlert("Login Error", "User not found.");
            }
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }


    @FXML
    private void cancelAction() {
        Stage currentStage = (Stage) button_cancel.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/boot.fxml", "Boot");
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
    @Override
    protected boolean verifyCredentials(String password) {
        String username = tf_username.getText().trim();
        var usersCollection = database.getCollection("users");
        Document query = new Document("username", username).append("password", password);
        return usersCollection.find(query).first() != null;
    }

    // Helper method to get user ID based on the username
    private User getUserByUsername(String username) {
        var usersCollection = database.getCollection("users");
        Document query = new Document("username", username);
        Document result = usersCollection.find(query).first();

        if (result != null) {
            String fetchedUsername = result.getString("username");
            String fetchedPassword = result.getString("password");
            String fetchedEmail = result.getString("email");
            String fetchedContact = result.getString("contact");
            ObjectId userId = result.getObjectId("_id");


            return new User(userId, fetchedUsername, fetchedPassword, fetchedEmail, fetchedContact);
        }
        return null;
    }

    // Helper method to navigate to the application screen (application.fxml)
    private void navigateToApplication() {
        Stage stage = (Stage) signinbutton.getScene().getWindow();
        navigateToView(stage, "/org/example/newsrecommender/Application.fxml", "Application");
    }

    @Override
    public void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration) {
        try {
            // Standard navigation logic, could be overridden if needed
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            currentStage.setScene(new Scene(root));
            if (title != null) {
                currentStage.setTitle(title);
            }
            if (noDecoration) {
                currentStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            }

            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load " + fxmlFile);
        }
    }

    @Override
    public void navigateToView(Stage currentStage, String fxmlFile) {
        BaseController.super.navigateToView(currentStage, fxmlFile);
    }

    @Override
    public void navigateToView(Stage currentStage, String fxmlFile, String title) {
        BaseController.super.navigateToView(currentStage, fxmlFile, title);
    }
}