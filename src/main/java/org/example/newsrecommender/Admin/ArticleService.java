package org.example.newsrecommender.Admin;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.newsrecommender.articles.Article;

public class ArticleService {
    private MongoDatabase database;

    public ArticleService(MongoDatabase database) {
        this.database = database;
    }

    // Method to add article to the database
    public void addArticle(Article article) {
        MongoCollection<Document> collection = database.getCollection("articles");

        // Create a Document from the Article object
        Document articleDoc = new Document("link", article.getLink())
                .append("headline", article.getHeadline())
                .append("category", article.getCategory())
                .append("author", article.getAuthor())
                .append("date", article.getDate());

        // Insert the article into the "articles" collection
        collection.insertOne(articleDoc);
    }
}
