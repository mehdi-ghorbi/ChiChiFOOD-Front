package com.chichifood.controller;

import com.chichifood.network.RestaurantNetwork;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

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
                String body = apiResponse.getBody();

                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader;
                        Scene scene;
                        Stage stage = (Stage) restaurantPanelButton.getScene().getWindow();

                        if (responseCode == 200) {
                            JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray();
                            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                            boolean isRestaurantConfirmed = jsonObject.get("isRestaurantConfirmed").getAsBoolean();
                            if (isRestaurantConfirmed) {
                                // لود کردن پنل رستوران
                                loader = new FXMLLoader(getClass().getResource("/views/RestaurantPanel.fxml"));
                                scene = new Scene(loader.load());
                                stage.setScene(scene);
                                stage.setTitle("Restaurant Panel");
                            } else {
                                showAlert("403", "Your Restaurant is not confirmed");
                            }
                        } else if (responseCode == 404) {
                            // لود کردن صفحه ثبت رستوران
                            loader = new FXMLLoader(getClass().getResource("/views/RestaurantSignup.fxml"));
                            scene = new Scene(loader.load());
                            stage.setScene(scene);
                            stage.setTitle("Restaurant Signup");
                        }else {
                            showAlert(String.valueOf(responseCode), body);

                        }

                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
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
