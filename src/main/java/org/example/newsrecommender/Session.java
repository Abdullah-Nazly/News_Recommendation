package org.example.newsrecommender;

import org.example.newsrecommender.user.User;
import com.mongodb.client.MongoDatabase;

public class Session {

    private static User currentUser;

    // Set the current user, ensure that UserLikes is initialized in User
    public static void setCurrentUser(User user, MongoDatabase database) {
        currentUser = user;
        currentUser.initializeUserPoints(database);  // Ensure UserPoints is initialized
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

}
