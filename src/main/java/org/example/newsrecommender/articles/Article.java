package org.example.newsrecommender.articles;

public class Article {
    private String link;
    private String headline;
    private String category;
    private String author; // Optional
    private String date; // Optional, as per your collection structure

    // Constructor
    public Article(String link, String headline, String category, String author, String date) {
        this.link = link;
        this.headline = headline;
        this.category = category;
        this.author = author;
        this.date = date;
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
}
