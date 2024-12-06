package org.example.newsrecommender.Admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.newsrecommender.BaseController;

import java.io.IOException;
import java.util.Objects;

public class SystemAdmin implements BaseController { // interface with overriding method is used

    @FXML
    public Button AddArtcleButton;
    @FXML
    public Button DeleteUserButton;
    @FXML
    public Button DeleteArticleButton;
    @FXML
    public Button backButton;

    @FXML
    public void handleAddArticle(ActionEvent event) {
        Stage currentStage = (Stage) AddArtcleButton.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/AddArticle.fxml", null, true);
    }

    @FXML
    public void handleDeleteUser(ActionEvent event) {
        Stage currentStage = (Stage) DeleteUserButton.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/DeleteUser.fxml", null, false);
    }

    @FXML
    public void handleDeleteArticle(ActionEvent event) {
        Stage currentStage = (Stage) DeleteArticleButton.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/DeleteArticle.fxml", null, true);
    }

    @FXML
    private void handleBackButton() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/sysLogin.fxml", "system login");
    }

    // Overridden method from BaseController for navigation
    @Override
    public void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration) {
        try {
            // Load the FXML file and set the scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Set the scene and title
            currentStage.setScene(new Scene(root));
            if (title != null) {
                currentStage.setTitle(title);
            }

            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show an alert or handle the error in a user-friendly way
        }
    }
}
