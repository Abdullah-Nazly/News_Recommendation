package org.example.newsrecommender;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class LoginHandler {
    // Abstract method to verify the credentials entered by the user.

    protected abstract boolean verifyCredentials(String password);

    //Navigates to a new view by loading the specified FXML file and setting it as the current scene.
    protected void navigateToView(Stage currentStage, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Set the new scene on the provided stage
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}