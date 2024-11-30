package org.example.newsrecommender.recommendation;

import org.example.newsrecommender.user.UserPreferences;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CategoryRecommendationSystem {
    private final Map<Integer, UserPreferences> userPreferences;

    public CategoryRecommendationSystem(Map<Integer, UserPreferences> userPreferences) {
        this.userPreferences = userPreferences;
    }

    public Map<Integer, UserPreferences> getAllUserPreferences() {
        return userPreferences;
    }

    public List<String> recommendCategoriesForUser(int userId, int numRecommendations) {
        UserPreferences preferences = userPreferences.get(userId);
        if (preferences == null || preferences.getCategoryPoints() == null) return Collections.emptyList();

        Map<String, Integer> categoryPoints = preferences.getCategoryPoints();

        // Print the points for the current logged-in user
        System.out.println("Category Points for User " + userId + ": " + categoryPoints);

        Map<String, Map<String, Double>> categorySimilarities = computeCategorySimilarities();

        Map<String, Double> aggregatedScores = new HashMap<>();
        for (Map.Entry<String, Integer> entry : categoryPoints.entrySet()) {
            String category = entry.getKey();
            int points = entry.getValue();
            aggregatedScores.put(category, aggregatedScores.getOrDefault(category, 0.0) + points);

            Map<String, Double> similarCategories = categorySimilarities.get(category);
            if (similarCategories != null) {
                for (Map.Entry<String, Double> similarCategory : similarCategories.entrySet()) {
                    String similarCategoryId = similarCategory.getKey();
                    double similarity = similarCategory.getValue();
                    aggregatedScores.put(similarCategoryId,
                            aggregatedScores.getOrDefault(similarCategoryId, 0.0) + points * similarity);
                }
            }
        }

        return aggregatedScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .toList();
    }

    private Map<String, Map<String, Double>> computeCategorySimilarities() {
        Map<String, Map<String, Double>> similarities = new HashMap<>();

        Set<String> allCategories = new HashSet<>();
        for (UserPreferences preferences : userPreferences.values()) {
            Map<String, Integer> categoryPoints = preferences.getCategoryPoints();
            if (categoryPoints != null) {
                allCategories.addAll(categoryPoints.keySet());
            }
        }

        for (String categoryA : allCategories) {
            for (String categoryB : allCategories) {
                if (!categoryA.equals(categoryB)) {
                    double similarity = computeCosineSimilarity(categoryA, categoryB);
                    similarities.computeIfAbsent(categoryA, k -> new HashMap<>()).put(categoryB, similarity);
                }
            }
        }

        return similarities;
    }

    private double computeCosineSimilarity(String categoryA, String categoryB) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;

        for (UserPreferences preferences : userPreferences.values()) {
            Map<String, Integer> categoryPoints = preferences.getCategoryPoints();
            if (categoryPoints != null) {
                double pointsA = categoryPoints.getOrDefault(categoryA, 0);
                double pointsB = categoryPoints.getOrDefault(categoryB, 0);

                dotProduct += pointsA * pointsB;
                normA += Math.pow(pointsA, 2);
                normB += Math.pow(pointsB, 2);
            }
        }

        return normA == 0 || normB == 0 ? 0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static void main(String[] args) {
        // Mock user data
        Map<Integer, UserPreferences> userPreferences = new HashMap<>();
        userPreferences.put(1, new UserPreferences(Map.of("Tech", 10, "Health", 5, "Sports", 2, "Crime", 5), null, null, null));
        userPreferences.put(2, new UserPreferences(Map.of("Tech", 8, "Business", 6, "Health", 4, "Parenting", 8), null, null, null));
        userPreferences.put(3, new UserPreferences(Map.of("Sports", 9, "Tech", 5, "Business", 7), null, null, null));
        userPreferences.put(4, new UserPreferences(Map.of("Sports", 9, "Tech", 5, "Business", 7), null, null, null));

        CategoryRecommendationSystem system = new CategoryRecommendationSystem(userPreferences);

        // Simulate concurrent requests
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        List<Callable<List<String>>> tasks = List.of(
                () -> system.recommendCategoriesForUser(1, 3),
                () -> system.recommendCategoriesForUser(2, 2),
                () -> system.recommendCategoriesForUser(3, 3),
                () -> system.recommendCategoriesForUser(4, 1)
        );

        try {
            List<Future<List<String>>> futures = executorService.invokeAll(tasks);

            for (int i = 0; i < futures.size(); i++) {
                System.out.println("Recommended Categories for User " + (i + 1) + ": " + futures.get(i).get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
