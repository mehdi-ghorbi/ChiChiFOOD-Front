package com.chichifood.controller;

import com.chichifood.model.Order;
import com.chichifood.network.DeliveryNetwork;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.chichifood.network.SessionManager.showAlert;

public class DeliveryHistoryController {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button backButton;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> addressColumn;
    @FXML private TableColumn<Order, Number> courierFeeColumn;
    @FXML private TableColumn<Order, String> restaurantNameColumn;

    private final ObservableList<Order> allOrders = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDeliveryAddress()));
        courierFeeColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCourierFee()));
        restaurantNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVendorName()));

        seedSampleData();

        searchButton.setOnAction(e -> filterOrders());

        backButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/courierPanel.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Courier Panel");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("خطا", "مشکل در بارگذاری صفحه پروفایل");
            }
        });
    }

    private void seedSampleData(){
        DeliveryNetwork.getHistory("http://localhost:8569/deliveries/history", apiResponse -> {
            if (apiResponse.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    try {
                        JsonArray jsonArray = JsonParser.parseString(apiResponse.getBody()).getAsJsonArray();
                        allOrders.clear();

                        for (JsonElement elem : jsonArray) {
                            JsonObject obj = elem.getAsJsonObject();
                            Order order = new Order();
                            order.setId(obj.get("id").getAsInt());
                            order.setDeliveryAddress(obj.has("deliveryAddress") ? obj.get("deliveryAddress").getAsString() : "");
                            order.setVendorName(obj.has("restaurantName") ? obj.get("restaurantName").getAsString() : "");
                            order.setCourierFee(obj.has("courierFee") ? obj.get("courierFee").getAsInt() : 0);
                            order.setVendorID(obj.has("vendorID") && !obj.get("vendorID").isJsonNull()
                                    ? obj.get("vendorID").getAsInt()
                                    : 0);
                            System.out.println(order.toString());
                            System.out.println(order.getDeliveryAddress());
                            allOrders.add(order);
                        }

                        orderTable.setItems(FXCollections.observableArrayList(allOrders));

                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("خطا", "مشکل در پردازش اطلاعات سفارش‌ها");
                    }
                });
            } else {
                Platform.runLater(() -> showAlert("خطا", "عدم دریافت سفارش‌ها از سرور"));
            }
        });

    }


    private void filterOrders() {
        String keyword = searchField.getText().trim().toLowerCase();
        List<Order> filtered = allOrders.stream()
                .filter(order -> order.getVendorName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        orderTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private String getRestaurantName(int vendorId) {
        Map<Integer, String> dummyVendors = Map.of(
                1, "رستوران اکبر جوجه",
                2, "رستوران پیتزا فلفل",
                3, "رستوران کبابی طلایی"
        );
        return dummyVendors.getOrDefault(vendorId, "رستوران نامشخص");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
