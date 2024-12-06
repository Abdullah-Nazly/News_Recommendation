package org.example.newsrecommender;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import org.example.newsrecommender.db.DB;
import org.example.newsrecommender.db.DBservice;
import org.example.newsrecommender.user.Profile;

import java.io.IOException;

public class Application implements BaseController {

    @FXML
    private Button btn_profile;
    @FXML
    private Button button_logout;
    @FXML
    private Button readbutton;
    @FXML
    private Button openRecommendation;
    /**
     * Navigate to the Read Article screen.
     */
    @FXML
    public void openArticle() {
        Stage currentStage = (Stage) readbutton.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/ReadArticle.fxml", "Read Article", true);
    }

    /**
     * Navigate to the Recommended Articles screen.
     */
    @FXML
    public void openRecommendation() {
        Stage currentStage = (Stage) openRecommendation.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/Recommendation.fxml", "Recommended Articles");
    }

    /**
     * Handle logout action and navigate to the login screen.
     */
    @FXML
    public void logoutAction() {
        Stage currentStage = (Stage) button_logout.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/UserLogin.fxml", "Login");
    }

    /**
     * Navigate to the Profile screen.
     */
    public void go_profile(ActionEvent event) {
        Stage currentStage = (Stage) btn_profile.getScene().getWindow();
        navigateToView(currentStage, "/org/example/newsrecommender/Profile.fxml", "Profile");
    }


    /**
     * Implementation of the navigateToView method from BaseController_test.
     */
    @Override
    public void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlFile));
            javafx.scene.Parent root = loader.load();

            currentStage.setScene(new javafx.scene.Scene(root));

            if (title != null) {
                currentStage.setTitle(title);
            }

            // Remove window decorations if required
            if (noDecoration) {
                currentStage.setOpacity(1.0); // Ensures the window is fully opaque
            }

            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
