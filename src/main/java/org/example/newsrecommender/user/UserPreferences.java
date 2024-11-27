package org.example.newsrecommender.user;

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
        this.likedCategories = likedCategories;
        this.dislikedCategories = dislikedCategories;
        this.savedCategories = savedCategories;
    }

    public UserPreferences() {
        // Constructor for empty preferences
    }

    public Map<String, Integer> getCategoryPoints() {
        return categoryPoints;
    }

    public List<String> getLikedCategories() {
        return likedCategories;
    }

    public List<String> getDislikedCategories() {
        return dislikedCategories;
    }

    public List<String> getSavedCategories() {
        return savedCategories;
    }
}
