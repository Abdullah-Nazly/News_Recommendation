package org.example.newsrecommender.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.newsrecommender.Session;

import java.util.ArrayList;
import java.util.List;

public class UserLikes {

    private MongoDatabase database;

    // Constructor to initialize the MongoDatabase instance
    public UserLikes(MongoDatabase database) {
        this.database = database;
    }

    // Method to add a like to a specific category for the current user if not already liked
    public void addCategoryLike(ObjectId userId, String category) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");

        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            List<String> likedCategories = userDoc.getList("liked_categories", String.class);

            if (likedCategories == null) {
                likedCategories = new ArrayList<>();
            }

            if (!likedCategories.contains(category)) {
                likedCategories.add(category);
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("liked_categories", likedCategories))
                );
                updateCategoryPoints(userId, category, 2); // 2 points for a like
            }
        } else {
            // Create new user document if it doesn't exist
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername())
                    .append("liked_categories", List.of(category))
                    .append("category_points", new Document(category, 2));
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    // Add or update points when user clicks on a category
    // Add click points to a category only if it hasn’t been clicked before
    public void addClickPoints(ObjectId userId, String category) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");

        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            Document categoryPoints = userDoc.get("category_points", Document.class);

            if (categoryPoints == null || !categoryPoints.containsKey(category)) {
                updateCategoryPoints(userId, category, 1);  // 1 point for a click
            }
        } else {
            // If no user document, create one with 1 click point for the category
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername())
                    .append("category_points", new Document(category, 1));
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    // Add or update points when user likes a category
    public void addLikePoints(ObjectId userId, String category) {
        updateCategoryPoints(userId, category, 2);  // 2 points for a like
    }


    // General method to update points in `user_likes` collection
    private void updateCategoryPoints(ObjectId userId, String category, int pointsToAdd) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");

        // Find the user document
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            // Retrieve or initialize category points
            Document categoryPoints = userDoc.get("category_points", Document.class);
            if (categoryPoints == null) {
                categoryPoints = new Document();
            }

            // Get current points for the category or start from 0
            int currentPoints = categoryPoints.getInteger(category, 0);
            categoryPoints.put(category, currentPoints + pointsToAdd);

            // Update points in the user document
            userLikesCollection.updateOne(
                    new Document("user_id", userId),
                    new Document("$set", new Document("category_points", categoryPoints))
            );
        } else {
            // Create a new user document if it doesn't exist
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername())
                    .append("category_points", new Document(category, pointsToAdd));

            userLikesCollection.insertOne(newUserDoc);
        }
    }

}
