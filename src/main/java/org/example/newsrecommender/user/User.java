package org.example.newsrecommender.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.newsrecommender.db.DB;

public class User {

    private ObjectId userId; // Add userId field

    private String username;
    private String password;
    private String email;
    private String contact;
    private UserPoints userPoints;

    // Initialize UserPoints for managing categories and points
    public void initializeUserPoints(MongoDatabase database) {
        if (this.userPoints == null) {
            this.userPoints = new UserPoints(database);
        }
    }

    // Constructor
    public User(String username, String password, String email, String contact) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.contact = contact;

    }
    // Constructor with userId (for existing users retrieved from database)
    public User(ObjectId userId, String username, String password, String email, String contact) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.contact = contact;

    }

    // Getters and setters

    public ObjectId getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    // Getter for userLikes
    public UserPoints getUserLikes() {
        return userPoints;
    }

    // Initialize UserLikes manually


    // Save User to the MongoDB 'users' collection
    public void saveToDatabase() {
        // Create a connection to the database
        MongoDatabase database = DB.getDatabase(); // Ensure DB.getDatabase() returns MongoDatabase
        MongoCollection<Document> usersCollection = database.getCollection("users");

        // Convert the User object into a Document and insert into the database
        Document document = new Document("username", this.username)
                .append("password", this.password)
                .append("email", this.email)
                .append("contact", this.contact);

        usersCollection.insertOne(document); // Insert the user document into the collection
    }
    // Method to convert a MongoDB Document to a User object
    public static User fromDocument(Document document) {
        ObjectId userId = document.getObjectId("_id"); // Get the MongoDB _id field
        String username = document.getString("username");
        String password = document.getString("password");
        String email = document.getString("email");
        String contact = document.getString("contact");

        // Return a new User object using the data from the document
        return new User(userId, username, password, email, contact);
    }


}
