package com.chichifood.controller;

import com.chichifood.network.NetworkService;
import com.chichifood.network.RestaurantNetwork;
import com.chichifood.network.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

public class SellerPanelController {
    @FXML
    private Button profileButton;

    @FXML
    private Button restaurantPanelButton;
    @FXML
    private Button orderPanelButton;
    @FXML
    private Button LogoutButton;

    public void initialize() {
        NetworkService.getProfile(apiResponse -> {
            JsonObject json = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();
            int isUserConfirmed = json.get("isUserConfirmed").getAsInt();
            System.out.println(isUserConfirmed);
            Platform.runLater(() -> {
                if (isUserConfirmed != 1) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/notConfirmedUsers.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) profileButton.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("wait for confirmation");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("خطا", "مشکل در بارگذاری صفحه پروفایل");
                    }
                }
            });
        });

        profileButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/sellerProfile.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) profileButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Seller Profile");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("خطا", "مشکل در بارگذاری صفحه پروفایل");
            }
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
                            SessionManager.setRestaurantID(jsonObject.get("id").getAsString());
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

        LogoutButton.setOnAction(event -> {
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
            Stage stage = (Stage) LogoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();


        });

        orderPanelButton.setOnAction(event -> {
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
                            SessionManager.setRestaurantID(jsonObject.get("id").getAsString());
                            boolean isRestaurantConfirmed = jsonObject.get("isRestaurantConfirmed").getAsBoolean();
                            if (isRestaurantConfirmed) {
                                // لود کردن پنل رستوران
                                loader = new FXMLLoader(getClass().getResource("/views/orderPanel.fxml"));
                                scene = new Scene(loader.load());
                                stage.setScene(scene);
                                stage.setTitle("Order Panel");
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
