//package org.example.newsrecommender.recommendation;
//
//import org.bson.types.ObjectId;
//import org.example.newsrecommender.db.DBservice;
//import org.junit.jupiter.api.BeforeEach;
//import org.testng.annotations.Test;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static org.mockito.Mockito.mock;
//
//class RecommendationManagerTest {
//    private RecommendationManager recommendationManager;
//    private DBservice dbService;
//    private ArticleFetcher articleFetcher;
//    private UserPreferencesService preferencesService;
//
//    @BeforeEach
//    void setUp() {
//        dbService = mock(DBservice.class);
//        articleFetcher = mock(ArticleFetcher.class);
//        preferencesService = new UserPreferencesService(dbService);
//        recommendationManager = new RecommendationManager(preferencesService, articleFetcher);
//    }
//
//    @Test
//    void testConcurrentRecommendations() throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(5);
//
//        for (int i = 0; i < 10; i++) {
//            ObjectId userId = new ObjectId();
//            executor.submit(() -> recommendationManager.recommendArticles(userId));
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(10, TimeUnit.SECONDS);
//
//        recommendationManager.shutDownExecutor();
//    }
//}
