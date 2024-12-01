package org.example.newsrecommender.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.db.DBservice;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Profile {

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

    private DBservice dbService; // Service to interact with the database
    private ObjectId currentUserId; // ID of the current user (to be passed or initialized)

    public Profile(){
        // Default constructor to initialize the fxml file
    }
    public Profile(DBservice dbService, ObjectId currentUserId) {
        this.dbService = dbService;
        this.currentUserId = currentUserId;
    }

    // This method will be called to initialize the profile screen
    @FXML
    public void initialize() {
        if (dbService == null || currentUserId == null) {
        System.err.println("DBservice or currentUserId is not set. Ensure they are initialized before calling initialize().");
        return;
    }
        // Initialize the columns for liked articles
        likedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("headline"));
        likedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        likedDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Initialize the columns for saved articles
        savedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("headline"));
        savedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        savedDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Load the articles (you can replace this with actual database or API calls)
        loadLikedArticles(currentUserId);
        loadSavedArticles(currentUserId);
    }

     // Load liked articles from the database
    private void loadLikedArticles(ObjectId currentUserId) {
        try {
            List<ObjectId> likedArticleIds = dbService.getArticleIds(currentUserId, "liked_articles");
            List<Article> likedArticles = dbService.getArticlesByIds(likedArticleIds);

            ObservableList<Article> likedArticlesList = FXCollections.observableArrayList(likedArticles);
            likedArticlesTable.setItems(likedArticlesList);
        } catch (Exception e) {
            System.err.println("Error loading liked articles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load saved articles from the database
    private void loadSavedArticles(ObjectId currentUserId) {
        try {
            List<ObjectId> savedArticleIds = dbService.getArticleIds(currentUserId, "saved_articles");
            List<Article> savedArticles = dbService.getArticlesByIds(savedArticleIds);

            ObservableList<Article> savedArticlesList = FXCollections.observableArrayList(savedArticles);
            savedArticlesTable.setItems(savedArticlesList);
        } catch (Exception e) {
            System.err.println("Error loading saved articles: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void setDBservice(DBservice dbService) {
        this.dbService = dbService;
    }

    public void setCurrentUserId(ObjectId currentUserId) {
        this.currentUserId = currentUserId;
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
        try {
            Stage stage = (Stage) editButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/newsrecommender/editProfile.fxml")));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }}
}
