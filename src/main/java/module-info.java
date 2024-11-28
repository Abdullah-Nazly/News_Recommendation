module org.example.newsrecommender {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.web;
    requires org.jsoup;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    opens org.example.newsrecommender to javafx.fxml;
    exports org.example.newsrecommender;
    exports org.example.newsrecommender.db;
    opens org.example.newsrecommender.db to javafx.fxml;
    exports org.example.newsrecommender.user;
    opens org.example.newsrecommender.user to javafx.fxml;
    exports org.example.newsrecommender.Admin;
    opens org.example.newsrecommender.Admin to javafx.fxml;
    exports org.example.newsrecommender.articles;
    opens org.example.newsrecommender.articles to javafx.fxml;
    exports org.example.newsrecommender.recommendation;
    opens org.example.newsrecommender.recommendation to javafx.fxml;
}