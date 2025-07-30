package com.chichifood.controller;

import com.chichifood.network.BuyerNetwork;
import com.chichifood.network.ApiResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class WalletController {

    @FXML
    private Button backButton, homeButton, topUpButton, transactionsButton;

    @FXML
    private Label walletBalanceLabel;

    @FXML
    private TextField amountField;

    private int walletBalance;

    public void setWalletBalance(int walletBalance) {
        this.walletBalance = walletBalance;
        walletBalanceLabel.setText("موجودی کیف پول: " + walletBalance + " تومان");
    }

    @FXML
    public void initialize() {
        homeButton.setOnAction(e -> navigate("/Views/BuyerPanel.fxml", "خانه"));
        transactionsButton.setOnAction(e -> navigate("/Views/Transactions.fxml", "تراکنش‌ها"));

        topUpButton.setOnAction(e -> handleTopUp());
    }

    private void handleTopUp() {
        String amountText = amountField.getText().trim();
        try {
            int amount = Integer.parseInt(amountText);
            if (amount <= 0) throw new NumberFormatException();

            BuyerNetwork.walletTopUp(amount, response -> {
                Platform.runLater(() -> {
                    if (response.getStatusCode() == 200) {
                        showAlert("موفقیت", "افزایش موجودی با موفقیت انجام شد.");
                        walletBalance += amount;
                        walletBalanceLabel.setText("موجودی کیف پول: " + walletBalance + " تومان");
                        amountField.clear();
                    } else {
                        showAlert("خطا", "افزایش موجودی ناموفق بود.\n" + response.getBody());
                    }
                });
            });

        } catch (NumberFormatException e) {
            showAlert("خطا", "لطفاً یک عدد معتبر وارد کنید.");
        }
    }

    private void navigate(String fxmlPath, String title) {
        Stage currentStage = (Stage) homeButton.getScene().getWindow();
        currentStage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("خطا", "مشکلی در باز کردن صفحه " + title + " به وجود آمد.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
