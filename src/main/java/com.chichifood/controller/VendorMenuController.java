package com.chichifood.controller;

import com.chichifood.localdata.CartManager;
import com.chichifood.model.Item;
import com.chichifood.model.Menu;
import com.chichifood.model.Restaurant;
import com.chichifood.network.BuyerNetwork;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.geometry.Pos.CENTER_LEFT;

public class VendorMenuController {

    @FXML
    private VBox rootContainer;

    @FXML
    private VBox menusContainer;

    @FXML
    private Button homeButton;

    @FXML
    private Label restaurantNameLabel;

    @FXML
    private Label restaurantAddressLabel;

    @FXML
    private Label restaurantPhoneLabel;

    @FXML
    private ImageView restaurantLogo;

    @FXML
    private Button favoriteIcon;

    private Restaurant vendor;

    private boolean isFavorite = false;
    public void setVendor(Restaurant vendor) {
        this.vendor = vendor;
        showVendorInfo();
        showMenus();
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

        favoriteIcon.setOnMouseClicked(event -> {
            if (vendor == null) return;  // اطمینان از مقدار vendor

            if (isFavorite) {
                BuyerNetwork.removeFavoritesRestaurants(vendor.getId(), response -> {
                    if (response.getStatusCode() == 200) {
                        isFavorite = false;
                        Platform.runLater(() -> updateFavoriteIcon(false));
                    } else {
                        // خطا در حذف: پیام یا لاگ
                    }
                });
            } else {
                BuyerNetwork.addFavoritesRestaurants(vendor.getId(), response -> {
                    if (response.getStatusCode() == 200) {
                        isFavorite = true;
                        Platform.runLater(() -> updateFavoriteIcon(true));
                    } else {
                        // خطا در اضافه کردن: پیام یا لاگ
                    }
                });
            }
        });
    }

    private void showVendorInfo() {
        restaurantNameLabel.setText(vendor.getName());
        restaurantAddressLabel.setText("آدرس: " + vendor.getAddress());
        restaurantPhoneLabel.setText("تلفن: " + vendor.getPhone());

        if (vendor.getLogoBase64() != null && !vendor.getLogoBase64().isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(vendor.getLogoBase64());
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            restaurantLogo.setImage(image);
        }

        BuyerNetwork.getFavoritesRestaurants(response -> {
            if (response.getStatusCode() != 200) {
                System.err.println("Error loading favorites: " + response.getBody());
                return;
            }
            JsonArray restaurants = JsonParser.parseString(response.getBody()).getAsJsonArray();
            boolean found = false;
            for (JsonElement el : restaurants) {
                JsonObject obj = el.getAsJsonObject();
                int id = obj.get("id").getAsInt();
                if (id == vendor.getId()) {
                    found = true;
                    break;
                }
            }
            isFavorite = found;
            Platform.runLater(() -> updateFavoriteIcon(isFavorite));
        });
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        favoriteIcon.setText(isFavorite ? "❤" : "♡");
        favoriteIcon.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-text-fill: " + (isFavorite ? "red" : "gray") + ";" +
                        "-fx-cursor: hand;" +
                        "-fx-background-color: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0 0 0 0;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 0;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
    }



    private void showMenus() {
        for (Menu menu : vendor.getMenus()) {
            HBox headerBox = new HBox(10);
            Label titleLabel = new Label(menu.getTitle());
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label toggleArrow = new Label("▼");
            toggleArrow.setStyle("-fx-font-size: 16px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            headerBox.getChildren().addAll(titleLabel, spacer, toggleArrow);

            FlowPane itemsPane = new FlowPane();
            itemsPane.setHgap(10);
            itemsPane.setVgap(10);
            itemsPane.setPadding(new Insets(5));
            itemsPane.setStyle("-fx-background-color: #fefefe; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");

            for (Item item : menu.getItems()) {
                VBox itemBox = new VBox(5);
                itemBox.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-background-radius: 5;");
                itemBox.setPrefWidth(180);

                Label nameLabel = new Label(item.getName());
                nameLabel.setStyle("-fx-font-weight: bold;");

                ImageView imageView = new ImageView();
                imageView.setFitWidth(150);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(item.getImageBase64());
                    imageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
                } catch (Exception e) {}

                Label descLabel = new Label(item.getDescription());
                descLabel.setWrapText(true);
                descLabel.setMaxWidth(170);

                Label priceLabel = new Label("قیمت: " + item.getPrice() + " تومان");

                HBox controls = new HBox(5);
                controls.setAlignment(CENTER_LEFT);

                Button plusBtn = new Button("+");
                Button minusBtn = new Button("-");
                Label countLabel = new Label();
                countLabel.setStyle("-fx-font-size: 14px;");
                countLabel.setVisible(false);
                minusBtn.setVisible(false);

                plusBtn.setOnAction(ev -> {
                    int count = countLabel.getText().isEmpty() ? 0 : Integer.parseInt(countLabel.getText());
                    count++;
                    countLabel.setText(String.valueOf(count));
                    countLabel.setVisible(true);
                    minusBtn.setVisible(true);
                    CartManager.addItemToCart(item);
                });

                minusBtn.setOnAction(ev -> {
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

                itemBox.getChildren().addAll(nameLabel, imageView, descLabel, priceLabel, controls);
                itemsPane.getChildren().add(itemBox);
            }

            menusContainer.getChildren().add(headerBox);

            headerBox.setOnMouseClicked(event -> {
                int headerIndex = menusContainer.getChildren().indexOf(headerBox);

                if (menusContainer.getChildren().contains(itemsPane)) {
                    menusContainer.getChildren().remove(itemsPane);
                    toggleArrow.setText("▼");
                } else {
                    menusContainer.getChildren().add(headerIndex + 1, itemsPane);
                    toggleArrow.setText("▲");
                }
            });
        }
    }
    private void shoMenus() {
        for (Menu menu : vendor.getMenus()) {
            HBox headerBox = new HBox(10);
            Label titleLabel = new Label(menu.getTitle());
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label toggleArrow = new Label("▼");
            toggleArrow.setStyle("-fx-font-size: 16px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            headerBox.getChildren().addAll(titleLabel, spacer, toggleArrow);

            FlowPane itemsPane = new FlowPane();
            itemsPane.setHgap(10);
            itemsPane.setVgap(10);
            itemsPane.setPadding(new Insets(5));
            itemsPane.setVisible(false);

            for (Item item : menu.getItems()) {
                VBox itemBox = new VBox(5);
                itemBox.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-background-radius: 5;");
                itemBox.setPrefWidth(180);

                Label nameLabel = new Label(item.getName());
                nameLabel.setStyle("-fx-font-weight: bold;");

                ImageView imageView = new ImageView();
                imageView.setFitWidth(150);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(item.getImageBase64());
                    imageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
                } catch (Exception e) {
                }

                Label descLabel = new Label(item.getDescription());
                descLabel.setWrapText(true);
                descLabel.setMaxWidth(170);

                Label priceLabel = new Label("قیمت: " + item.getPrice() + " تومان");

                HBox controls = new HBox(5);
                controls.setAlignment(CENTER_LEFT);

                Button plusBtn = new Button("+");
                Button minusBtn = new Button("-");
                Label countLabel = new Label();
                countLabel.setStyle("-fx-font-size: 14px;");
                countLabel.setVisible(false);
                minusBtn.setVisible(false);

                plusBtn.setOnAction(ev -> {
                    int count = countLabel.getText().isEmpty() ? 0 : Integer.parseInt(countLabel.getText());
                    count++;
                    countLabel.setText(String.valueOf(count));
                    countLabel.setVisible(true);
                    minusBtn.setVisible(true);
                });

                minusBtn.setOnAction(ev -> {
                    int count = Integer.parseInt(countLabel.getText());
                    count--;
                    if (count <= 0) {
                        countLabel.setText("");
                        countLabel.setVisible(false);
                        minusBtn.setVisible(false);
                    } else {
                        countLabel.setText(String.valueOf(count));
                    }
                });

                controls.getChildren().addAll(plusBtn, countLabel, minusBtn);

                itemBox.getChildren().addAll(nameLabel, imageView, descLabel, priceLabel, controls);
                itemsPane.getChildren().add(itemBox);
            }

            headerBox.setOnMouseClicked(event -> {
                boolean isVisible = itemsPane.isVisible();
                itemsPane.setVisible(!isVisible);
                toggleArrow.setText(isVisible ? "▼" : "▲");
            });

            menusContainer.getChildren().addAll(headerBox, itemsPane);
        }
    }

    private VBox createMenuCard(Menu menu, List<Item> items) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(600);

        Label titleLabel = new Label(menu.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button showItemsButton = new Button("نمایش آیتم‌ها");
        VBox itemsContainer = new VBox(15);
        itemsContainer.setVisible(false);

        showItemsButton.setOnAction(e -> {
            if (!itemsContainer.isVisible()) {
                itemsContainer.getChildren().clear();
                for (Item item : items) {
                    VBox itemCard = new VBox(10);
                    itemCard.setPadding(new Insets(10));
                    itemCard.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
                    itemCard.setPrefWidth(560);

                    Label nameLabel = new Label(item.getName());
                    nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                    Label descriptionLabel = new Label(item.getDescription());
                    descriptionLabel.setWrapText(true);

                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    imageView.setPreserveRatio(true);
                    try {
                        byte[] imageBytes = Base64.getDecoder().decode(item.getImageBase64());
                        imageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
                    } catch (Exception ex) {
                        System.out.println("خطا در بارگذاری عکس برای آیتم " + item.getId());
                    }

                    HBox controls = new HBox(10);
                    controls.setAlignment(CENTER_LEFT);

                    Button plusBtn = new Button("+");
                    Button minusBtn = new Button("-");
                    Label countLabel = new Label();
                    countLabel.setStyle("-fx-font-size: 14px;");
                    countLabel.setVisible(false);
                    minusBtn.setVisible(false);

                    plusBtn.setOnAction(ev -> {
                        int count = countLabel.getText().isEmpty() ? 0 : Integer.parseInt(countLabel.getText());
                        count++;
                        countLabel.setText(String.valueOf(count));
                        countLabel.setVisible(true);
                        minusBtn.setVisible(true);
                    });

                    minusBtn.setOnAction(ev -> {
                        int count = Integer.parseInt(countLabel.getText());
                        count--;
                        if (count <= 0) {
                            countLabel.setText("");
                            countLabel.setVisible(false);
                            minusBtn.setVisible(false);
                        } else {
                            countLabel.setText(String.valueOf(count));
                        }
                    });

                    controls.getChildren().addAll(plusBtn, countLabel, minusBtn);
                    itemCard.getChildren().addAll(nameLabel, imageView, descriptionLabel, controls);
                    itemsContainer.getChildren().add(itemCard);
                }
            }

            itemsContainer.setVisible(!itemsContainer.isVisible());
        });

        card.getChildren().addAll(titleLabel, showItemsButton, itemsContainer);
        return card;
    }
}