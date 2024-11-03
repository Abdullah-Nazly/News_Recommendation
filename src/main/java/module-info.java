module org.example.newsrecommender {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires mysql.connector.j;


    opens org.example.newsrecommender to javafx.fxml;
    exports org.example.newsrecommender;
}