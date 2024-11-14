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
    private String contact; // Changed to String for phone numbers

    // Constructor for a new user registration
    public User(String username, String password, String email, String contact ) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.contact = contact;
    }

    // Constructor for an existing user (after login)
    public User(int userId, String username, String email, String contact) {
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

    public String getContact(){ return contact; }


    // Method to register a new user
    public boolean signUp() {

        // Validate input fields
        if (this.username == null || this.username.trim().isEmpty()) {
            System.out.println("Username cannot be empty or spaces!");
            return false;
        }
        if (this.password == null || this.password.trim().isEmpty()) {
            System.out.println("Password cannot be empty or spaces!");
            return false;
        }
        if (this.email == null || this.email.trim().isEmpty()) {
            System.out.println("Email cannot be empty or spaces!");
            return false;
        }
        if (this.contact == null || this.contact.trim().isEmpty() || this.contact.length() != 10) {
            System.out.println("Contact must be 10 digits long!");
            return false;
        }

        // Check for existing username, email, or contact in the database
        String checkQuery = "SELECT * FROM Users WHERE username = ? OR email = ? OR contact = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setString(1, this.username);
            checkStmt.setString(2, this.email);
            checkStmt.setString(3, this.contact);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Username, email, or contact number already exists!");
                return false; // Duplicate found, return false
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Insert new user into the database if no duplicates were found
        String insertQuery = "INSERT INTO Users (username, password, email, contact) VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            insertStmt.setString(1, this.username);
            insertStmt.setString(2, this.password); // Consider hashing the password
            insertStmt.setString(3, this.email);
            insertStmt.setString(4, this.contact);
            insertStmt.executeUpdate();

            System.out.println("User registered successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Static method to authenticate a user on login
    public static User login(String username, String password) {
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // Consider hashing the password
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String email = rs.getString("email");
                String contact = rs.getString("contact");
                return new User(userId, username, email, contact); // User successfully authenticated
            } else {
                System.out.println("Invalid username or password.");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

