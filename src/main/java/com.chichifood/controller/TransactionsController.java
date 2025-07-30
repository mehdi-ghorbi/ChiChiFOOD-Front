package com.chichifood.controller;

import com.chichifood.network.BuyerNetwork;
import com.google.gson.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class TransactionsController {

    @FXML
    private VBox transactionsContainer;

    @FXML
    private Button homeButton, backButton;

    @FXML
    public void initialize() {
        BuyerNetwork.getTransactions(apiResponse -> {
            Platform.runLater(() -> {
                if (apiResponse.getStatusCode() == 200) {
                    JsonArray jsonArray = JsonParser.parseString(apiResponse.getBody()).getAsJsonArray();
                    for (JsonElement element : jsonArray) {
                        JsonObject transaction = element.getAsJsonObject();

                        VBox card = new VBox(5);
                        card.setPadding(new Insets(10));
                        card.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ff6f00; -fx-border-radius: 5px; -fx-background-radius: 5px;");

                        Label idLabel = new Label("شناسه تراکنش: " + transaction.get("id").getAsInt());
                        Label orderIdLabel = new Label("شناسه سفارش: " + transaction.get("orderID").getAsInt());
                        Label userIdLabel = new Label("شناسه کاربر: " + transaction.get("userID").getAsString());
                        Label methodLabel = new Label("روش پرداخت: " + transaction.get("method").getAsString());
                        Label statusLabel = new Label("وضعیت: " + transaction.get("status").getAsString());
                        Label moneyLabel = new Label("مبلغ: " + transaction.get("money").getAsInt() + " تومان");

                        for (Label label : new Label[]{idLabel, orderIdLabel, userIdLabel, methodLabel, statusLabel, moneyLabel}) {
                            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                        }

                        card.getChildren().addAll(idLabel, orderIdLabel, userIdLabel, methodLabel, statusLabel, moneyLabel);
                        transactionsContainer.getChildren().add(card);
                    }
                } else {
                    Label errorLabel = new Label("خطا در دریافت اطلاعات: " + apiResponse.getBody());
                    errorLabel.setTextFill(Color.RED);
                    transactionsContainer.getChildren().add(errorLabel);
                }
            });
        });

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
