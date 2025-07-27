package com.chichifood.controller;

import com.chichifood.model.Restaurant;
import com.chichifood.network.BuyerNetwork;
import com.chichifood.network.SessionManager;
import com.google.gson.*;
import com.sun.javafx.scene.control.InputField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
                    JsonObject json = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();
                    JsonArray array = json.getAsJsonArray("restaurants");

                    for (JsonElement el : array) {
                        JsonObject obj = el.getAsJsonObject();
                        Restaurant r = new Restaurant(
                                obj.get("id").getAsInt(),
                                obj.get("name").getAsString(),
                                obj.get("address").getAsString(),
                                obj.get("phone").getAsString(),
                                obj.get("logo").getAsString(),
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

        // کلیک روی عکس برای رفتن به صفحه رستوران
        imageView.setOnMouseClicked(e -> {
            System.out.println("کلیک شد روی رستوران: " + restaurant.getName());
            // TODO: باز کردن پنجره اطلاعات رستوران
        });

        // اطلاعات متنی
        VBox infoBox = new VBox(5);
        Text name = new Text("نام: " + restaurant.getName());
        Text address = new Text("آدرس: " + restaurant.getAddress());
        Text taxFee = new Text(restaurant.getTaxFee() == 0 ? "" : "مالیات: " + restaurant.getTaxFee());
        Text addFee = new Text(restaurant.getAdditionalFee() == 0 ? "" : "هزینه اضافی: " + restaurant.getAdditionalFee());
        infoBox.getChildren().addAll(name, address, taxFee, addFee);

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

    private Image base64ToImage(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return new Image(new ByteArrayInputStream(bytes));
    }
}