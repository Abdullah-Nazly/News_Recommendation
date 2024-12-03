package org.example.newsrecommender.articles;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArticleLoader implements ContentLoader {
    private static final Logger logger = Logger.getLogger(ArticleLoader.class.getName());

    private final MongoCollection<Document> articleCollection;

    public ArticleLoader(MongoDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("Database instance cannot be null.");
        }
        this.articleCollection = database.getCollection("articles");
    }

    @Override
    public List<Article> loadArticles(String category) {
        if (category == null || category.isEmpty()) {
            logger.warning("Category is null or empty; returning an empty article list.");
            return new ArrayList<>();
        }

        List<Document> documents = articleCollection.find(new Document("category", category)).into(new ArrayList<>());

        List<Article> articles = new ArrayList<>();
        for (Document document : documents) {
            articles.add(Article.fromDocument(document));
        }
        return articles;
    }

    @Override
    public String loadArticleContentFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            logger.warning("URL is null or empty; cannot load article content.");
            return "Invalid URL.";
        }

        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
            return extractContentFromDocument(doc);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load article from URL: " + url, e);
            return "Failed to load article from URL.";
        }
    }

    private String extractContentFromDocument(org.jsoup.nodes.Document document) {
        StringBuilder content = new StringBuilder();
        document.select("p").forEach(paragraph -> content.append(paragraph.text()).append("\n"));
        return content.toString();
    }
}
