package org.example.newsrecommender.recommendation;

import org.bson.types.ObjectId;
import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.user.UserPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationManagerConcurrencyTest {
    private RecommendationManager recommendationManager;
    private UserPreferencesService mockPreferencesService;
    private ArticleFetcher mockArticleFetcher;

    @BeforeEach
    void setUp() {
        mockPreferencesService = mock(UserPreferencesService.class);
        mockArticleFetcher = mock(ArticleFetcher.class);
        recommendationManager = new RecommendationManager(mockPreferencesService, mockArticleFetcher);
    }

    @Test
    void testConcurrentRecommendationsForMultipleUsers() throws InterruptedException, ExecutionException {
        // Mock user preferences for multiple users
        Map<String, Integer> categoryPointsUser1 = Map.of("Technology", 10, "Science", 8);
        Map<String, Integer> categoryPointsUser2 = Map.of("Health", 12, "Sports", 7);

        UserPreferences preferencesUser1 = new UserPreferences(categoryPointsUser1, List.of("Technology"), null, null);
        UserPreferences preferencesUser2 = new UserPreferences(categoryPointsUser2, List.of("Health"), null, null);

        ObjectId userId1 = new ObjectId();
        ObjectId userId2 = new ObjectId();

        when(mockPreferencesService.fetchUserPreferences(eq(userId1))).thenReturn(preferencesUser1);
        when(mockPreferencesService.fetchUserPreferences(eq(userId2))).thenReturn(preferencesUser2);

        when(mockPreferencesService.getSortedCategoryPoints(preferencesUser1)).thenReturn(categoryPointsUser1);
        when(mockPreferencesService.getSortedCategoryPoints(preferencesUser2)).thenReturn(categoryPointsUser2);

        when(mockArticleFetcher.fetchArticlesByCategory(eq("Technology"), anyInt()))
            .thenReturn(List.of(new Article("http://tech.com", "Tech Headline")));
        when(mockArticleFetcher.fetchArticlesByCategory(eq("Science"), anyInt()))
            .thenReturn(List.of(new Article("http://science.com", "Science Headline")));
        when(mockArticleFetcher.fetchArticlesByCategory(eq("Health"), anyInt()))
            .thenReturn(List.of(new Article("http://health.com", "Health Headline")));
        when(mockArticleFetcher.fetchArticlesByCategory(eq("Sports"), anyInt()))
            .thenReturn(List.of(new Article("http://sports.com", "Sports Headline")));

        // Simulate concurrent execution for multiple users
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            ObjectId userId = (i % 2 == 0) ? userId1 : userId2; // Alternate between two users
            executorService.submit(() -> recommendationManager.recommendArticles(userId));
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS), "Executor did not terminate in time");

        // Verify no data corruption and results for each user
        List<Article> articles = recommendationManager.getArticleList();
        assertNotNull(articles);
        assertFalse(articles.isEmpty());

        // Check user-specific results
        assertTrue(articles.stream().anyMatch(article -> "Tech Headline".equals(article.getHeadline())));
        assertTrue(articles.stream().anyMatch(article -> "Science Headline".equals(article.getHeadline())));
        assertTrue(articles.stream().anyMatch(article -> "Health Headline".equals(article.getHeadline())));
        assertTrue(articles.stream().anyMatch(article -> "Sports Headline".equals(article.getHeadline())));
    }
}
