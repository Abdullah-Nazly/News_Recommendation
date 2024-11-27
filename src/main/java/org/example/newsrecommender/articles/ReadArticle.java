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

    private ContentLoader articleLoader;
    private ObservableList<String> categories;
    private List<Article> articles;
    private int currentArticleIndex = 0;
    private MongoDatabase database;
    private UserPoints userPoints;

    public ReadArticle() {
        this.database = DB.getDatabase();
        this.articleLoader = new ArticleLoader(database);
        this.userPoints = new UserPoints(database);
    }

    @FXML
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        MongoCollection<Document> collection = DB.getDatabase().getCollection("articles");
        List<String> categoryList = collection.distinct("category", String.class).into(new ArrayList<>());
        categories = FXCollections.observableArrayList(categoryList);
        categoryComboBox.setItems(categories);
    }

    @FXML
    public void handleCategorySelection(ActionEvent event) {
        String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            loadHeadlinesByCategory(selectedCategory);
            User currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                userPoints.addClickPoints(currentUser.getUserId(), selectedCategory);
            }
        }
    }

    private void loadHeadlinesByCategory(String selectedCategory) {
        articles = articleLoader.loadArticles(selectedCategory);  // articles is now a List<Article>
        headlinesVBox.getChildren().clear();

        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            String headline = article.getHeadline();  // Use getHeadline method

            Label headlineLabel = new Label((i + 1) + ". " + headline);
            headlineLabel.setStyle("-fx-font-size: 10px; -fx-padding: 6px;");
            headlineLabel.setOnMouseEntered(event -> headlineLabel.setStyle("-fx-font-size: 10px;-fx-padding: 6px;-fx-cursor: hand;"));
            headlineLabel.setOnMouseExited(event -> headlineLabel.setStyle("-fx-font-size: 10px;-fx-padding: 6px;-fx-cursor: default;"));

            headlineLabel.setOnMouseClicked(event -> loadArticleContent(article));
            headlinesVBox.getChildren().add(headlineLabel);
        }
    }

    private void loadArticleContent(Article article) {
    String content = articleLoader.loadArticleContentFromUrl(article.getLink());  // Use Article's link
    articleTextArea.setText(content);
}

    @FXML
    public void handleLikeButton(ActionEvent event) {
        if (articles != null && !articles.isEmpty()) {
            Article currentArticle = articles.get(0);
            String articleCategory = currentArticle.getCategory();

            User currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                userPoints.addCategoryLike(currentUser.getUserId(), articleCategory);
                showAlert("Liked", "You liked the category: " + articleCategory);
            } else {
                showAlert("Error", "User not logged in.");
            }
        } else {
            showAlert("Error", "No articles loaded. Please select a category first.");
        }
    }

    @FXML
    public void handleDislikeButton(ActionEvent event) {
        if (articles != null && !articles.isEmpty()) {
            Article currentArticle = articles.get(0);
            String articleCategory = currentArticle.getCategory();

            User currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                userPoints.dislikeCategory(currentUser.getUserId(), articleCategory);
                showAlert("Disliked", "You disliked the category: " + articleCategory);
            } else {
                showAlert("Error", "User not logged in.");
            }
        } else {
            showAlert("Error", "No articles loaded. Please select a category first.");
        }
    }

    @FXML
    public void handleSaveButton(ActionEvent event) {
        if (articles != null && !articles.isEmpty()) {
            Article currentArticle = articles.get(0);
            String articleCategory = currentArticle.getCategory();

            User currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                userPoints.saveCategory(currentUser.getUserId(), articleCategory);
                showAlert("Saved", "You saved the category: " + articleCategory);
            } else {
                showAlert("Error", "User not logged in.");
            }
        } else {
            showAlert("Error", "No articles loaded. Please select a category first.");
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
