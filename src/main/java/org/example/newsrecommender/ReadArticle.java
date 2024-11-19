package org.example.newsrecommender;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.newsrecommender.db.DB;
import org.example.newsrecommender.user.UserLikes;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReadArticle implements Initializable {
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextArea articleTextArea;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button backButton;

    private ObservableList<String> categories;
    private List<Document> articles = new ArrayList<>();
    private int currentArticleIndex = 0;
    private MongoDatabase database;
    private UserLikes userLikes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        database = DB.getDatabase();
        userLikes = new UserLikes(database);
        fetchCategories();
    }

    private void fetchCategories() {
        MongoCollection<Document> articlesCollection = database.getCollection("articles");
        List<String> categoryList = new ArrayList<>();
        articlesCollection.distinct("category", String.class).forEach(categoryList::add);
        categories = FXCollections.observableArrayList(categoryList);
        categoryComboBox.setItems(categories);
    }

    @FXML
    public void handleCategorySelection(ActionEvent event) {
        String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            fetchArticlesByCategory(selectedCategory);
        }
    }

    private void fetchArticlesByCategory(String selectedCategory) {
        MongoCollection<Document> articlesCollection = database.getCollection("articles");
        articles = articlesCollection.find(new Document("category", selectedCategory)).into(new ArrayList<>());
        currentArticleIndex = 0;
        if (!articles.isEmpty()) {
            loadArticleContent(articles.get(currentArticleIndex));
        } else {
            showAlert("No Articles", "No articles found in this category.");
        }
    }

    private void loadArticleContent(Document article) {
        String articleLink = article.getString("link");
        String articleContent = fetchContentFromUrl(articleLink);
        articleTextArea.setText(articleContent);
    }

    private String fetchContentFromUrl(String url) {
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
            StringBuilder articleContent = new StringBuilder();
            doc.select("p").forEach(paragraph -> articleContent.append(paragraph.text()).append("\n"));
            return articleContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load article from the link.");
            return "";
        }
    }

    @FXML
    public void handleNextButton(ActionEvent event) {
        if (currentArticleIndex < articles.size() - 1) {
            currentArticleIndex++;
            loadArticleContent(articles.get(currentArticleIndex));
        }
    }

    @FXML
    public void handlePreviousButton(ActionEvent event) {
        if (currentArticleIndex > 0) {
            currentArticleIndex--;
            loadArticleContent(articles.get(currentArticleIndex));
        }
    }

    @FXML
    public void handleLikeButton(ActionEvent event) {
        if (!articles.isEmpty()) {
            Document currentArticle = articles.get(currentArticleIndex);
            String articleCategory = currentArticle.getString("category");

            // Record the like event and get the result
            boolean isLiked = userLikes.recordLike(articleCategory);

            // Show alert based on whether the category was successfully added or already present
            if (isLiked) {
                showAlert("Liked", "Category added to your likes!");
            } else {
                showAlert("Already Liked", "This category is already in your likes.");
            }
        } else {
            showAlert("Error", "No article loaded to like.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow(); // Get current stage from user_button
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Application.fxml")));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

