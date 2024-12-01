package org.example.newsrecommender.articles;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.newsrecommender.db.DB;

import java.io.IOException;
import java.util.Objects;

public class AddArticle {

    public TextField link;
    public TextField headline;
    public TextField category;
    public TextField date;
    @FXML
    private Button backButton;

    public void handleAddButton() {
        String linkText = link.getText().trim();
        String headlineText = headline.getText().trim();
        String categoryText = category.getText().trim().toUpperCase(); // Convert category to uppercase
        String dateText = date.getText().trim();

        // Validate inputs
        if (linkText.isEmpty() || headlineText.isEmpty() || categoryText.isEmpty() || dateText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "All fields are required. Please fill out all fields.");
            return;
        }

        // Save article to the database
        saveArticle(linkText, headlineText, categoryText, dateText);

        // Clear the text fields after saving
        link.clear();
        headline.clear();
        category.clear();
        date.clear();
    }

    private void saveArticle(String link, String headline, String category, String date) {
        // Create a connection to the database
        var database = DB.getDatabase();
        var articlesCollection = database.getCollection("articles");

        // Create the article document
        Document article = new Document("link", link)
                .append("headline", headline)
                .append("category", category)
                .append("date", date);

        // Insert the article into the collection
        articlesCollection.insertOne(article);
    }
    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/newsrecommender/SystemAdmin.fxml")));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to show alert messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
