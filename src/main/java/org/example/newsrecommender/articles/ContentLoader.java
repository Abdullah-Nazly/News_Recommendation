package org.example.newsrecommender.articles;

import org.bson.Document;
import java.util.List;

public interface ContentLoader {
    List<Document> loadArticles(String category);
    String loadArticleContentFromUrl(String url);
}
