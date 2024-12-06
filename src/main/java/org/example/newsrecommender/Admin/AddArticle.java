package org.example.newsrecommender.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.newsrecommender.BaseController;
import org.example.newsrecommender.db.DB;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class AddArticle implements BaseController { // interface with overriding method is used

    public TextField link;
    public TextField headline;
    public TextField category;
    public TextField short_description;
    @FXML
    Button backButton;

    public void handleAddButton() {
        String linkText = link.getText().trim();
        String headlineText = headline.getText().trim();
        String categoryText = category.getText().trim().toUpperCase(); // Convert category to uppercase
        String descriptionText = short_description.getText().trim();

        // Validate inputs
        if (linkText.isEmpty() || headlineText.isEmpty() || categoryText.isEmpty() || descriptionText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "All fields are required. Please fill out all fields.");
            return;
        }
        // Validate the link format
        // Validate the link format
        if (!isValidUrl(linkText)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Link", "The link provided is not valid. Please enter a valid URL.");
            return;
        }

        // Save article to the database
        saveArticle(linkText, headlineText, categoryText, descriptionText);

        // Show success alert
        showAlert(Alert.AlertType.INFORMATION, "Success", "Article added successfully!");

        // Clear the text fields after saving
        link.clear();
        headline.clear();
        category.clear();
        short_description.clear();
    }

    void saveArticle(String link, String headline, String category, String date) {
        // Create a connection to the database
        var database = DB.getDatabase();
        var articlesCollection = database.getCollection("articles");

        // Create the article document
        Document article = new Document("link", link)
                .append("headline", headline)
                .append("category", category)
                .append("short_description", date);

        // Insert the article into the collection
        articlesCollection.insertOne(article);
    }

    //Validates the URL format to ensure it's a well-formed absolute URL.
    private boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true; // If successful, it's a valid URL
        } catch (MalformedURLException e) {
            return false; // Return false if URL is malformed
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

    @FXML
    private void handleBackButton() { // back button
        navigateToView((Stage) backButton.getScene().getWindow(), "/org/example/newsrecommender/SystemAdmin.fxml");
    }

    @Override // overriding method
    public void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxmlFile));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            if (noDecoration) {
                currentStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            }

            if (title != null) {
                currentStage.setTitle(title);
            }
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
