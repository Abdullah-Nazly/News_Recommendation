package org.example.newsrecommender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    Connection c;
    private static final String URL = "jdbc:mysql://localhost:3306/news_oop";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public DB(){
    }
    public void connect(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection Successful");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Connection unsuccessful");
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}