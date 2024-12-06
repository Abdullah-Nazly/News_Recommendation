package org.example.newsrecommender.articles;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.newsrecommender.Session;
import org.example.newsrecommender.db.DB;
import org.example.newsrecommender.user.User;
import org.example.newsrecommender.user.UserPoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReadArticle {
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextArea articleTextArea;
    @FXML
    private ScrollPane headlinesScrollPane;
    @FXML
    private VBox headlinesVBox;
    @FXML
    private Button backButton;
    @FXML
    private Button likeButton;
    @FXML
    private Button dislikeButton;
    @FXML
    private Button saveButton;
    private ContentLoader articleLoader;// Loader for fetching articles
    private ObservableList<String> categories;
    private List<Article> articles;// List of articles in the selected category
    private MongoDatabase database;
    private UserPoints userPoints;
    private Article currentArticle; // Track the currently displayed article


    public ReadArticle() {
        this.database = DB.getDatabase(); // Initialize MongoDB connection
        this.articleLoader = new ArticleLoader(database); // Initialize the article loader
        this.userPoints = new UserPoints(database); // Initialize user points management
    }

    @FXML
    public void initialize() {
        loadCategories();
        likeButton.setDisable(true); // Initially disabled until article is displayed
        dislikeButton.setDisable(true); // Initially disabled
        saveButton.setDisable(true); // Initially disabled
    }

    private void loadCategories() { // Fetch distinct categories from the articles collection
        MongoCollection<Document> collection = DB.getDatabase().getCollection("articles");
        List<String> categoryList = collection.distinct("category", String.class).into(new ArrayList<>());
        categories = FXCollections.observableArrayList(categoryList);
        categoryComboBox.setItems(categories); // Populate category dropdown
    }

    @FXML
    public void handleCategorySelection(ActionEvent event) { // Load headlines when a category is selected
        String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            loadHeadlinesByCategory(selectedCategory); // Load headlines for the selected category
            User currentUser = Session.getCurrentUser(); // Get currently logged-in user
            if (currentUser != null) {
                userPoints.addClickPoints(currentUser.getUserId(), selectedCategory); // Add points for category click
            }
        }
    }

    private void loadHeadlinesByCategory(String selectedCategory) {// Fetch articles and display their headlines
        articles = articleLoader.loadArticles(selectedCategory);
        headlinesVBox.getChildren().clear();

        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            String headline = article.getHeadline();  // Use getHeadline method

            Label headlineLabel = new Label((i + 1) + ". " + headline);
            headlineLabel.setStyle("-fx-font-size: 10px; -fx-padding: 6px;");
            headlineLabel.setOnMouseEntered(event -> headlineLabel.setStyle("-fx-font-size: 10px;-fx-padding: 6px;-fx-cursor: hand;"));
            headlineLabel.setOnMouseExited(event -> headlineLabel.setStyle("-fx-font-size: 10px;-fx-padding: 6px;-fx-cursor: default;"));

            headlineLabel.setOnMouseClicked(event -> loadArticleContent(article)); // Load article content on click
            headlinesVBox.getChildren().add(headlineLabel);
        }
    }

    private void loadArticleContent(Article article) {// Load and display the selected article content
        String content = articleLoader.loadArticleContentFromUrl(article.getLink()); // Fetch content from URL
        articleTextArea.setText(content); // Display content in text area
        currentArticle = article; // Set the current article

        // Enable the buttons after the article is displayed
        likeButton.setDisable(false);
        dislikeButton.setDisable(false);
        saveButton.setDisable(false);
    }

    @FXML
    public void handleLikeButton(ActionEvent event) {
        if (currentArticle != null) {
            ObjectId articleId = currentArticle.getId();
            String category = currentArticle.getCategory();  // Get the category of the article
            User currentUser = Session.getCurrentUser();

            if (currentUser != null) {
                if (!userPoints.isArticleLiked(currentUser.getUserId(), articleId)) {
                    userPoints.addArticleLike(currentUser.getUserId(), articleId); // Update article-specific points
                    userPoints.updateCategoryPoints(currentUser.getUserId(), category, 2);  // Update category points
                    showAlert("Liked", "You liked the article: " + currentArticle.getHeadline());
                } else {
                    showAlert("Error", "You have already liked this article.");
                }
            } else {
                showAlert("Error", "User not logged in.");
            }
        }
    }

    @FXML
    public void handleDislikeButton(ActionEvent event) {
        if (currentArticle != null) {
            ObjectId articleId = currentArticle.getId();
            String category = currentArticle.getCategory();  // Get the category of the article
            User currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                if (!userPoints.isArticleDisliked(currentUser.getUserId(), articleId)) {
                    userPoints.dislikeArticle(currentUser.getUserId(), articleId);
                    userPoints.updateCategoryPoints(currentUser.getUserId(), category, -2);  // Update category points
                    showAlert("Disliked", "You disliked the article: " + currentArticle.getHeadline());
                } else {
                    showAlert("Error", "You have already disliked this article.");
                }
            } else {
                showAlert("Error", "User not logged in.");
            }
        }
    }

    @FXML
    public void handleSaveButton(ActionEvent event) {
        if (currentArticle != null) {
            ObjectId articleId = currentArticle.getId();
            String category = currentArticle.getCategory();
            User currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                if (!userPoints.isArticleSaved(currentUser.getUserId(), articleId)) {
                    userPoints.saveArticle(currentUser.getUserId(), articleId);
                    userPoints.updateCategoryPoints(currentUser.getUserId(), category, 1);  // Update category points
                    showAlert("Saved", "You saved the article: " + currentArticle.getHeadline());
                } else {
                    showAlert("Error", "You have already saved this article.");
                }
            } else {
                showAlert("Error", "User not logged in.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/newsrecommender/Application.fxml")));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
