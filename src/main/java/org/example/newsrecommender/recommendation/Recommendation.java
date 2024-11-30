package org.example.newsrecommender.recommendation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.user.UserPreferences;

import java.util.List;
import java.util.Map;

public class Recommendation {

    public TableView<Article> article_table;
    public TextArea content_area;

    private final CategoryRecommendationSystem recommendationSystem;

    public Recommendation() {
        // Mock data for demonstration (replace with actual user data fetching)
        Map<Integer, UserPreferences> mockPreferences = Map.of(
                1, new UserPreferences(Map.of("Tech", 10, "Health", 5, "Sports", 2, "Crime", 5), null, null, null),
                2, new UserPreferences(Map.of("Tech", 8, "Business", 6, "Health", 4, "Parenting", 8), null, null, null),
                3, new UserPreferences(Map.of("Sports", 9, "Tech", 5, "Business", 7), null, null, null),
                4, new UserPreferences(Map.of("Sports", 9, "Tech", 5, "Business", 7), null, null, null)
        );

        this.recommendationSystem = new CategoryRecommendationSystem(mockPreferences);
    }

    public void initialize() {
        setupTableColumns();
        printAllUserPreferences(); // Print all user points on initialization
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

    private void printAllUserPreferences() {
        // Fetch all user preferences from the recommendation system
        Map<Integer, UserPreferences> allPreferences = recommendationSystem.getAllUserPreferences();

        System.out.println("Displaying all user preferences and points:");
        for (Map.Entry<Integer, UserPreferences> entry : allPreferences.entrySet()) {
            int userId = entry.getKey();
            UserPreferences preferences = entry.getValue();
            Map<String, Integer> categoryPoints = preferences.getCategoryPoints();
            System.out.println("User ID: " + userId + ", Category Points: " + categoryPoints);
        }
    }
}
