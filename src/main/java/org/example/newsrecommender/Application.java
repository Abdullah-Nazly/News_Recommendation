package org.example.newsrecommender;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.newsrecommender.db.DB;

import java.io.IOException;

public class Application {

    @FXML
    private Button button_logout;
    @FXML
    private Button readbutton;
    @FXML
    private Label label_welcome;

    // This method can be used to handle actions like reading articles
    @FXML
    public void openArticle() {
        try {
            // Load the ReadArticle.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/newsrecommender/ReadArticle.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) readbutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Read Article");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is bound to a logout button or some logout action
    @FXML
    public void logoutAction() {
        // Here we can log out the user and navigate them to the login screen
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/newsrecommender/UserLogin.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) button_logout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is called when the window is closed to safely close the DB connection
    public void closeApp() {
        DB.close();
    }
}
