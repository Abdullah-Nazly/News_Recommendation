package org.example.newsrecommender.recommendation;

import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.db.DBservice;

import java.util.List;

public class ArticleFetcher {
    private final DBservice dbService; // Service for database interactions

    // Constructor to initialize DBservice
    public ArticleFetcher(DBservice dbService) {
        this.dbService = dbService;
    }

    public List<Article> fetchArticlesByCategory(String category, int limit) { //Fetches a limited number of articles from the database for a specific category.
        return dbService.getArticlesByCategory(category, limit);
    }
}