package org.example.newsrecommender;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class SignUp implements Initializable {

    public Button cancelButton;
    @FXML
    private Button button_signup;
    @FXML
    private Button button_login;
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField tf_password;
    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_contact;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {





    }
    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSignUp(ActionEvent event) {
        // Collect input data from text fields
        String username = tf_username.getText();
        String password = tf_password.getText();
        String email = tf_email.getText();
        int contact = Integer.parseInt(tf_contact.getText());

        // Create a new User object and attempt to register it
        User newUser = new User(username, password, email, contact);
        boolean signUpSuccessful = newUser.signUp();

        // Show a confirmation alert based on the outcome
        if (signUpSuccessful) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "There was an error registering the user.");
        }
    }

    // Utility method to display alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
