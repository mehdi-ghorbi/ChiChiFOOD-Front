//package com.chichifood.controller;
//
//import com.chichifood.model.Item;
//import javafx.fxml.FXML;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import javax.swing.text.html.ImageView;
//import java.io.ByteArrayInputStream;
//import java.util.Base64;
//import java.util.List;
//
//public class ItemsListController {
//
//    @FXML
//    private VBox itemsContainer;
//    @FXML
//    private Button backButton;
//
//    public void initialize() {
//        backButton.setOnAction(e -> {
//            // بستن این صفحه و برگشت به قبلی
//            Stage stage = (Stage) backButton.getScene().getWindow();
//            stage.close();
//        });
//    }
//
//    public void setItems(List<Item> items) {
//        for (Item item : items) {
//            HBox row = new HBox(15);
//            row.setAlignment(Pos.CENTER_LEFT);
//            row.setPadding(new Insets(10));
//            row.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-color: #f8f8f8;");
//
//            // تصویر
//            ImageView imageView = new ImageView();
//            imageView.setFitWidth(80);
//            imageView.setFitHeight(80);
//            try {
//                byte[] imageBytes = Base64.getDecoder().decode(item.getImageBase64());
//                Image image = new Image(new ByteArrayInputStream(imageBytes));
//                imageView.setImage(image);
//            } catch (Exception e) {
//                System.out.println("خطا در بارگذاری عکس برای آیتم " + item.getId());
//            }
//
//            imageView.setOnMouseClicked(ev -> {
//                System.out.println("تصویر کلیک شد: " + item.getName());
//                // می‌تونی اینجا کار خاصی انجام بدی مثلاً نمایش بزرگ‌شده
//            });
//
//            // اطلاعات متنی آیتم
//            VBox infoBox = new VBox(5);
//            infoBox.getChildren().addAll(
//                    new Label("ID: " + item.getId()),
//                    new Label("Name: " + item.getName()),
//                    new Label("Description: " + item.getDescription()),
//                    new Label("Vendor ID: " + item.getVendorId()),
//                    new Label("Price: " + item.getPrice()),
//                    new Label("Supply: " + item.getSupply()),
//                    new Label("Keywords: " + String.join(", ", item.getKeywords()))
//            );
//
//            row.getChildren().addAll(imageView, infoBox);
//            itemsContainer.getChildren().add(row);
//        }
//    }
//}
