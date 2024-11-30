package org.example.newsrecommender.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.newsrecommender.user.UserPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DBservice {

    private static MongoDatabase database;
    private static final MongoCollection<Document> userLikesCollection = DB.getDatabase().getCollection("user_likes");

    public DBservice(MongoDatabase database) {
        this.database = database;
    }

    // Method to retrieve all user preferences from the database (Asynchronously)
    public static CompletableFuture<List<UserPreferences>> getAllUserPreferencesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<UserPreferences> userPreferencesList = new ArrayList<>();

            for (Document userDoc : userLikesCollection.find()) {
                // Extract category points, liked, disliked, and saved categories
                Map<String, Integer> categoryPoints = new HashMap<>();
                Document categoryPointsDoc = userDoc.get("category_points", Document.class);
                if (categoryPointsDoc != null) {
                    for (String key : categoryPointsDoc.keySet()) {
                        categoryPoints.put(key, categoryPointsDoc.getInteger(key, 0));
                    }
                }

                List<String> likedCategories = userDoc.getList("liked_categories", String.class);
                List<String> dislikedCategories = userDoc.getList("disliked_categories", String.class);
                List<String> savedCategories = userDoc.getList("saved_categories", String.class);

                // Create UserPreferences object and add it to the list
                UserPreferences userPreferences = new UserPreferences(categoryPoints, likedCategories, dislikedCategories, savedCategories);
                userPreferencesList.add(userPreferences);
            }

            return userPreferencesList;
        });
    }

    // Method to retrieve a single user's preferences from the database (Asynchronously)
    public static CompletableFuture<UserPreferences> getUserPreferencesAsync(ObjectId userId) {
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
}
