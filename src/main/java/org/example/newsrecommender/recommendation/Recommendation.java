package org.example.newsrecommender.recommendation;

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
import org.bson.types.ObjectId;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.db.DBservice;
import org.example.newsrecommender.user.UserPreferences;
import org.example.newsrecommender.Session;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.example.newsrecommender.db.DBservice.getUserPreferencesAsync;

public class Recommendation {

    public TableView<Article> article_table;
    public TextArea content_area;
    public Button backButton;

    private UserPreferences userPreferences;

    public void initialize() {
        setupTableColumns();
        printUserPreferences(); // Print user preferences during initialization
        recommendCategories(); // Recommend categories based on user preferences
    }

    public Recommendation() {
        // Default constructor required by JavaFX
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

    private void printUserPreferences() {
        try {
            // Fetch the current user's preferences using ObjectId
            ObjectId userId = Session.getCurrentUser().getUserId(); // Assumes userId is of type ObjectId
            String username = Session.getCurrentUser().getUsername(); // Fetch current user's username
            UserPreferences preferences = getUserPreferencesAsync(userId).get(); // Fetch preferences asynchronously

            // Print the current user's details
            System.out.println("Current User: " + username + " (ID: " + userId + ")");

            if (preferences != null) {
                // Print category points using the method in UserPreferences
                preferences.printCategoryPoints(username);
            } else {
                System.out.println("No preferences found for the current user.");
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching user preferences: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Recommend categories based on category points
    private void recommendCategories() {
        try {
            // Fetch the current user's preferences asynchronously
            ObjectId userId = Session.getCurrentUser().getUserId();

            // Fetch preferences in a non-blocking manner
            CompletableFuture<UserPreferences> preferencesFuture = getUserPreferencesAsync(userId);

            // Perform other UI-related tasks if necessary here...

            // Get the preferences once they are fetched
            UserPreferences preferences = preferencesFuture.get(); // Blocking here until data is available

            if (preferences != null) {
                // Get the category points and sort them by highest points
                Map<String, Integer> categoryPoints = preferences.getCategoryPoints();
                List<Map.Entry<String, Integer>> sortedCategories = categoryPoints.entrySet().stream()
                        .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                        .collect(Collectors.toList());

                // Print the recommended categories
                System.out.println("\nRecommended Categories for " + Session.getCurrentUser().getUsername() + ":");
                for (Map.Entry<String, Integer> entry : sortedCategories) {
                    System.out.println("Category: " + entry.getKey() + ", Points: " + entry.getValue());
                }

                // Optionally, update the UI with recommended categories
                ObservableList<String> recommendedCategories = FXCollections.observableArrayList();
                for (Map.Entry<String, Integer> entry : sortedCategories) {
                    recommendedCategories.add(entry.getKey());
                }

                // You could add the recommended categories to your UI, such as a list view or a table
                content_area.setText("Recommended Categories:\n");
                for (String category : recommendedCategories) {
                    content_area.appendText(category + "\n");
                }

            } else {
                System.out.println("No preferences found for the current user.");
            }
        } catch (Exception e) {
            System.err.println("Error recommending categories: " + e.getMessage());
            e.printStackTrace();
        }
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
