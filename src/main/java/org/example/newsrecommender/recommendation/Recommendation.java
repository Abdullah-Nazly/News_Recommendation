package org.example.newsrecommender.recommendation;

import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.newsrecommender.Session;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.articles.ArticleLoader;
import org.example.newsrecommender.db.DB;
import org.example.newsrecommender.db.DBservice;
import org.example.newsrecommender.recommendation.RecommendationManager;

import java.io.IOException;

public class Recommendation {

    public TableColumn titleColumn;
    public TableColumn categoryColumn;
    @FXML
    private TableView<Article> article_table;
    @FXML
    private TextArea content_area;
    @FXML
    private Button backButton;


    private RecommendationManager recommendationManager;

    public Recommendation(){
    }

    public Recommendation(RecommendationManager recommendationManager) {
        this.recommendationManager = recommendationManager;
    }

    public void initialize() {
        // Get the MongoDatabase instance from your DB class
        MongoDatabase mongoDatabase = DB.getDatabase();
        // Instantiate the DBservice with the MongoDatabase instance
        DBservice dbService = new DBservice(mongoDatabase);

        // Instantiate the ArticleFetcher with the DBservice
        ArticleFetcher articleFetcher = new ArticleFetcher(dbService);  // Pass DBservice to ArticleFetcher

         // Instantiate UserPreferencesService with the required DBservice parameter
        UserPreferencesService preferencesService = new UserPreferencesService(dbService);  // Pass dbService
        recommendationManager = new RecommendationManager(preferencesService, articleFetcher);  // Now instantiate RecommendationManager

        // Bind existing columns to Article properties
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("headline"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        article_table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click detected
                Article selectedArticle = article_table.getSelectionModel().getSelectedItem();
                if (selectedArticle != null) {
                    displayArticleContent(selectedArticle);
                }
            }
        });

        // Fetch and recommend articles after initialization
        recommendationManager.recommendArticles(Session.getCurrentUser().getUserId());
        article_table.setItems(recommendationManager.getArticleList());
    }
    private void displayArticleContent(Article article) {
        String url = article.getLink();
        if (url != null && !url.isEmpty()) {
            content_area.setText(new ArticleLoader(DB.getDatabase()).loadArticleContentFromUrl(url));
        } else {
            content_area.setText("No URL available for this article.");
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/newsrecommender/Application.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}