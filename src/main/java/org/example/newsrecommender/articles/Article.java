package org.example.newsrecommender.articles;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Article {
    private ObjectId articleId;
    private String link;
    private String headline;
    private String category;
    private String author; // Optional
    private String date; // Optional, as per your collection structure

    // Constructor
    public Article(ObjectId articleId, String link, String headline, String category, String author, String date) {
        this.articleId = articleId;
        this.link = link;
        this.headline = headline;
        this.category = category;
        this.author = author;
        this.date = date;
    }
    // Constructor for Article without articleId (MongoDB will generate it)
    public Article(String link, String headline, String category, String author, String date) {
        this.link = link;
        this.headline = headline;
        this.category = category;
        this.author = author;
        this.date = date;
    }


    // Getters and Setters for each field
    public ObjectId getId() {
        return articleId;
    }

    public void setId(ObjectId articleId) {
        this.articleId = articleId;
    }


    // Getters and Setters for each field
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

    @Override
    public String toString() {
        return "Article{" +
                "link='" + link + '\'' +
                ", headline='" + headline + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    // Static method to create an Article object from a MongoDB Document
    public static Article fromDocument(Document document) {
        ObjectId articleId = document.getObjectId("_id");  // Get the MongoDB _id field
        return new Article(
                articleId,  // Set the articleId
                document.getString("link"),
                document.getString("headline"),
                document.getString("category"),
                document.getString("author"),
                document.getString("date")
        );
    }

}
