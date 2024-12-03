package org.example.newsrecommender.Admin;

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
import org.example.newsrecommender.BaseController;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.db.DB;

import java.io.IOException;
import java.util.Objects;

public class DeleteArticle implements BaseController {

    public TableView<Article> articleTable;
    public TableColumn<Article, String> link;
    public TableColumn<Article, String> headline;
    public TableColumn<Article, String> category;
    public TableColumn<Article, String> date;
    public Button backButton;

    public void initialize() {
            // Bind the TableColumns to the Article properties
        link.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLink()));
        headline.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHeadline()));
        category.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        date.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));

        // Load articles from the database
        loadArticlesFromDatabase();

        // Handle row double-click
        articleTable.setOnMouseClicked(this::handleTableRowDoubleClick);
    }

    private void loadArticlesFromDatabase() {
        var database = DB.getDatabase();
        var articlesCollection = database.getCollection("articles");

        articleTable.getItems().clear();

        for (Document document : articlesCollection.find()) {
            articleTable.getItems().add(Article.fromDocument(document));
        }
    }

    private void handleTableRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
            if (selectedArticle != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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
        var database = DB.getDatabase();
        var articlesCollection = database.getCollection("articles");
        articlesCollection.deleteOne(new Document("_id", article.getId()));

        loadArticlesFromDatabase();

        showAlert(Alert.AlertType.INFORMATION, "Article Deleted", "The article has been successfully deleted.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackButton() {
        navigateToView((Stage) backButton.getScene().getWindow(), "/org/example/newsrecommender/SystemAdmin.fxml");
    }

    @Override
    public void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxmlFile));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            if (noDecoration) {
                currentStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            }

            if (title != null) {
                currentStage.setTitle(title);
            }
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
