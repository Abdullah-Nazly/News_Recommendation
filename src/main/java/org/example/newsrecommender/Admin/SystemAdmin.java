package org.example.newsrecommender.Admin;

import javafx.scene.control.TextField;
import org.example.newsrecommender.articles.Article;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class SystemAdmin {
    public TextField link;
    public TextField headline;
    public TextField category;
    public TextField author;
    public TextField date;

    private MongoDatabase database;

    private ArticleService articleService;

    // No-argument constructor required by JavaFX
    public SystemAdmin() {
    }

    // Constructor that initializes ArticleService
    public SystemAdmin(MongoDatabase database) {
        this.articleService = new ArticleService(database);
    }

    // Method to add article to the database
    public void addArticleToDatabase() {
        String linkText = link.getText();
        String headlineText = headline.getText();
        String categoryText = category.getText();
        String authorText = author.getText();
        String dateText = date.getText();

        // Create an Article object with the data from the text fields
        Article article = new Article(linkText, headlineText, categoryText, authorText, dateText);

        // Use the ArticleService to add the article to the database
        articleService.addArticle(article);
    }
}
