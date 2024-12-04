package org.example.newsrecommender.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.newsrecommender.BaseController;
import org.example.newsrecommender.Session;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.db.DB;
import org.example.newsrecommender.db.DBservice;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Profile implements BaseController {

    public Button backButton;
    public Button editButton;
    @FXML private TableView<Article> likedArticlesTable;
    @FXML private TableView<Article> savedArticlesTable;

    @FXML private TableColumn<Article, String> likedTitleColumn;
    @FXML private TableColumn<Article, String> likedCategoryColumn;
    @FXML private TableColumn<Article, String> likedDateColumn;

    @FXML private TableColumn<Article, String> savedTitleColumn;
    @FXML private TableColumn<Article, String> savedCategoryColumn;
    @FXML private TableColumn<Article, String> savedDateColumn;


    @FXML
    public void initialize() {
        // Initialize columns for liked articles
        likedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("headline"));
        likedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        likedDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Initialize columns for saved articles
        savedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("headline"));
        savedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        savedDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Ensure the database is connected and load the user's articles
        DB.connect();
        loadUserArticles();
    }

    private void loadUserArticles() {
    var userLikesCollection = DB.getDatabase().getCollection("user_likes");
    var articlesCollection = DB.getDatabase().getCollection("articles");

    if (!Session.isLoggedIn()) {
        System.err.println("No user is logged in. Cannot load user articles.");
        return;
    }

    User currentUser = Session.getCurrentUser();
    ObjectId userId = currentUser.getUserId();

    // Fetch the user's document from the `userlikes` collection
    Document userDocument = userLikesCollection.find(new Document("user_id", userId)).first();

    if (userDocument == null) {
        System.err.println("User not found in userlikes collection.");
        return;
    }

    // Fetch liked articles
    List<ObjectId> likedArticleIds = userDocument.getList("liked_articles", ObjectId.class);
    ObservableList<Article> likedArticles = FXCollections.observableArrayList();

    if (likedArticleIds != null) {
        likedArticleIds.forEach(articleId -> {
            Document articleDoc = articlesCollection.find(new Document("_id", articleId)).first();
            if (articleDoc != null) {
                likedArticles.add(Article.fromDocument(articleDoc));
            }
        });
    }
    likedArticlesTable.setItems(likedArticles);

    // Fetch saved articles
    List<ObjectId> savedArticleIds = userDocument.getList("saved_articles", ObjectId.class);
    ObservableList<Article> savedArticles = FXCollections.observableArrayList();

    if (savedArticleIds != null) {
        savedArticleIds.forEach(articleId -> {
            Document articleDoc = articlesCollection.find(new Document("_id", articleId)).first();
            if (articleDoc != null) {
                savedArticles.add(Article.fromDocument(articleDoc));
            }
        });
    }
    savedArticlesTable.setItems(savedArticles);
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




    public void handleEditButton() {
        Stage currentStage = (Stage) editButton.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/editProfile.fxml", null);
    }
    @Override
    public void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlFile));
            javafx.scene.Parent root = loader.load();

            currentStage.setScene(new javafx.scene.Scene(root));

            if (title != null) {
                currentStage.setTitle(title);
            }

            // Remove window decorations if required
            if (noDecoration) {
                currentStage.initStyle(javafx.stage.StageStyle.UTILITY); // Removes title bar, close, minimize buttons
                currentStage.setOpacity(1.0); // Ensures the window is fully opaque
            }

            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
