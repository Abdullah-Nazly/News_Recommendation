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
    public List<Document> loadArticles(String category) {
        MongoCollection<Document> collection = database.getCollection("articles");
        return collection.find(new Document("category", category)).into(new ArrayList<>());
    }

    @Override
    public String loadArticleContentFromUrl(String url) {
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
            StringBuilder content = new StringBuilder();
            doc.select("p").forEach(paragraph -> content.append(paragraph.text()).append("\n"));
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to load article from URL.";
        }
    }
}
