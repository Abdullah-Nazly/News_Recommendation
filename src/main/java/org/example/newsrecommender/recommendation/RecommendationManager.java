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
    private final ObservableList<Article> articleList;
    private final ExecutorService executorService;

    public RecommendationManager(UserPreferencesService preferencesService, ArticleFetcher articleFetcher) {
        this.preferencesService = preferencesService;
        this.articleFetcher = articleFetcher;
        this.articleList = FXCollections.observableArrayList();
        this.executorService = Executors.newFixedThreadPool(4); // Adjust threads for concurrency
    }

    public void recommendArticles(ObjectId userId) {
        try {
            UserPreferences preferences = preferencesService.fetchUserPreferences(userId);
            Map<String, Integer> sortedCategories = preferencesService.getSortedCategoryPoints(preferences);

            articleList.clear();

            List<Future<List<Article>>> futures = new ArrayList<>();
            int categoryCount = 0;

            for (Map.Entry<String, Integer> entry : sortedCategories.entrySet()) {
                String category = entry.getKey();
                int numArticles = getNumArticles(categoryCount);

                futures.add(executorService.submit(() -> {
                    List<Article> articles = articleFetcher.fetchArticlesByCategory(category, 7);
                    return rankArticles(articles, preferences);
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

    private int getNumArticles(int rank) {
        return switch (rank) {
            case 0 -> 4;
            case 1 -> 3;
            case 2 -> 2;
            case 3 -> 1;
            default -> 0;
        };
    }

    private List<Article> rankArticles(List<Article> articles, UserPreferences preferences) {
        Set<String> likedCategories = new HashSet<>(preferences.getLikedCategories());
        Map<String, Integer> categoryPoints = preferences.getCategoryPoints();

        // Calculate similarity scores
        return articles.stream()
                .sorted(Comparator.comparingDouble(article -> calculateCosineSimilarity(article, likedCategories, categoryPoints)))
                .limit(10)
                .collect(Collectors.toList());
    }

    private double calculateCosineSimilarity(Article article, Set<String> likedCategories, Map<String, Integer> categoryPoints) {
        int articleCategoryScore = categoryPoints.getOrDefault(article.getCategory(), 0);
        return likedCategories.contains(article.getCategory()) ? articleCategoryScore * 1.0 : 0.5 * articleCategoryScore;
    }

    private void printRecommendationsToConsole() {
        logger.info("Recommended Articles:");
        articleList.forEach(article -> logger.info(article.toString()));
    }

    public ObservableList<Article> getArticleList() {
        return articleList;
    }

    public void shutDownExecutor() {
        executorService.shutdown();
    }
}
