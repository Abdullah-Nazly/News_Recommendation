package org.example.newsrecommender.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.Date;

public class UserLogs {

    private MongoDatabase database;

    // Constructor to initialize the MongoDatabase instance
    public UserLogs(MongoDatabase database) {
        this.database = database;
    }

    // Method to record a user login with user ID and username
    public void recordLogin(ObjectId userId, String userName) {
        MongoCollection<Document> loginsCollection = database.getCollection("user_logins");

        // Create a new document with login details
        Document loginDocument = new Document("user_id", userId)
                .append("user_name", userName)  // Add user name to the document
                .append("login_time", new Date());  // Log the timestamp of the login

        // Insert the document into the collection
        loginsCollection.insertOne(loginDocument);

        System.out.println("Login recorded for user: " + userName + " (ID: " + userId + ")");
    }

}