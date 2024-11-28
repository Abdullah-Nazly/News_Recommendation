package org.example.newsrecommender.recommendation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bson.types.ObjectId;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.user.UserPoints;

import java.util.List;

public class Recommendation {
    public TableView<Article> article_table;
    public TextArea content_area;
    private final RecommendationSystem recommendationSystem;
    private final ObjectId userId; // Assuming you have a userId to fetch recommendations

    public Recommendation(UserPoints userPoints, ObjectId userId) {
        this.recommendationSystem = new RecommendationSystem(userPoints);
        this.userId = userId;
    }

    public void initialize() {
        setupTableColumns();
        displayRecommendations();
    }

    private void setupTableColumns() {
        TableColumn<Article, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Article, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Article, Double> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        article_table.getColumns().addAll(titleColumn, categoryColumn, scoreColumn);
    }

    public void displayRecommendations() {
        // Get recommended articles for the user
        List<Article> articles = recommendationSystem.generateRecommendations(userId, 5); // 5 articles for now

        // Print articles to console (debugging)
        for (Article article : articles) {
            System.out.println("Recommended Article: " + article.getHeadline());
        }

        // Convert articles to ObservableList and display in the table
        ObservableList<Article> observableArticles = FXCollections.observableArrayList(articles);
        article_table.setItems(observableArticles);
    }
}
