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

    // Method to insert a new login record with both user_id and user_name
    public void recordLogin(ObjectId userId, String userName) {
        MongoCollection<Document> loginsCollection = database.getCollection("user_logins");

        // Create a document with user_id, user_name, and login_time fields
        Document loginDocument = new Document("user_id", userId)
                .append("user_name", userName)  // Add user_name to the login document
                .append("login_time", new Date());  // Log the current timestamp

        // Insert the document into the collection
        loginsCollection.insertOne(loginDocument);

        System.out.println("User login recorded successfully for user_id: " + userId + ", user_name: " + userName);
    }
}
