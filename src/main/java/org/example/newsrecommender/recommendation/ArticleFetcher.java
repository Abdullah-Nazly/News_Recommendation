package org.example.newsrecommender.recommendation;

import org.example.newsrecommender.articles.Article;
import org.example.newsrecommender.db.DBservice;

import java.util.List;

public class ArticleFetcher {
    private final DBservice dbService;

    public ArticleFetcher(DBservice dbService) {
        this.dbService = dbService;
    }

    public List<Article> fetchArticlesByCategory(String category, int limit) {
        return dbService.getArticlesByCategory(category, limit);
    }
}
