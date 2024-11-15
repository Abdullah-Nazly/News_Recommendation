package org.example.newsrecommender;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReadArticle implements Initializable {

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextArea articleTextArea;

    @FXML
    private Button backButton;
    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    private ObservableList<String> categories;
    private List<String> articleUrls = new ArrayList<>();
    private int currentArticleIndex = 0;

    private int userId;
    private long articleId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        categoryComboBox.setOnAction(event -> loadArticlesByCategory(categoryComboBox.getValue()));
    }

    private void loadCategories() {
        categories = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT category FROM articles";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            categoryComboBox.setItems(categories);

        } catch (SQLException e) {
            showAlert("Database Error", "Unable to load categories.");
            e.printStackTrace();
        }
    }

    private void loadArticlesByCategory(String category) {
        articleUrls.clear();
        String query = "SELECT link FROM articles WHERE category = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                articleUrls.add(rs.getString("link"));
            }

            if (!articleUrls.isEmpty()) {
                currentArticleIndex = 0;
                displayArticleContent(articleUrls.get(currentArticleIndex));
                enableNavigationButtons();
            } else {
                articleTextArea.setText("No articles found in this category.");
                disableNavigationButtons();
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Unable to load articles.");
            e.printStackTrace();
        }
    }

    private void displayArticleContent(String articleUrl) {
        try {
            // Connect to the URL and fetch the HTML content
            Document doc = Jsoup.connect(articleUrl).get();

            // Extract the paragraphs from the HTML and format them with line breaks
            StringBuilder articleText = new StringBuilder();
            doc.select("p").forEach(paragraph -> {
                String text = paragraph.text().trim();
                if (!text.isEmpty()) {
                    articleText.append(text).append("\n"); // Add a line break after each paragraph
                }
            });

            // If no paragraphs are found, use the body text as a fallback
            if (articleText.length() == 0) {
                articleText.append(doc.body().text().replaceAll("(?<=\\.)\\s+", "\n\n")); // Split on sentence end
            }

            // Remove the last unnecessary newline at the end of the article
            String finalText = articleText.toString().trim();

            // Display the formatted text content in TextArea
            articleTextArea.setText(finalText);

        } catch (IOException e) {
            showAlert("Network Error", "Unable to load the article.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("application.fxml"));
            Parent applicationView = loader.load();
            Stage stage = (Stage) articleTextArea.getScene().getWindow();
            stage.setScene(new Scene(applicationView));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading application.fxml");
        }
    }
    @FXML
    private void handleNextButton() {
        if (currentArticleIndex < articleUrls.size() - 1) {
            currentArticleIndex++;
            displayArticleContent(articleUrls.get(currentArticleIndex));
            enableNavigationButtons();
        }
    }

    @FXML
    private void handlePreviousButton() {
        if (currentArticleIndex > 0) {
            currentArticleIndex--;
            displayArticleContent(articleUrls.get(currentArticleIndex));
            enableNavigationButtons();
        }
    }

    private void enableNavigationButtons() {
        // Enable or disable the navigation buttons based on the index
        previousButton.setDisable(currentArticleIndex == 0);
        nextButton.setDisable(currentArticleIndex == articleUrls.size() - 1);
    }

    private void disableNavigationButtons() {
        previousButton.setDisable(true);
        nextButton.setDisable(true);
    }

    // Setter methods for userId and articleId
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }
}
