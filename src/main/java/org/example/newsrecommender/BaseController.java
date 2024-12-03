package org.example.newsrecommender;

import javafx.stage.Stage;

public interface BaseController {
    // Abstract method with title and noDecoration parameter
    void navigateToView(Stage currentStage, String fxmlFile, String title, boolean noDecoration);
    // Default method: can be overridden if needed, or used as-is
    default void navigateToView(Stage currentStage, String fxmlFile) {
        navigateToView(currentStage, fxmlFile, null, false); // Default to noDecoration = false
    }
    // Default method to navigate without a title (decoration can be adjusted as needed)
    default void navigateToView(Stage currentStage, String fxmlFile, String title) {
        navigateToView(currentStage, fxmlFile, title, false); // Default to noDecoration = false
    }

}
