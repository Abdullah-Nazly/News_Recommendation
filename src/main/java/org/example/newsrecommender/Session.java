package org.example.newsrecommender;

import org.example.newsrecommender.user.User;

public class Session {

    // Declare a static variable to hold the current user
    private static User currentUser;

    // Set the current user
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Get the current user
    public static User getCurrentUser() {
        return currentUser;
    }

    // Check if a user is logged in
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Log the user out (clear the session)
    public static void logout() {
        currentUser = null;
    }
}
