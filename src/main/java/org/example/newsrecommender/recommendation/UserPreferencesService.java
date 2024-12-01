package org.example.newsrecommender.recommendation;

import org.bson.types.ObjectId;
import org.example.newsrecommender.db.DBservice;
import org.example.newsrecommender.user.UserPreferences;
import java.util.LinkedHashMap;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class UserPreferencesService {
    private final DBservice dbService;

    public UserPreferencesService(DBservice dbService) {
        this.dbService = dbService; // Dependency Injection for DBservice
    }
    public UserPreferences fetchUserPreferences(ObjectId userId) throws ExecutionException, InterruptedException {
        // Make an asynchronous call and wait for the result with .get()
        CompletableFuture<UserPreferences> future = dbService.getUserPreferencesAsync(userId);
        return future.get();  // Blocks here to get the result (you can also use other async handling methods if needed)
    }

    public Map<String, Integer> getSortedCategoryPoints(UserPreferences preferences) {
        if (preferences == null || preferences.getCategoryPoints() == null) {
            System.out.println("UserPreferences or CategoryPoints is null. Returning an empty map.");
            return Collections.emptyMap();
        }

        return preferences.getCategoryPoints().entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,                     // Key mapper
                        Map.Entry::getValue,                   // Value mapper
                        (e1, e2) -> e1,                        // Conflict resolution (use the first entry)
                        LinkedHashMap::new                     // LinkedHashMap to preserve order
                ));
    }
}