package com.chichifood.controller;

import com.chichifood.model.Order;
import com.chichifood.network.DeliveryNetwork;
import com.chichifood.network.NetworkService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static com.chichifood.network.SessionManager.showAlert;

public class DeliveryPanelController {

    @FXML private Text titleText;
    @FXML private TableView<Order> orderTableView;
    @FXML private TableColumn<Order, String> addressColumn;
    @FXML private TableColumn<Order, Number> feeColumn;
    @FXML private TableColumn<Order, String> restaurantColumn;
    @FXML private Button acceptOrderButton;
    @FXML private Button backButton;
    @FXML private Button deliveredButton;
    private static int ID;
    private static  Boolean doIHaveADelivery = false;
    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // اتصال ستون‌ها به فیلدهای کلاس Order
        addressColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDeliveryAddress()));
        feeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCourierFee()));
        restaurantColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVendorName()));
        loadSampleOrders();
        getID();
        if (doIHaveADelivery) {
            deliveredButton.setDisable(false);
            orderTableView.setDisable(true);
            acceptOrderButton.setDisable(true);
        }else {
            deliveredButton.setDisable(true);
        }
        orderTableView.setItems(orders);
        acceptOrderButton.setOnAction(e -> handleAcceptOrder());
        backButton.setOnAction(e -> handleBack());
        deliveredButton.setOnAction(e -> handleDelivered());
    }
    private void getID(){
        NetworkService.getProfile(apiResponse -> {
            JsonObject json = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();
            ID = json.get("id").getAsInt();
        });
    }
    private void loadSampleOrders() {
        DeliveryNetwork.getOrders(apiResponse -> {
            System.out.println();
            if (apiResponse.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    try {
                        orders.clear();
                        var jsonArray = JsonParser.parseString(apiResponse.getBody()).getAsJsonArray();

                        for (var element : jsonArray) {
                            var json = element.getAsJsonObject();
                            Order order = new Order();

                            order.setId(json.has("id") && !json.get("id").isJsonNull() ? json.get("id").getAsInt() : 0);
                            order.setVendorID(json.has("vendor_id") && !json.get("vendor_id").isJsonNull() ? json.get("vendor_id").getAsInt() : 0);
                            order.setVendorName(json.has("vendor_name") ? json.get("vendor_name").getAsString() : "");
                            order.setCourierFee(json.has("courier_fee") && !json.get("courier_fee").isJsonNull() ? json.get("courier_fee").getAsInt() : 0);
                            order.setDeliveryAddress(json.has("delivery_address") && !json.get("delivery_address").isJsonNull()
                                    ? json.get("delivery_address").getAsString()
                                    : "نامشخص");

                            orders.add(order);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert(String.valueOf(apiResponse.getStatusCode()), apiResponse.getBody());
                    }
                });
            } else {

                Platform.runLater(() ->
                        showAlert(String.valueOf(apiResponse.getStatusCode()), apiResponse.getBody()));
            }
        });
    }


    private void handleAcceptOrder() {
        Order selected = orderTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("هشدار", "لطفاً یک سفارش را انتخاب کنید");

        }
        JsonObject json = new JsonObject();
        json.addProperty("status", "on the way");
        json.addProperty("courier_id", ID);
        DeliveryNetwork.getDelivery(json,selected.getId(),apiResponse -> {
            if (apiResponse.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    doIHaveADelivery = true;
                    deliveredButton.setDisable(false);
                    orderTableView.setDisable(true);
                    acceptOrderButton.setDisable(true);
                });
            }else {
                showAlert(String.valueOf(apiResponse.getStatusCode()), apiResponse.getBody());
            }

        });

    }


    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/courierPanel.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Courier Panel");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("خطا", "مشکل در بارگذاری صفحه پروفایل");
        }
    }

    private void handleDelivered() {
        Order selected = orderTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("هشدار", "لطفاً یک سفارش را انتخاب کنید");
        }
        JsonObject json = new JsonObject();
        json.addProperty("status", "completed");
        json.addProperty("courier_id", ID);
        DeliveryNetwork.getDelivery(json,selected.getId(),apiResponse -> {
            if (apiResponse.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    deliveredButton.setDisable(true);
                    orderTableView.setDisable(false);
                    acceptOrderButton.setDisable(false);
                    doIHaveADelivery = false;
                    orders.remove(selected);
                });
            }else {
                showAlert(String.valueOf(apiResponse.getStatusCode()), apiResponse.getBody());
            }
        });
    }
}
