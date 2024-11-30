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
    public void addArticleLike(ObjectId userId, ObjectId articleId) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            // Get the lists of liked and disliked articles
            List<ObjectId> likedArticles = userDoc.getList("liked_articles", ObjectId.class);
            if (likedArticles == null) {
                likedArticles = new ArrayList<>();
            }

            List<ObjectId> dislikedArticles = userDoc.getList("disliked_articles", ObjectId.class);
            if (dislikedArticles == null) {
                dislikedArticles = new ArrayList<>();
            }

            // Check if the article is already liked
            if (!likedArticles.contains(articleId)) {
                // If the article is disliked, remove it from disliked list before adding to liked list
                dislikedArticles.remove(articleId);

                // Add the article to the liked articles list
                likedArticles.add(articleId);

                // Update the user document with the new liked and disliked articles lists
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("liked_articles", likedArticles)
                                .append("disliked_articles", dislikedArticles))
                );
            } else {
                // Log or show alert if the article is already liked
                System.out.println("Article already liked");
            }
        } else {
            // If the user does not exist, create a new document with liked articles
            List<ObjectId> likedArticles = new ArrayList<>();
            likedArticles.add(articleId);

            Document newUserDoc = new Document("user_id", userId)
                    .append("liked_articles", likedArticles)
                    .append("disliked_articles", new ArrayList<>());
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    public void saveArticle(ObjectId userId, ObjectId articleId) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            // Get the list of saved articles
            List<ObjectId> savedArticles = userDoc.getList("saved_articles", ObjectId.class);
            if (savedArticles == null) {
                savedArticles = new ArrayList<>();
            }

            // Check if the article is already saved
            if (!savedArticles.contains(articleId)) {
                // Add the article to the saved articles list
                savedArticles.add(articleId);

                // Update the user document with the new saved articles list
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("saved_articles", savedArticles))
                );
            } else {
                // Log or show alert if the article is already saved
                System.out.println("Article already saved");
            }
        } else {
            // If the user does not exist, create a new document with saved articles
            List<ObjectId> savedArticles = new ArrayList<>();
            savedArticles.add(articleId);

            Document newUserDoc = new Document("user_id", userId)
                    .append("saved_articles", savedArticles)
                    .append("liked_articles", new ArrayList<>())
                    .append("disliked_articles", new ArrayList<>());
            userLikesCollection.insertOne(newUserDoc);
        }
    }

    public void dislikeArticle(ObjectId userId, ObjectId articleId) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            // Get the lists of liked and disliked articles
            List<ObjectId> dislikedArticles = userDoc.getList("disliked_articles", ObjectId.class);
            if (dislikedArticles == null) {
                dislikedArticles = new ArrayList<>();
            }

            List<ObjectId> likedArticles = userDoc.getList("liked_articles", ObjectId.class);
            if (likedArticles == null) {
                likedArticles = new ArrayList<>();
            }

            // Check if the article is already disliked
            if (!dislikedArticles.contains(articleId)) {
                // If the article is liked, remove it from liked list before adding to disliked list
                likedArticles.remove(articleId);

                // Add the article to the disliked articles list
                dislikedArticles.add(articleId);

                // Update the user document with the new liked and disliked articles lists
                userLikesCollection.updateOne(
                        new Document("user_id", userId),
                        new Document("$set", new Document("disliked_articles", dislikedArticles)
                                .append("liked_articles", likedArticles))
                );
            } else {
                // Log or show alert if the article is already disliked
                System.out.println("Article already disliked");
            }
        } else {
            // If the user does not exist, create a new document with disliked articles
            List<ObjectId> dislikedArticles = new ArrayList<>();
            dislikedArticles.add(articleId);

            Document newUserDoc = new Document("user_id", userId)
                    .append("disliked_articles", dislikedArticles)
                    .append("liked_articles", new ArrayList<>());
            userLikesCollection.insertOne(newUserDoc);
        }
    }


    public void updateCategoryPoints(ObjectId userId, String category, int pointsToAdd) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            // Get existing category points or create a new document if null
            Document categoryPoints = userDoc.get("category_points", Document.class);
            if (categoryPoints == null) {
                categoryPoints = new Document();
            }

            // Update the points for the given category
            int currentPoints = categoryPoints.getInteger(category, 0);
            categoryPoints.put(category, currentPoints + pointsToAdd);

            // Create a new ordered document to enforce field order
            Document updatedDocument = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername()) // Ensure order
                    .append("category_points", categoryPoints);

            // Update the existing document
            userLikesCollection.updateOne(
                    new Document("user_id", userId),
                    new Document("$set", updatedDocument)
            );

            UserPreferences preferences = getUserPreferences(userId);
            preferences.printCategoryPoints(Session.getCurrentUser().getUsername());
        } else {
            // Insert a new document with user_name immediately after user_id
            Document newUserDoc = new Document("user_id", userId)
                    .append("user_name", Session.getCurrentUser().getUsername()) // Ensure order
                    .append("category_points", new Document(category, pointsToAdd));

            userLikesCollection.insertOne(newUserDoc);

            UserPreferences preferences = getUserPreferences(userId);
            preferences.printCategoryPoints(Session.getCurrentUser().getUsername());
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
    // Method to check if the article is already liked
    public boolean isArticleLiked(ObjectId userId, ObjectId articleId) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            List<ObjectId> likedArticles = userDoc.getList("liked_articles", ObjectId.class);
            return likedArticles != null && likedArticles.contains(articleId);
        }
        return false;
    }

    // Method to check if the article is already disliked
    public boolean isArticleDisliked(ObjectId userId, ObjectId articleId) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            List<ObjectId> dislikedArticles = userDoc.getList("disliked_articles", ObjectId.class);
            return dislikedArticles != null && dislikedArticles.contains(articleId);
        }
        return false;
    }

    // Method to check if the article is already saved
    public boolean isArticleSaved(ObjectId userId, ObjectId articleId) {
        MongoCollection<Document> userLikesCollection = database.getCollection("user_likes");
        Document userDoc = userLikesCollection.find(new Document("user_id", userId)).first();

        if (userDoc != null) {
            List<ObjectId> savedArticles = userDoc.getList("saved_articles", ObjectId.class);
            return savedArticles != null && savedArticles.contains(articleId);
        }
        return false;
    }

}
