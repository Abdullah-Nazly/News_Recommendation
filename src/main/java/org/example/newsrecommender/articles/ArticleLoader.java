package org.example.newsrecommender.articles;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArticleLoader implements ContentLoader {
    private MongoDatabase database;

    public ArticleLoader(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public List<Article> loadArticles(String category) {
        MongoCollection<Document> collection = database.getCollection("articles");
        List<Document> documents = collection.find(new Document("category", category)).into(new ArrayList<>());

        List<Article> articles = new ArrayList<>();
        for (Document document : documents) {
            articles.add(Article.fromDocument(document));
        }
        return articles;
    }

    @Override
    public String loadArticleContentFromUrl(String url) {
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(url).get();
            StringBuilder content = new StringBuilder();
            doc.select("p").forEach(paragraph -> content.append(paragraph.text()).append("\n"));
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to load article from URL.";
        }
    }
}