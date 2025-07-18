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
    requires org.testng;
//    requires org.junit.jupiter.api;      // For JUnit 5 API
//    requires org.junit.jupiter.engine;   // For JUnit 5 engine
//    requires org.junit.platform.commons; // For JUnit platform commons
//    requires org.junit.platform.engine;
    requires org.apache.opennlp.tools;
    requires org.apache.commons.csv;
    requires lucene.core;

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
    exports org.example.newsrecommender.NLP;
    opens org.example.newsrecommender.NLP to javafx.fxml;
}