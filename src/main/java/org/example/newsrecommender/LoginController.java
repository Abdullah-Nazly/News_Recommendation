package org.example.newsrecommender;

import javafx.event.ActionEvent;
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
    public PasswordField enterPasswordField;
    public ImageView brandingImageView;
    public TextField userNameTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File brandingFile = new File("@../../../../../../../../../../Abdullah/Screenshots/Screenshot_20230318-194025_WhatsApp.jpg");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);
    }



    @FXML
    public void loginButtonOnAction(ActionEvent event){

        if (!(!userNameTextField.getText().isBlank() && enterPasswordField.getText().isBlank())){
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter user name and password");
        }
    }

    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin(){

    }

}