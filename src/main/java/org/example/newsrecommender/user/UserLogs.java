package org.example.newsrecommender.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Date;

public class UserLogs {

    private MongoDatabase database;

    // Constructor to pass the MongoDB instance
    public UserLogs(MongoDatabase database) {
        this.database = database;
    }

    // Method to insert a new login record
    public void recordLogin(int userId) {
        MongoCollection<Document> loginsCollection = database.getCollection("user_logins");

        // Create a document with user_id and login_time fields
        Document loginDocument = new Document("user_id", userId)
                .append("login_time", new Date());  // Using Date to log the current timestamp

        // Insert the document into the collection
        loginsCollection.insertOne(loginDocument);

        System.out.println("User login recorded successfully!");
    }
}
