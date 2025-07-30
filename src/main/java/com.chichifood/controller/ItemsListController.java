package com.chichifood.controller;

import com.chichifood.localdata.CartManager;
import com.chichifood.model.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class ItemsListController {

    @FXML
    private VBox itemsContainer;

    @FXML
    private Button backButton;

    public void initialize() {
        backButton.setOnAction(e -> {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/BuyerPanel.fxml"));
                Parent root = loader.load();
                Stage previousStage = new Stage();
                previousStage.setScene(new Scene(root));
                previousStage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void setItems(List<Item> items) {
        for (Item item : items) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
            card.setPrefWidth(760);

            ImageView imageView = new ImageView();
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            try {
                File imageFile = new File(item.getImageBase64());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    System.out.println("فایل تصویر یافت نشد برای آیتم " + item.getId());
                }
            } catch (Exception e) {
                System.out.println("خطا در بارگذاری عکس برای آیتم " + item.getId());
                e.printStackTrace();
            }


            Label nameLabel = new Label(item.getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label descriptionLabel = new Label(item.getDescription());
            descriptionLabel.setWrapText(true);

            HBox controls = new HBox(10);
            controls.setAlignment(Pos.CENTER_LEFT);

            Button plusBtn = new Button("+");
            Button minusBtn = new Button("-");
            Label countLabel = new Label();

            countLabel.setStyle("-fx-font-size: 14px;");
            countLabel.setVisible(false);
            minusBtn.setVisible(false);

            plusBtn.setOnAction(e -> {
                int count = countLabel.getText().isEmpty() ? 0 : Integer.parseInt(countLabel.getText());
                count++;
                countLabel.setText(String.valueOf(count));
                countLabel.setVisible(true);
                minusBtn.setVisible(true);
                CartManager.addItemToCart(item);
            });

            minusBtn.setOnAction(e -> {
                int count = Integer.parseInt(countLabel.getText());
                count--;

                if (count <= 0) {
                    countLabel.setText("");
                    countLabel.setVisible(false);
                    minusBtn.setVisible(false);
                } else {
                    countLabel.setText(String.valueOf(count));
                }
                CartManager.decreaseItemQuantity(item);
            });

            controls.getChildren().addAll(plusBtn, countLabel, minusBtn);

            card.getChildren().addAll(nameLabel, imageView, descriptionLabel, controls);
            itemsContainer.getChildren().add(card);
        }
    }
}