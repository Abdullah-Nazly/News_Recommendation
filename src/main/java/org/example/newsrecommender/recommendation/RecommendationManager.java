package org.example.newsrecommender.recommendation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.types.ObjectId;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.user.UserPreferences;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RecommendationManager {
    private static final Logger logger = Logger.getLogger(RecommendationManager.class.getName());
    private final UserPreferencesService preferencesService;
    private final ArticleFetcher articleFetcher;
    private final ObservableList<Article> articleList; // List to hold recommended articles
    private final ExecutorService executorService; // Executor for concurrent tasks

    // Constructor to initialize required services and set up executor
    public RecommendationManager(UserPreferencesService preferencesService, ArticleFetcher articleFetcher) {
        this.preferencesService = preferencesService;
        this.articleFetcher = articleFetcher;
        this.articleList = FXCollections.observableArrayList();
        this.executorService = Executors.newFixedThreadPool(4); // Adjust threads for concurrency
    }

    public void recommendArticles(ObjectId userId) { //Generates article recommendations for a given user.
        try {
            UserPreferences preferences = preferencesService.fetchUserPreferences(userId);
            Map<String, Integer> sortedCategories = preferencesService.getSortedCategoryPoints(preferences);

            articleList.clear();

            List<Future<List<Article>>> futures = new ArrayList<>(); // List to hold async tasks
            int categoryCount = 0; // Limit recommendations to top 4 categories

            for (Map.Entry<String, Integer> entry : sortedCategories.entrySet()) {
                String category = entry.getKey();
                futures.add(executorService.submit(() -> {
                    List<Article> articles = articleFetcher.fetchArticlesByCategory(category, 7);
                    return rankArticles(articles, preferences); // Rank articles based on preferences
                }));

                if (++categoryCount >= 4) break;
            }

            for (Future<List<Article>> future : futures) {
                articleList.addAll(future.get());
            }

        } catch (Exception e) {
            logger.severe("Error recommending articles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Ranks a list of articles based on user preferences.
    private List<Article> rankArticles(List<Article> articles, UserPreferences preferences) {
        Set<String> likedCategories = new HashSet<>(preferences.getLikedCategories());
        Map<String, Integer> categoryPoints = preferences.getCategoryPoints();

        // Calculate similarity scores
        return articles.stream()
                .sorted(Comparator.comparingDouble(article -> calculateCosineSimilarity(article, likedCategories, categoryPoints)))
                .limit(10)
                .collect(Collectors.toList());
    }

    //Calculates cosine similarity between an article and user's preferences.
    private double calculateCosineSimilarity(Article article, Set<String> likedCategories, Map<String, Integer> categoryPoints) {
        int articleCategoryScore = categoryPoints.getOrDefault(article.getCategory(), 0);
        return likedCategories.contains(article.getCategory()) ? articleCategoryScore * 1.0 : 0.5 * articleCategoryScore; // Adjust score based on user preference
    }
    //Returns the list of recommended articles.
    public ObservableList<Article> getArticleList() {
        return articleList;
    }

}