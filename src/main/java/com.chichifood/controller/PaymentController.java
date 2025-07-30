package com.chichifood.controller;

import com.chichifood.network.BuyerNetwork;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class PaymentController {

    @FXML
    private Button homeButton;

    @FXML
    private Button backButton;

    @FXML
    private Button onlinePaymentButton;

    @FXML
    private Button walletPaymentButton;

    @FXML
    private Label walletBalanceLabel;

    @FXML
    private Label totalPriceLabel;

    private int totalPrice = 0; // قیمت کل
    private int walletBalance = 0; // موجودی کیف پول
    private int orderId;

    public void setPaymentInfo(int totalPrice, int orderId, int walletBalance) {
        this.totalPrice = totalPrice;
        this.orderId = orderId;
        this.walletBalance = walletBalance;

        totalPriceLabel.setText("قیمت کل: " + totalPrice + " تومان");
        walletBalanceLabel.setText(walletBalance + " تومان");
    }

    @FXML
    public void initialize() {
        onlinePaymentButton.setOnAction(e -> {
            showAlert("پرداخت با موفقیت انجام شد");

            BuyerNetwork.pay(orderId, "ONLINE", response -> {
                System.out.println("ONLINE payment response: " + response.getStatusCode() + " - " + response.getBody());

                Platform.runLater(() -> goHome());
            });
        });

        walletPaymentButton.setOnAction(e -> {
            if (walletBalance < totalPrice) {
                showAlert("موجودی کافی نیست.");
            } else {
                BuyerNetwork.pay(orderId, "WALLET", response -> {
                    System.out.println("WALLET payment response: " + response.getStatusCode() + " - " + response.getBody());

                    Platform.runLater(() -> goHome());
                });
            }
        });

        homeButton.setOnAction(e -> goHome());

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

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("پرداخت");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void goHome() {
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
    }
}
