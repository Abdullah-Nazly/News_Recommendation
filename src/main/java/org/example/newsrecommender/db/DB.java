package org.example.newsrecommender.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DB {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017"; // MongoDB URI
    private static final String DATABASE_NAME = "News_recommender"; // Database name
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    // Connect to MongoDB
    public static void connect() {
        if (mongoClient == null) {
            try {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println("MongoDB connection successful.");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("MongoDB connection unsuccessful.");
            }
        }
    }

    // Get the MongoDatabase instance
    public static MongoDatabase getDatabase() {
        if (database == null) {
            connect();
        }
        return database;
    }

    // Close the connection
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("MongoDB connection closed.");
        }
    }
}
