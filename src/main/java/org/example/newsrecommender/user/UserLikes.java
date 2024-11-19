package org.example.newsrecommender.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class UserLikes {
    private MongoDatabase database;

    public UserLikes(MongoDatabase database) {
        this.database = database;
    }

    // Method to fetch the user_id and username of the last logged-in user
    public Map<String, String> getLastLoggedInUserInfo() {
        MongoCollection<Document> userLoginCollection = database.getCollection("user_logins");
        Document lastLogin = userLoginCollection.find().sort(new Document("login_time", -1)).first();

        if (lastLogin != null) {
            Map<String, String> userInfo = new HashMap<>();
            Object userIdObj = lastLogin.get("user_id");

            // Check if the user_id is an ObjectId, then convert it to a String
            if (userIdObj instanceof ObjectId) {
                userInfo.put("user_id", userIdObj.toString());
            } else if (userIdObj instanceof String) {
                userInfo.put("user_id", (String) userIdObj);
            }

            // Retrieve the username and add it to the map
            String username = lastLogin.getString("user_name");
            if (username != null) {
                userInfo.put("user_name", username);
            }

            return userInfo;  // Return both user_id and username
        }

        return null;  // No last login found
    }

    public boolean recordLike(String category) {
        Map<String, String> userInfo = getLastLoggedInUserInfo();

        if (userInfo == null || !userInfo.containsKey("user_id")) {
            System.out.println("No user is currently logged in.");
            return false;
        }

        String userId = userInfo.get("user_id");
        String username = userInfo.get("user_name");

        MongoCollection<Document> likesCollection = database.getCollection("user_likes");

        // Check if there’s already a document for this user
        Document existingUserLikes = likesCollection.find(eq("user_id", userId)).first();

        if (existingUserLikes != null) {
            // Retrieve the liked categories list, or set to an empty list if it's null
            List<String> likedCategories = existingUserLikes.getList("liked_categories", String.class);
            if (likedCategories == null) {
                likedCategories = Collections.emptyList();
            }

            // Add the category if it's not already in the list
            if (!likedCategories.contains(category)) {
                likesCollection.updateOne(
                        eq("user_id", userId),
                        Updates.addToSet("liked_categories", category)
                );
                System.out.println("Category added to existing user likes.");
            } else {
                System.out.println("Category already liked by user.");
            }
        } else {
            // Create a new document for the user if none exists
            Document newUserLikes = new Document("user_id", userId)
                    .append("user_name", username)
                    .append("liked_categories", List.of(category));

            likesCollection.insertOne(newUserLikes);
            System.out.println("User likes record created and category added.");
        }

        return true;
    }
}
 
