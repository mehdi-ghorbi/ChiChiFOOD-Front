package com.chichifood.controller;

import com.chichifood.network.BuyerNetwork;
import com.google.gson.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class OneOrderController {

    @FXML private Button homeButton, backButton;
    @FXML private Label orderIdLabel, vendorNameLabel, addressLabel, deliveryFeeLabel;
    @FXML private Label totalPriceLabel, taxFeeLabel, serviceFeeLabel, statusLabel;
    @FXML private Label createdAtLabel, updatedAtLabel;
    @FXML private VBox itemsContainer;

    private int orderId;

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        BuyerNetwork.getOrder(orderId, response -> {
            if (response.getStatusCode() == 200) {
                Platform.runLater(() -> showOrder(response.getBody()));
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "خطا در دریافت سفارش: " + response.getBody());
                    alert.showAndWait();
                });
            }
        });
    }

    private void showOrder(String body) {
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        System.out.println(json.toString());
        orderIdLabel.setText("شناسه سفارش: " + json.get("id").getAsInt());
        vendorNameLabel.setText("رستوران: " + json.get("vendorName").getAsString());
        addressLabel.setText("آدرس: " + json.get("delivery_address").getAsString());
        deliveryFeeLabel.setText("هزینه پیک: " + json.get("courier_fee").getAsInt());
        totalPriceLabel.setText("قیمت کل: " + json.get("pay_price").getAsInt());
        taxFeeLabel.setText("مالیات: " + json.get("tax_fee").getAsInt());
        serviceFeeLabel.setText("هزینه سرویس: " + json.get("additional_fee").getAsInt());
        statusLabel.setText("وضعیت: " + json.get("status").getAsString());
        createdAtLabel.setText("تاریخ شروع: " + json.get("created_at").getAsString());
        updatedAtLabel.setText("آخرین تغییر: " + json.get("updated_at").getAsString());

        itemsContainer.getChildren().clear();

    JsonArray items = json.getAsJsonArray("items");
        for (JsonElement itemElem : items) {
            JsonObject item = itemElem.getAsJsonObject();
            String text = "• " + item.get("name").getAsString() + " - " + item.get("price").getAsInt() + " تومان";
            itemsContainer.getChildren().add(new Label(text));
        }

    }

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
        backButton.setOnAction(e -> {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/OrderBuyer.fxml"));
                Parent root = loader.load();

                Stage previousStage = new Stage();
                previousStage.setScene(new Scene(root));
                previousStage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
