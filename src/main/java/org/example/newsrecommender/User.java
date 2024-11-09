package org.example.newsrecommender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private int userId;
    private String username;
    private String password;
    private String email;
    private int contact;

    // Constructor for a new user registration
    public User(String username, String password, String email, int contact ) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.contact = contact;
    }

    // Constructor for an existing user (after login)
    public User(int userId, String username, String email, int contact) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.contact = contact;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public int getContact(){ return contact; }


    // Method to register a new user
    public boolean signUp() {
        String query = "INSERT INTO Users (username, password, email, contact) VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, this.username);
            pstmt.setString(2, this.password); // Consider hashing the password
            pstmt.setString(3, this.email);
            pstmt.setInt(4, this.contact);
            pstmt.executeUpdate();

            System.out.println("User registered successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

