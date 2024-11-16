package org.example.newsrecommender;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.newsrecommender.db.DB;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the database connection
            DB.connect();

            // Load the main FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("boot.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("News Recommender");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            // Ensure DB connection is closed on application exit
            primaryStage.setOnCloseRequest(event -> DB.close());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load FXML file.", e);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
