package org.example.newsrecommender;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Boot {

    @FXML
    private Button user_button;
    @FXML
    private Button admin_button;
    @FXML
    private void handleUserLogin() {
        try {
            Stage stage = (Stage) user_button.getScene().getWindow(); // Get current stage from user_button
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("UserLogin.fxml")));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdminLogin() {
        try {
            Stage stage = (Stage) admin_button.getScene().getWindow(); // Get current stage from admin_button
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sysLogin.fxml")));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
