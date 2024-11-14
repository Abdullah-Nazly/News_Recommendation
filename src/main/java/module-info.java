module org.example.newsrecommender {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires mysql.connector.j;
    requires javafx.web;

    exports org.example.newsrecommender;
    opens org.example.newsrecommender to javafx.fxml;


}