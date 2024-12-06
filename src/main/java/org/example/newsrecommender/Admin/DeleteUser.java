package org.example.newsrecommender.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.newsrecommender.BaseController;
import org.example.newsrecommender.db.DB;
import org.example.newsrecommender.user.User;

import java.io.IOException;

public class DeleteUser implements BaseController { // interface with overriding method is used
    public TableView<User> userTable;
    public TableColumn<User, String> userName;
    public TableColumn<User, String> password;
    public TableColumn<User, String> contact;
    public TableColumn<User, String> email;
    public Button backButton;

    public void initialize() {
        // Fetch users from the database and display them in the table
        loadUsersFromDatabase();

        // Set custom cell factories to display user data without modifying the User class
        userName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        password.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPassword()));
        contact.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContact()));
        email.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        // Add event listener for double-click on a user row
        userTable.setOnMouseClicked(this::handleTableRowDoubleClick);
    }

    private void loadUsersFromDatabase() {
        // initializing the database
        var database = DB.getDatabase();
        var usersCollection = database.getCollection("users");

        // Clear current table data
        userTable.getItems().clear();

        // Get all users from the database
        var documents = usersCollection.find();

        // Convert documents to User objects and add to the table
        for (Document document : documents) {
            User user = User.fromDocument(document);
            userTable.getItems().add(user);
        }
    }

    private void handleTableRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click event
            User selectedUser = userTable.getSelectionModel().getSelectedItem();

            if (selectedUser != null) {
                // Show confirmation alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete User");
                alert.setHeaderText("Are you sure you want to delete this user?");
                alert.setContentText(selectedUser.getUsername());

                alert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        deleteUser(selectedUser);
                    }
                });
            }
        }
    }

    private void deleteUser(User user) {
        // Get database and users collection
        var database = DB.getDatabase();
        var usersCollection = database.getCollection("users");

        // Delete the user from the database using its ObjectId
        usersCollection.deleteOne(new Document("_id", user.getUserId()));

        // Refresh the TableView after deletion
        loadUsersFromDatabase();

        // Show success alert
        showAlert(Alert.AlertType.INFORMATION, "User Deleted", "The user has been successfully deleted.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackButton() { // back button
        navigateToView((Stage) backButton.getScene().getWindow(), "/org/example/newsrecommender/SystemAdmin.fxml");
    }

    @Override
    public void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxmlFile));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            if (noDecoration) {
                currentStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            }

            if (title != null) {
                currentStage.setTitle(title);
            }
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
