package org.example.newsrecommender.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.user.UserPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.in;

public class DBservice {

    private final MongoDatabase database;
    private final MongoCollection<Document> userLikesCollection;
    private final MongoCollection<Document> articleCollection;

    public DBservice(MongoDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("Database cannot be null");
        }
        this.database = database;
        this.userLikesCollection = database.getCollection("user_likes");
        this.articleCollection = database.getCollection("articles");
    }

    // Method to retrieve a single user's preferences from the database (Asynchronously)
    public CompletableFuture<UserPreferences> getUserPreferencesAsync(ObjectId userId) {
        return CompletableFuture.supplyAsync(() -> {
            Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();
            if (userDoc != null) {
                Map<String, Integer> categoryPoints = new HashMap<>();
                Document categoryPointsDoc = userDoc.get("category_points", Document.class);

                // If category points exist, populate the categoryPoints map
                if (categoryPointsDoc != null) {
                    for (String key : categoryPointsDoc.keySet()) {
                        categoryPoints.put(key, categoryPointsDoc.getInteger(key, 0));
                    }
                }

                // Return UserPreferences with only category points
                return new UserPreferences(categoryPoints, null, null, null); // Other fields are null
            }
            return null; // No preferences found
        });
    }

    // Method to retrieve articles by category from the database (Limited by number of articles)
    public List<Article> getArticlesByCategory(String category, int limit) {
        List<Article> articles = new ArrayList<>();
        // Query the database to find articles by category and limit the number of results
        List<Document> articleDocs = articleCollection.find(new Document("category", category))
                                                      .limit(limit)  // Limit the number of articles
                                                      .into(new ArrayList<>());

        // Convert the MongoDB documents to Article objects using the fromDocument method
        for (Document doc : articleDocs) {
            articles.add(Article.fromDocument(doc));  // Map each document to an Article object
        }

        return articles;  // Return the list of articles
    }

    // Fetch liked or saved article IDs from the userlikes collection
    public List<ObjectId> getArticleIds(ObjectId userId, String field) {
        MongoCollection<org.bson.Document> userLikesCollection = database.getCollection("user_likes");
        org.bson.Document user = userLikesCollection.find(new org.bson.Document("user_id", userId)).first();

        if (user != null) {
            return user.getList(field, ObjectId.class);
        }
        return new ArrayList<>();
    }

    // Fetch article details by a list of IDs from the articles collection
    public List<Article> getArticlesByIds(List<ObjectId> articleIds) {
        MongoCollection<org.bson.Document> articlesCollection = database.getCollection("articles");
        List<Article> articles = new ArrayList<>();

        for (org.bson.Document doc : articlesCollection.find(in("_id", articleIds))) {
            articles.add(new Article(
                doc.getObjectId("_id"),
                doc.getString("link"),
                doc.getString("headline"),
                doc.getString("category"),
                null, // Author field is missing in example data; replace with actual field if available
                doc.getString("date")
            ));
        }
        return articles;
    }




}
