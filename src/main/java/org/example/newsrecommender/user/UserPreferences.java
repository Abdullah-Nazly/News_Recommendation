package org.example.newsrecommender.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserPreferences {

    private Map<String, Integer> categoryPoints;
    private List<String> likedCategories;
    private List<String> dislikedCategories;
    private List<String> savedCategories;

    public UserPreferences(Map<String, Integer> categoryPoints, List<String> likedCategories,
                       List<String> dislikedCategories, List<String> savedCategories) {
        this.categoryPoints = categoryPoints;
        this.likedCategories = likedCategories != null ? likedCategories : new ArrayList<>();  // Ensure it's never null
        this.dislikedCategories = dislikedCategories != null ? dislikedCategories : new ArrayList<>();
        this.savedCategories = savedCategories != null ? savedCategories : new ArrayList<>();
    }
    public UserPreferences() {
        // Constructor for empty preferences
    }

    public Map<String, Integer> getCategoryPoints() {
        return categoryPoints;
    }

    public List<String> getLikedCategories() {
    return likedCategories == null ? new ArrayList<>() : likedCategories;
}

    public List<String> getDislikedCategories() {
        return dislikedCategories;
    }

    public List<String> getSavedCategories() {
        return savedCategories;
    }

    // New method to print category points
    public void printCategoryPoints(String username) {
        System.out.println("Category points for user: " + username);
        if (categoryPoints == null || categoryPoints.isEmpty()) {
            System.out.println("No categories or points found.");
        } else {
            categoryPoints.forEach((category, points) ->
                System.out.println("Category: " + category + ", Points: " + points)
            );
        }
    }
}
