package com.chichifood.controller;

import com.chichifood.model.Restaurant;
import com.chichifood.network.BuyerNetwork;
import com.chichifood.network.SessionManager;
import com.google.gson.*;
import com.sun.javafx.scene.control.InputField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import java.io.*;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONString;
import java.io.File;
import java.util.Base64;
import java.util.Optional;

public class FavoritesListController {

    @FXML
    private VBox favoritesBox;

    @FXML
    private Button backButton;
    @FXML
    public void initialize() {
        backButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/buyerPanel.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("پنل خریدار");
                stage.show();
                ((Stage) backButton.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        BuyerNetwork.getFavoritesRestaurants(apiResponse -> {
            Platform.runLater(() -> {
                int status = apiResponse.getStatusCode();
                if (status == 200) {

                    JsonArray array = JsonParser.parseString(apiResponse.getBody()).getAsJsonArray();

                    for (JsonElement el : array) {
                        JsonObject obj = el.getAsJsonObject();
                        Restaurant r = new Restaurant(
                                obj.get("id").getAsInt(),
                                obj.get("name").getAsString(),
                                obj.get("address").getAsString(),
                                obj.get("phone").getAsString(),
                                obj.get("logoBase64").getAsString(),
                                obj.get("tax_fee").getAsInt(),
                                obj.get("additional_fee").getAsInt()
                        );

                        favoritesBox.getChildren().add(createRestaurantCard(r));
                    }
                } else {
                    SessionManager.showAlert("خطا", "بارگذاری لیست مورد علاقه‌ها ناموفق بود.");
                }
            });
        });
    }

    private HBox createRestaurantCard(Restaurant restaurant) {
        HBox card = new HBox(10);
        card.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-radius: 5;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setImage(base64ToImage(restaurant.getLogoBase64()));

        // بزرگ شدن تصویر هنگام hover
        imageView.setOnMouseEntered(e -> {
            imageView.setScaleX(1.1);
            imageView.setScaleY(1.1);
        });
        imageView.setOnMouseExited(e -> {
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
        });

        imageView.setOnMouseClicked(e -> {
            System.out.println("کلیک شد روی رستوران: " + restaurant.getName());
            BuyerNetwork.getVendorMenus(restaurant.getId(), fullVendor -> {
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/VendorsMenuView.fxml"));
                        Parent root = loader.load();

                        VendorMenuController controller = loader.getController();
                        controller.setVendor(fullVendor);

                        Stage stage = new Stage();
                        stage.setTitle("منوی رستوران " + fullVendor.getName());
                        stage.setScene(new Scene(root));
                        stage.show();

                        Stage currentStage = (Stage) imageView.getScene().getWindow();
                        currentStage.close();
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }
                });
            });
        });

        // اطلاعات متنی
        VBox infoBox = new VBox(5);
        Label vendorLabel = new Label("رستوران: " + restaurant.getName());
        vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 13px;");
        vendorLabel.setCursor(Cursor.HAND);
        vendorLabel.setOnMouseEntered(e -> vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 15px; -fx-font-weight: bold;"));
        vendorLabel.setOnMouseExited(e -> vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 13px;"));
        vendorLabel.setOnMouseClicked(e -> {
            System.out.println("باز کردن صفحه رستوران " + restaurant.getName());
            BuyerNetwork.getVendorMenus(restaurant.getId(), fullVendor -> {
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/VendorsMenuView.fxml"));
                        Parent root = loader.load();

                        VendorMenuController controller = loader.getController();
                        controller.setVendor(fullVendor);  // حالا با منوها

                        Stage stage = new Stage();
                        stage.setTitle("منوی رستوران " + fullVendor.getName());
                        stage.setScene(new Scene(root));
                        stage.show();

                        // بستن صفحه فعلی
                        Stage currentStage = (Stage) vendorLabel.getScene().getWindow();
                        currentStage.close();
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }
                });
            });
        });

        Text address = new Text("آدرس: " + restaurant.getAddress());
        Text taxFee = new Text(restaurant.getTaxFee() == 0 ? "" : "مالیات: " + restaurant.getTaxFee());
        Text addFee = new Text(restaurant.getAdditionalFee() == 0 ? "" : "هزینه اضافی: " + restaurant.getAdditionalFee());
        infoBox.getChildren().addAll(vendorLabel, address, taxFee, addFee);

        // حذف از علاقه‌مندی
        Text deleteText = new Text("❌");
        deleteText.setStyle("-fx-font-size: 20; -fx-cursor: hand;");
        deleteText.setOnMouseClicked((MouseEvent e) -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "آیا مطمئنی می‌خواهی حذفش کنی؟", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {BuyerNetwork.removeFavoritesRestaurants(restaurant.getId(), response -> {
                Platform.runLater(() -> {
                    if (response.getStatusCode() == 200) {
                        favoritesBox.getChildren().remove(card);
                    } else {
                        SessionManager.showAlert("خطا", "حذف ناموفق بود.");
                    }
                });
            });
            }
        });

        card.getChildren().addAll(imageView, infoBox, deleteText);
        return card;
    }

    private Image base64ToImage(String base64Path) {
        try {
            FileInputStream inputStream = new FileInputStream(base64Path);
            return new Image(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
