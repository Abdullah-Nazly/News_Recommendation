package org.example.newsrecommender.articles;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.newsrecommender.db.DB;

import java.io.IOException;
import java.util.Objects;

public class DeleteArticle {

    public TableView<Article> articleTable;
    public TableColumn<Article, String> link;
    public TableColumn<Article, String> headline;
    public TableColumn<Article, String> category;
    public TableColumn<Article, String> date;
    public Button backButton;

    public void initialize() {
        // Fetch articles from the database and display them in the table
        loadArticlesFromDatabase();

        // Add event listener for double-click on an article row
        articleTable.setOnMouseClicked(this::handleTableRowDoubleClick);
    }

    private void loadArticlesFromDatabase() {
        var database = DB.getDatabase();
        var articlesCollection = database.getCollection("articles");

        // Clear current table data
        articleTable.getItems().clear();

        // Get all articles from the database
        var documents = articlesCollection.find();

        // Convert documents to Article objects and add to the table
        for (Document document : documents) {
            Article article = Article.fromDocument(document); // Convert Document to Article
            articleTable.getItems().add(article);
        }
    }

    private void handleTableRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click event
            Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();

            if (selectedArticle != null) {
                // Show confirmation alert
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Delete Article");
                alert.setHeaderText("Are you sure you want to delete this article?");
                alert.setContentText(selectedArticle.getHeadline());

                alert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        deleteArticle(selectedArticle);
                    }
                });
            }
        }
    }

    private void deleteArticle(Article article) {
        // Get database and articles collection
        var database = DB.getDatabase();
        var articlesCollection = database.getCollection("articles");

        // Delete the article from the database using its ObjectId
        articlesCollection.deleteOne(new Document("_id", article.getId()));

        // Refresh the TableView after deletion
        loadArticlesFromDatabase();

        // Show success alert
        showAlert(AlertType.INFORMATION, "Article Deleted", "The article has been successfully deleted.");
    }

    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/newsrecommender/SystemAdmin.fxml")));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
