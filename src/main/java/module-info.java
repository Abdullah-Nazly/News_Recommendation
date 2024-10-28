module org.example.newsrecommender {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
//    requires mysql.connector.java;


    opens org.example.newsrecommender to javafx.fxml;
    exports org.example.newsrecommender;
}