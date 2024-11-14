package org.example.newsrecommender;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class  Application implements Initializable {

    public Button readbutton;
    @FXML
    private Button button_logout;
    @FXML
    private Label label_welcome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void setUserInformation(String username ){
        label_welcome.setText("Welcome" + username + "!");

    }

    @FXML
    private void handleReadArticlesButton(ActionEvent event) {
        try {
            // Load the ReadArticle.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReadArticle.fxml"));
            Parent readArticleRoot = loader.load();

            // Set up a new scene with the loaded content
            Scene readArticleScene = new Scene(readArticleRoot);

            // Get the current stage
            Stage stage = (Stage) readbutton.getScene().getWindow();

            // Set the scene to the stage and show it
            stage.setScene(readArticleScene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading ReadArticle.fxml");
        }
    }
}
