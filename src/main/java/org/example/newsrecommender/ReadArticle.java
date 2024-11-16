package org.example.newsrecommender;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.bson.Document;
import org.example.newsrecommender.db.DB;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReadArticle implements Initializable {

    @FXML
    private ComboBox<String> categoryComboBox;  // ComboBox to display categories

    @FXML
    private TextArea articleTextArea;  // TextArea to display the article content

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    private ObservableList<String> categories;  // List to hold category names
    private List<String> articleUrls = new ArrayList<>();  // List of article URLs
    private int currentArticleIndex = 0;  // Current article index for navigation

    private MongoDatabase database;  // MongoDatabase instance

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize MongoDB connection (assumed that DB.getDatabase() gives the database)
        database = DB.getDatabase();

        // Fetch and populate the ComboBox with categories
        fetchCategories();
    }

    // Method to fetch available categories from the database and populate ComboBox
    private void fetchCategories() {
        MongoCollection<Document> articlesCollection = database.getCollection("articles");

        // List to store category names
        List<String> categoryList = new ArrayList<>();

        // Fetch distinct categories from the database
        articlesCollection.distinct("category", String.class).forEach(categoryList::add);

        // Convert the category list to an ObservableList
        categories = FXCollections.observableArrayList(categoryList);

        // Set the ComboBox items to the list of categories
        categoryComboBox.setItems(categories);

    }

    // Handle the category selection and load the article(s) from the database
    @FXML
    public void handleCategorySelection(ActionEvent event) {
        String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            System.out.println("Selected Category: " + selectedCategory);

            // Fetch articles of the selected category
            fetchArticlesByCategory(selectedCategory);
        }
    }

    // Method to fetch articles of the selected category from the database
    private void fetchArticlesByCategory(String selectedCategory) {
        MongoCollection<Document> articlesCollection = database.getCollection("articles");

        // Query to fetch articles from the selected category
        List<Document> articles = articlesCollection.find(new Document("category", selectedCategory)).into(new ArrayList<>());

        // Clear the list of article URLs
        articleUrls.clear();

        // Add the article URLs to the list
        for (Document article : articles) {
            articleUrls.add(article.getString("link"));  // Assuming the link field stores the article URL
        }

        // If there are articles, load the first one
        if (!articleUrls.isEmpty()) {
            loadArticleContent(articleUrls.get(0));
        } else {
            showAlert("No Articles", "No articles found in this category.");
        }
    }

    // Method to load the content of the article based on the URL
    private void loadArticleContent(String articleUrl) {
        try {
            // Fetch the article content from the link using JSoup
            org.jsoup.nodes.Document doc = Jsoup.connect(articleUrl).get();  // No aliasing needed here

            // Extract the article content (you can customize this based on the structure of the article page)
            StringBuilder articleContent = new StringBuilder();

            // Extract text from each paragraph <p> and add a line break after each
            doc.select("p").forEach(paragraph -> {
                articleContent.append(paragraph.text()).append("\n");  // Add two line breaks for readability
            });

            // Display the article content in the TextArea
            articleTextArea.setText(articleContent.toString());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load article from the link.");
        }
    }

    // Method to show alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);  // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Placeholder methods for navigation (previous and next buttons)
    public void handleNextButton(ActionEvent event) {
        if (currentArticleIndex < articleUrls.size() - 1) {
            currentArticleIndex++;
            loadArticleContent(articleUrls.get(currentArticleIndex));
        }
    }

    public void handlePreviousButton(ActionEvent event) {
        if (currentArticleIndex > 0) {
            currentArticleIndex--;
            loadArticleContent(articleUrls.get(currentArticleIndex));
        }
    }

    public void handleBackButton(ActionEvent event) {
        // Handle back button (navigate to previous screen)
    }

    public void handleLikeButton(ActionEvent event) {
        // Handle like button functionality
    }
}
