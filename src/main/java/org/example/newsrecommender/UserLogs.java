package org.example.newsrecommender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserLogs {

    // Method to insert a new login record
    public void recordLogin(int userId) {
        String query = "INSERT INTO user_logins (user_id, login_time) VALUES (?, CURRENT_TIMESTAMP)";

        try (Connection conn = DB.getConnection();  // DB.getConnection() retrieves the database connection
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the user_id parameter in the query
            stmt.setInt(1, userId);

            // Execute the query
            stmt.executeUpdate();

            System.out.println("User login recorded successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error recording user login.");
        }
    }
}
