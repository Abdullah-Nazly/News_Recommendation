package org.example.newsrecommender;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReadArticle implements Initializable {

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private WebView articleWebView;
    @FXML
    private Button backButton;

    private ObservableList<String> categories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();

        // Load articles when category is selected
        categoryComboBox.setOnAction(event -> loadArticleByCategory(categoryComboBox.getValue()));
    }

    private void loadCategories() {
        categories = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT category FROM articles";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                categories.add(category);
            }
            categoryComboBox.setItems(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load categories.");
        }
    }

    private void loadArticleByCategory(String category) {
        String query = "SELECT link FROM articles WHERE category = ? LIMIT 1"; // Load the first article URL for simplicity

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String url = rs.getString("link");
                loadUrlInWebView(url);
            } else {
                showAlert("No Articles", "No articles found in this category.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load article URL.");
        }
    }

    private void loadUrlInWebView(String url) {
        WebEngine webEngine = articleWebView.getEngine();
        webEngine.load(url);
    }

    @FXML
    private void handleBackButton() {
        try {
            // Load application.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("application.fxml"));
            Parent applicationView = loader.load();

            // Get the current stage (the window) and set it to the application.fxml scene
            Stage stage = (Stage) articleWebView.getScene().getWindow();
            stage.setScene(new Scene(applicationView));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading application.fxml");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
