package org.example.newsrecommender.articles;

import java.util.List;

public interface ContentLoader {
    List<Article> loadArticles(String category);  // Updated return type to List<Article>
    String loadArticleContentFromUrl(String url);
}
