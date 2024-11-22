package org.example.newsrecommender.Admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class LoginHandler {

    protected abstract boolean verifyCredentials(String password);

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
