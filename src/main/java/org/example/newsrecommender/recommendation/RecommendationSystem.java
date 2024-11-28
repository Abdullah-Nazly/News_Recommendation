package org.example.newsrecommender.recommendation;

import org.bson.types.ObjectId;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.user.UserPoints;
import org.example.newsrecommender.user.UserPreferences;

import java.util.*;

public class RecommendationSystem {
    private final UserPoints userPoints;

    public RecommendationSystem(UserPoints userPoints) {
        this.userPoints = userPoints;
    }

    public List<Article> generateRecommendations(ObjectId userId, int numRecommendations) {
        // Retrieve user preferences from the database (via UserPoints)
        UserPreferences preferences = userPoints.getUserPreferences(userId);

        if (preferences == null) {
            System.out.println("No preferences found for user ID: " + userId);
            return Collections.emptyList();
        }

        // Extract preferences (category points, liked, and disliked categories)
        Map<String, Integer> categoryPoints = preferences.getCategoryPoints();
        List<String> likedCategories = preferences.getLikedCategories();
        List<String> dislikedCategories = preferences.getDislikedCategories();

        if (categoryPoints == null || likedCategories == null) {
            System.out.println("Insufficient data for recommendations.");
            return Collections.emptyList();
        }

        // Generate recommendation scores based on category points and user preferences
        Map<String, Double> recommendationScores = new HashMap<>();

        for (String likedCategory : likedCategories) {
            categoryPoints.forEach((category, points) -> {
                if (!dislikedCategories.contains(category) && !likedCategory.equals(category)) {
                    recommendationScores.put(category,
                            recommendationScores.getOrDefault(category, 0.0) + points * 1.5); // Weighted scoring
                }
            });
        }

        // Sort categories by score
        List<String> recommendedCategories = recommendationScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .toList();

        // Now, find articles for the recommended categories
        List<Article> recommendedArticles = new ArrayList<>();
        for (String category : recommendedCategories) {
            // Retrieve articles for each category from your articles data source
            // For simplicity, assume we have a method getArticlesByCategory()
            List<Article> articlesForCategory = getArticlesByCategory(category);
            recommendedArticles.addAll(articlesForCategory);
        }

        return recommendedArticles;
    }

    private List<Article> getArticlesByCategory(String category) {
        // This is a placeholder for retrieving articles based on the category
        // Replace this with actual code to retrieve articles from your data source
        // For now, we'll return a dummy list

        return List.of(
//            new Article("Sample Article 1", category, 4.5),
//            new Article("Sample Article 2", category, 3.8)
        );
    }

}
