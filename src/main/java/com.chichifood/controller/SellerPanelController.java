package com.chichifood.controller;

import com.chichifood.network.RestaurantNetwork;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class SellerPanelController {
    @FXML
    private Button profileButton;

    @FXML
    private Button restaurantPanelButton;

    public void initialize() {
        profileButton.setOnAction(event -> {
        });

        restaurantPanelButton.setOnAction(event -> {
            RestaurantNetwork.getRestaurants(apiResponse -> {
                int responseCode = apiResponse.getStatusCode();
                Platform.runLater(() -> {
                    if (responseCode == 200) {

                        if (true) {
                            showAlert("موفق", "رستوران شما ثبت شده.");
                        }
                    } else {
                        // ببرمش به restaurantSignup.fxml
                    }
                });
            });
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
