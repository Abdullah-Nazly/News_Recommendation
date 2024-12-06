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

public class ArticleLoader implements ContentLoader { // An Interface to load the contents
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
        // Check if the URL is null or empty
        if (url == null || url.isEmpty()) {
            logger.warning("URL is null or empty; cannot load article content.");
            return "Invalid URL."; // Return an error message for invalid input
        }

        // Validate the URL format
        if (!isValidUrl(url)) {
            logger.warning("The supplied URL is malformed: " + url);
            return "The URL is malformed. Please ensure it starts with 'http://' or 'https://'.";
        }

        try {
            // Attempt to fetch the article content using Jsoup
            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
            return extractContentFromDocument(doc); // Extract and return the content
        } catch (IOException e) {
            // Handle exceptions during the network request
            logger.severe("Failed to load article from URL: " + e.getMessage());
            return "Failed to load article from the URL. Please check your internet connection and try again.";
        }
    }

    // Helper method to validate the URL format
    private boolean isValidUrl(String url) {
        try {
            // Attempt to parse the URL to check its validity
            java.net.URL parsedUrl = new java.net.URL(url);
            // Check if the protocol is HTTP or HTTPS
            return "http".equalsIgnoreCase(parsedUrl.getProtocol()) || "https".equalsIgnoreCase(parsedUrl.getProtocol());
        } catch (Exception e) {
            // Return false if URL parsing fails
            return false;
        }
    }


    private String extractContentFromDocument(org.jsoup.nodes.Document document) {
        StringBuilder content = new StringBuilder();
        document.select("p").forEach(paragraph -> content.append(paragraph.text()).append("\n"));
        return content.toString();
    }
}
