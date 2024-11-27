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

    // No-argument constructor required by JavaFX
    public SystemAdmin() {
    }
    public SystemAdmin(MongoDatabase database) {
        this.database = database;
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

        // Create a Document to insert into MongoDB
        Document articleDoc = new Document("link", article.getLink())
                .append("headline", article.getHeadline())
                .append("category", article.getCategory())
                .append("author", article.getAuthor())
                .append("date", article.getDate());

        // Insert the article into the "articles" collection
        MongoCollection<Document> collection = database.getCollection("articles");
        collection.insertOne(articleDoc);
    }
}
