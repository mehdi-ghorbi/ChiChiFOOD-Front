package com.chichifood.controller;

import com.chichifood.network.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

import static com.chichifood.network.SessionManager.showAlert;

public class AdminPanelController {
    @FXML
    private Button logoutBtn;
    @FXML
    private Button restaurantBtn;
    @FXML
    private Button usersBtn;
    @FXML
    private Button ordersBtn;
    @FXML
    private Button couponBtn;


    public void initialize() {
    logoutBtn.setOnAction(event -> {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/logout"))
                .header("Authorization", "Bearer " + SessionManager.getToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        SessionManager.clearToken();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/login.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    })  ;

    usersBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/adminUsersPanel.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) usersBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Admin - Users");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "Cannot load the admin users panel.");
            }
        });

    couponBtn.setOnAction(e -> {

    });

    restaurantBtn.setOnAction(e -> {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/adminRestaurantPanel.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) restaurantBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin - restaurants");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Cannot load the admin restaurants panel.");
        }
    });
    }
}
