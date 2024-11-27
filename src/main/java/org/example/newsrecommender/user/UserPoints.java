package org.example.newsrecommender.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.newsrecommender.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPoints {

    private final MongoDatabase database;

    public UserPoints(MongoDatabase database) {
        this.database = database;
    }

    // Method to add a like to a specific category for the current user if not already liked
    public void addCategoryLike(ObjectId userId, String category) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            List<String> likedCategories = userDoc.getList("liked_categories", String.class);
            List<String> dislikedCategories = userDoc.getList("disliked_categories", String.class);

            if (likedCategories == null) likedCategories = new ArrayList<>();
            if (dislikedCategories != null && dislikedCategories.contains(category)) {
                dislikedCategories.remove(category); // Remove from disliked if it exists
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("disliked_categories", dislikedCategories))
                );
            }

            if (!likedCategories.contains(category)) {
                likedCategories.add(category);
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("liked_categories", likedCategories))
                );
                updateCategoryPoints(userId, category, 2);
            }
        } else {
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername())
                    .append("liked_categories", List.of(category))
                    .append("category_points", new Document(category, 2));
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    public void addClickPoints(ObjectId userId, String category) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            Document categoryPoints = userDoc.get("category_points", Document.class);

            if (categoryPoints == null || !categoryPoints.containsKey(category)) {
                updateCategoryPoints(userId, category, 1);
            }
        } else {
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername())
                    .append("category_points", new Document(category, 1));
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    public void saveCategory(ObjectId userId, String category) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            List<String> savedCategories = userDoc.getList("saved_categories", String.class);
            if (savedCategories == null) savedCategories = new ArrayList<>();

            if (!savedCategories.contains(category)) {
                savedCategories.add(category);
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("saved_categories", savedCategories))
                );
                updateCategoryPoints(userId, category, 3);
            }
        } else {
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername())
                    .append("saved_categories", List.of(category))
                    .append("category_points", new Document(category, 3));
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    public void dislikeCategory(ObjectId userId, String category) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            List<String> dislikedCategories = userDoc.getList("disliked_categories", String.class);
            List<String> likedCategories = userDoc.getList("liked_categories", String.class);

            if (dislikedCategories == null) dislikedCategories = new ArrayList<>();
            if (likedCategories != null && likedCategories.contains(category)) {
                likedCategories.remove(category); // Remove from liked if it exists
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("liked_categories", likedCategories))
                );
            }

            if (!dislikedCategories.contains(category)) {
                dislikedCategories.add(category);
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("disliked_categories", dislikedCategories))
                );
                updateCategoryPoints(userId, category, -1);
            }
        } else {
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername())
                    .append("disliked_categories", List.of(category))
                    .append("category_points", new Document(category, -1));
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    private void updateCategoryPoints(ObjectId userId, String category, int pointsToAdd) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            Document categoryPoints = userDoc.get("category_points", Document.class);
            if (categoryPoints == null) {
                categoryPoints = new Document();
            }

            int currentPoints = categoryPoints.getInteger(category, 0);
            categoryPoints.put(category, currentPoints + pointsToAdd);

            userLikesCollection.updateOne(
                    new Document("user_id", userId),
                    new Document("$set", new Document("category_points", categoryPoints))
            );
        } else {
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername())
                    .append("category_points", new Document(category, pointsToAdd));
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    public UserPreferences getUserPreferences(ObjectId userId) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            // Convert the "category_points" field to a Map<String, Integer>
            Document categoryPointsDoc = userDoc.get("category_points", Document.class);
            Map<String, Integer> categoryPoints = new HashMap<>();

            if (categoryPointsDoc != null) {
                for (String key : categoryPointsDoc.keySet()) {
                    categoryPoints.put(key, categoryPointsDoc.getInteger(key, 0));
                }
            }
            List<String> likedCategories = userDoc.getList("liked_categories", String.class);
            List<String> dislikedCategories = userDoc.getList("disliked_categories", String.class);
            List<String> savedCategories = userDoc.getList("saved_categories", String.class);

            return new UserPreferences(categoryPoints, likedCategories, dislikedCategories, savedCategories);
        }
        return new UserPreferences(); // Return empty preferences if userDoc is not found
    }
}
