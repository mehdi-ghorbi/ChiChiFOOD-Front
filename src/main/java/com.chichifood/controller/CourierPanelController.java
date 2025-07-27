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

public class CourierPanelController {

    @FXML
    private Button profileBtn;

    @FXML
    private Button deliveyBtn;

    @FXML
    private Button historyBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    public void initialize() {
        profileBtn.setOnAction(event -> openProfile());
        deliveyBtn.setOnAction(event -> startDelivery());
        historyBtn.setOnAction(event -> viewHistory());
        logoutBtn.setOnAction(event -> logout());
    }

    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/courierProfile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Courier Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("خطا", "مشکل در بارگذاری صفحه پروفایل");
        }
    }

    private void startDelivery() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/deliveryPanel.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) deliveyBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Delivery Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("خطا", "مشکل در بارگذاری صفحه پروفایل");
        }
    }

    private void viewHistory() {
        System.out.println("History button clicked");
    }

    private void logout() {
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
    }
}
