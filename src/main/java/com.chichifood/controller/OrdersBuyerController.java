package com.chichifood.controller;



import com.chichifood.model.Restaurant;
import com.chichifood.network.BuyerNetwork;
import com.chichifood.localdata.*;
import com.chichifood.network.NetworkService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.chichifood.network.SessionManager.showAlert;

public class OrdersBuyerController {

    @FXML private Button homeButton;
    @FXML private Button transactionsButton;
    @FXML private TextField itemSearchField;
    @FXML private TextField vendorSearchField;
    @FXML private Button searchButton;

    @FXML private VBox pendingOrdersContainer;
    @FXML private VBox activeOrdersContainer;
    @FXML private VBox pastOrdersContainer;

    @FXML
    public void initialize() {

        homeButton.setOnAction(e -> {
            Stage currentStage = (Stage) homeButton.getScene().getWindow();
            currentStage.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/BuyerPanel.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("خانه");
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        transactionsButton.setOnAction(e -> {
            Stage currentStage = (Stage) homeButton.getScene().getWindow();
            currentStage.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Transactions.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("تراکنش");
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

//        searchButton.setOnAction(event -> {
//            if (itemSearchField.getText().isEmpty() && vendorSearchField.getText().isEmpty()) {
//                showAlert("ورودی نامعتبر" , "لطفا فیلد ها را پر کنید. ");
//            }
//            String itemName = itemSearchField.getText();
//            String vendorName = vendorSearchField.getText();
//
//            BuyerNetwork.serachOrders(itemName, vendorName, apiResponse -> {
//                Platform.runLater(() -> {
//                    int statusCode = apiResponse.getStatusCode();
//                    String body = apiResponse.getBody();
//
//                    if (statusCode == 200) {
//                        JsonArray vendorsArray = JsonParser.parseString(body).getAsJsonArray();
//
//                        for (JsonElement vendorElement : vendorsArray) {
//                            JsonObject vendorJson = vendorElement.getAsJsonObject();
//                            Restaurant restaurant = new Restaurant(
//                                    vendorJson.get("id").getAsInt(),
//                                    vendorJson.get("name").getAsString(),
//                                    vendorJson.get("address").getAsString(),
//                                    vendorJson.get("phone").getAsString(),
//                                    vendorJson.get("logoBase64").getAsString(),
//                                    vendorJson.get("tax_fee").getAsInt(),
//                                    vendorJson.get("additional_fee").getAsInt()
//                            );
//                            restaurantList.add(restaurant);
//                        }
//
//                        try {
//                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/OrderBuyerSearch.fxml"));
//                            Parent root = loader.load();
//
//                            VendorsViewsController controller = loader.getController();
//                            controller.setVendors(restaurantList);
//
//                            Stage stage = new Stage();
//                            stage.setTitle("Orders");
//                            stage.setScene(new Scene(root));
//                            stage.show();
//                            ((Stage) searchButton.getScene().getWindow()).close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            });
//        });

        showPendingOrders();
        showActiveOrders();
        showPastOrders();
    }

    private void showPendingOrders() {
        pendingOrdersContainer.getChildren().clear();

        Map<Integer, List<CartItem>> allCarts = CartManager.getAllCarts();
        for (Map.Entry<Integer, List<CartItem>> entry : allCarts.entrySet()) {
            int vendorId = entry.getKey();
            List<CartItem> cartItems = entry.getValue();

            // عنوان رستوران (یا vendor ID)
            Label vendorLabel = new Label("رستوران شماره " + vendorId + ":");
            vendorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            pendingOrdersContainer.getChildren().add(vendorLabel);

            // لیست آیتم‌ها
            for (CartItem ci : cartItems) {
                String itemText = ci.getItem().getName() + " × " + ci.getQuantity();
                Label itemLabel = new Label(itemText);
                itemLabel.setStyle("-fx-padding: 0 0 0 10px;"); // کمی تورفتگی برای خوانایی
                pendingOrdersContainer.getChildren().add(itemLabel);
            }
            int price = CartManager.calculateTotalPrice(vendorId);
            Label priceLabel = new Label(price + "قیمت کل: ");
            pendingOrdersContainer.getChildren().add(priceLabel);
            // فاصله بین رستوران‌ها
            pendingOrdersContainer.getChildren().add(new Label(""));
        }
    }

    private void showOrder(int orderId) {
        BuyerNetwork.getOrder(orderId, response -> {
            if (response.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    activeOrdersContainer.getChildren().clear();
                    Label label = new Label("سفارش فعال: " + response.getBody());
                    activeOrdersContainer.getChildren().add(label);
                });
            } else {
                Platform.runLater(() -> {
                    activeOrdersContainer.getChildren().clear();
                    Label label = new Label("خطا در دریافت سفارش فعال: " + response.getBody());
                    activeOrdersContainer.getChildren().add(label);
                });
            }
        });
    }

    private void showActiveOrders() {
        int userId = buyerId();
        BuyerNetwork.getAllUserOrders(userId, response -> {
            if (response.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    pastOrdersContainer.getChildren().clear();

                    JsonArray orders = JsonParser.parseString(response.getBody()).getAsJsonArray();

                    for (JsonElement elem : orders) {
                        JsonObject order = elem.getAsJsonObject();

                        String status = order.get("status").getAsString();
                        if ((status.equals("completed") || status.equals("cancelled") || status.equals("unpaid and cancelled"))) continue; // فقط سفارش‌های تحویل‌داده‌شده

                        int orderId = order.get("id").getAsInt();
                        String vendorName = order.get("vendorName").getAsString();
                        int totalPrice = order.get("totalPrice").getAsInt();

                        Label label = new Label("سفارش #" + orderId +
                                " | رستوران: " + vendorName +
                                " | مبلغ: " + totalPrice + " تومان");

                        pastOrdersContainer.getChildren().add(label);
                    }

                    if (pastOrdersContainer.getChildren().isEmpty()) {
                        pastOrdersContainer.getChildren().add(new Label("هیچ سفارش فعالی وجود ندارد."));
                    }
                });
            } else {
                Platform.runLater(() -> {
                    pastOrdersContainer.getChildren().clear();
                    Label label = new Label("خطا در دریافت سفارش‌های فعال: " + response.getBody());
                    pastOrdersContainer.getChildren().add(label);
                });
            }
        });
    }
    private void showPastOrders() {
        int userId = buyerId();
        BuyerNetwork.getAllUserOrders(userId, response -> {
            if (response.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    pastOrdersContainer.getChildren().clear();

                    JsonArray orders = JsonParser.parseString(response.getBody()).getAsJsonArray();

                    for (JsonElement elem : orders) {
                        JsonObject order = elem.getAsJsonObject();

                        String status = order.get("status").getAsString();
                        if (!(status.equals("completed") || status.equals("cancelled") || status.equals("unpaid and cancelled"))) continue; // فقط سفارش‌های تحویل‌داده‌شده

                        int orderId = order.get("id").getAsInt();
                        String vendorName = order.get("vendorName").getAsString();
                        int totalPrice = order.get("totalPrice").getAsInt();

                        Label label = new Label("سفارش #" + orderId +
                                " | رستوران: " + vendorName +
                                " | مبلغ: " + totalPrice + " تومان");

                        pastOrdersContainer.getChildren().add(label);
                    }

                    if (pastOrdersContainer.getChildren().isEmpty()) {
                        pastOrdersContainer.getChildren().add(new Label("هیچ سفارش تحویل‌داده‌شده‌ای وجود ندارد."));
                    }
                });
            } else {
                Platform.runLater(() -> {
                    pastOrdersContainer.getChildren().clear();
                    Label label = new Label("خطا در دریافت سفارش‌های گذشته: " + response.getBody());
                    pastOrdersContainer.getChildren().add(label);
                });
            }
        });
    }
    public int buyerId(){
        final int[] id = {0};
        NetworkService.getProfile(apiResponse -> {
            JsonObject json = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();
            id[0] = json.get("id").getAsInt();

        });
        return id[0];
    }
}
