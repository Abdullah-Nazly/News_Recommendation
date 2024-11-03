package org.example.newsrecommender;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DBUtils {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username ){
        Parent root = null;

        if (username != null){
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                Application application = loader.getController();
                application.setUserInformation(username);
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String password, String email){
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_table","root","myAdmin123 ");
            psCheckUserExists = connection.prepareStatement("select * from users where username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()){
                System.out.println("User name already exist");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("you cannot use this user name");
                alert.show();
            } else {
                psInsert = connection.prepareStatement("INSERT INTO users(username, password, email) VALUES (?, ?, ?)");
                psInsert.setString(1,username);
                psInsert.setString(2,password);
                psInsert.setString(3,email);
                psInsert.executeUpdate();

                changeScene(event, "Application.fxml","Welcome", username);
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null){
                try {
                    psCheckUserExists.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (psInsert != null){
                try {
                    psInsert.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }

    }

    public static void logInUser(ActionEvent event, String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_table","root","myAdmin123 ");
            preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()){
                System.out.println("User not found in the database");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                System.out.println("Provided credentials are incorrect");
                alert.show();
            } else{
                while(resultSet.next()){
                    String retrievePassword = resultSet.getString("password");
                    if (retrievePassword.equals(password)){
                        changeScene(event, "Application.fxml", "Welcome", username);
                    } else {
                        System.out.println("Password did not match");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Provided credentials are incorrect");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }


}
