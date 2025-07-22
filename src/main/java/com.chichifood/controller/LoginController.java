package com.chichifood.controller;

import com.chichifood.model.User;
import com.chichifood.network.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField userNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> {
            String phone = userNameField.getText();
            String password = passwordField.getText();
            User user = new User(phone, password);

            NetworkService.login(user, apiResponse -> {
                Platform.runLater(() -> {
                    int statusCode = apiResponse.getStatusCode();
                    String body = apiResponse.getBody();

                    if (statusCode == 200) {
                        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                        String token = jsonObject.get("token").getAsString();
                        SessionManager.setToken(token);
                        SessionManager.setRole(jsonObject.get("role").getAsString());
                      //  showAlert("Login Successful", "Welcome!");
                       openCorrectPanel(SessionManager.getRole());
                    } else {
                        showAlert(String.valueOf(statusCode),  "Message:\n" + body);
                    }
                });
            });
        });
        signupButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/signup.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Signup Panel");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Cannot load your panel.");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openCorrectPanel(String role) {
        String fxmlPath = "";
        switch (role.toLowerCase()) {
            case "admin":
                fxmlPath = "/Views/adminPanel.fxml";
                openPanel(fxmlPath,role);
                break;
            case "seller":
                fxmlPath = "/Views/sellerPanel.fxml";
                openPanel(fxmlPath,role);
                break;
            case "buyer":
                fxmlPath = "/Views/buyerPanel.fxml";
                openPanel(fxmlPath,role);
                break;
            case "courier":
                fxmlPath = "/Views/courierPanel.fxml";
                openPanel(fxmlPath,role);
                break;
            default:
                showAlert("Unknown Role", "Role: " + role);
                return;
        }
    }
    private void openPanel(String fxmlPath, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(role + "Panel");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load your panel.");
        }
    }

}
