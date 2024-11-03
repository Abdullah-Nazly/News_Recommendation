package org.example.newsrecommender;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public PasswordField tf_password;
    public TextField tf_username;
    @FXML
    private Button cancelButton;
    @FXML
    private Button button_login;
    @FXML
    private Button button_signup;

    @Override
    public void initialize(URL location, ResourceBundle resource){
        button_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.logInUser(event, tf_username.getText(), tf_password.getText());
            }
        });

        button_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "SignUp.fxml", "Signup", null);
            }
        });

    }




    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }



}