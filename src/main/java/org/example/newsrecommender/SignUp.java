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
import org.w3c.dom.views.DocumentView;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!tf_username.getText().trim().isEmpty() && !tf_password.getText().trim().isEmpty() && !tf_email.getText().trim().isEmpty()) {
                    DBUtils.signUpUser(event, tf_username.getText(), tf_password.getText(), tf_email.getText());
                } else {
                    System.out.println("Please fill in all the information");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please fill all the information sign up");
                    alert.show();
                }
            }
        });

        button_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "Login.fxml","Login",null);
            }
        });


    }
    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
