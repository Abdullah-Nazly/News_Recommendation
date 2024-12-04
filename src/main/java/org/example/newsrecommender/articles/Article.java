package org.example.newsrecommender.articles;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Objects;

public class Article {
    private ObjectId articleId;
    private String link;
    private String headline;
    private String category;
    private String author; // Optional
    private String date;   // Optional

    // Constructor with all fields
    public Article(ObjectId articleId, String link, String headline, String category, String author, String date) {
        this.articleId = articleId;
        this.link = link;
        this.headline = headline;
        this.category = category != null ? category : "Unknown";
        this.author = author != null ? author : "Unknown";
        this.date = date != null ? date : "Unknown";
    }

    // Constructor without articleId
    public Article(String link, String headline, String category, String author, String date) {
        this(null, link, headline, category, author, date); // Delegate to primary constructor
    }

    // Constructor with minimal fields (for quick creation)
    public Article(String link, String headline) {
        this(link, headline, "Unknown", "Unknown", "Unknown"); // Default values
    }

    // Static method to create an Article object from a MongoDB Document
    public static Article fromDocument(Document document) {
        ObjectId articleId = document.getObjectId("_id");
        return new Article(
                articleId,
                document.getString("link"),
                document.getString("headline"),
                document.getString("category"),
                document.getString("author"),
                document.getString("date")
        );
    }

    // Convert Article to MongoDB Document
    public Document toDocument() {
        return new Document("link", link)
                .append("headline", headline)
                .append("category", category)
                .append("author", author)
                .append("date", date);
    }

    // Getters and Setters
    public ObjectId getId() {
        return articleId;
    }

    public void setId(ObjectId articleId) {
        this.articleId = articleId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Override equals to compare Article objects by articleId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(articleId, article.articleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId);
    }

    @Override
    public String toString() {
        return "Article{" +
                "articleId=" + articleId +
                ", link='" + link + '\'' +
                ", headline='" + headline + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                '}';
    }


}
