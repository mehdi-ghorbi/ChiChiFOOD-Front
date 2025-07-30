package com.chichifood.controller;

import com.chichifood.model.Item;
import com.chichifood.model.Restaurant;
import com.chichifood.model.User;
import com.chichifood.network.ApiResponse;
import com.chichifood.network.BuyerNetwork;
import com.chichifood.network.NetworkService;
import com.chichifood.network.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.chichifood.network.SessionManager.showAlert;

public class BuyerPanelController {

    @FXML
    private TextField searchRestaurant, vendorKeywords;
    @FXML
    private TextField itemName, itemPrice, itemKeywords;
    @FXML
    private Button vendorSearchButton, itemSearchButton, favoriteButton, orderButton, profileButton;
    @FXML
    private VBox vendorsContainer;
    public void initialize() {
        profileButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/BuyerProfile.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("پروفایل");
                stage.setScene(new Scene(root));
                stage.show();
                ((Stage) profileButton.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        orderButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/OrderBuyer.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("سفارش ها");
                stage.setScene(new Scene(root));
                stage.show();
                ((Stage) orderButton.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        vendorSearchButton.setOnAction(event -> {
            if (!vendorKeywords.getText().contains(",")){
                showAlert("ورودی نامعتبر" , "کلمات کلیدی را با ',' از هم جدا کنید و اخر متن از ',' استفاده کنید. ");
            }
            String search = searchRestaurant.getText();
            List<String> keywords = Arrays.stream(vendorKeywords.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            BuyerNetwork.getVendorsList(search, keywords, apiResponse -> {
                Platform.runLater(() -> {
                    int statusCode = apiResponse.getStatusCode();
                    String body = apiResponse.getBody();

                    List<Restaurant> restaurantList = new ArrayList<>();
                    if (statusCode == 200) {
                        JsonArray vendorsArray = JsonParser.parseString(body).getAsJsonArray();

                        for (JsonElement vendorElement : vendorsArray) {
                            JsonObject vendorJson = vendorElement.getAsJsonObject();
                            Restaurant restaurant = new Restaurant(
                                    vendorJson.get("id").getAsInt(),
                                    vendorJson.get("name").getAsString(),
                                    vendorJson.get("address").getAsString(),
                                    vendorJson.get("phone").getAsString(),
                                    vendorJson.get("logoBase64").getAsString(),
                                    vendorJson.get("tax_fee").getAsInt(),
                                    vendorJson.get("additional_fee").getAsInt()
                            );
                            restaurantList.add(restaurant);
                        }

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/VendorsList.fxml"));
                            Parent root = loader.load();

                            VendorsViewsController controller = loader.getController();
                            controller.setVendors(restaurantList);

                            Stage stage = new Stage();
                            stage.setTitle("Vendors List");
                            stage.setScene(new Scene(root));
                            stage.show();
                            ((Stage) vendorSearchButton.getScene().getWindow()).close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        });
        itemSearchButton.setOnAction(event -> {
            if (!itemKeywords.getText().contains(",")){
                showAlert("ورودی نامعتبر" , "کلمات کلیدی را با ',' از هم جدا کنید و اخر متن از ',' استفاده کنید. ");
            }
            String name = itemName.getText();
            String price = itemPrice.getText();
            if (!isNumeric(price)) {
                showAlert("ورودی اشتباه", "لطفا برای قیمت عدد طبیعی وارد کنید.");
            };
            List<String> keywords = Arrays.stream(itemKeywords.getText().split(","))
                            .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                            .collect(Collectors.toList());
            BuyerNetwork.getItemsList(name, Integer.parseInt(price), keywords, apiResponse -> {
                Platform.runLater(() -> {
                    int statusCode = apiResponse.getStatusCode();
                    String body = apiResponse.getBody();

                    List<Item> itemList = new ArrayList<>();
                    if (statusCode == 200) {
                        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                        JsonArray itemsArray = jsonObject.getAsJsonArray("items");

                        for (JsonElement itemElement : itemsArray) {
                            JsonObject itemJson = itemElement.getAsJsonObject();
                            Item item = new Item(
                                    itemJson.get("id").getAsInt(),
                                    itemJson.get("name").getAsString(),
                                    itemJson.get("imageBase64").getAsString(),
                                    itemJson.get("description").getAsString(),
                                    itemJson.get("vendor_id").getAsInt(),
                                    itemJson.get("price").getAsInt(),
                                    itemJson.get("supply").getAsInt(),
                                    keywords
                            );
                            itemList.add(item);
                        }

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ItemsList.fxml"));
                            Parent root = loader.load();

                            ItemsListController controller = loader.getController();
                            controller.setItems(itemList);

                            Stage stage = new Stage();
                            stage.setTitle("Items List");
                            stage.setScene(new Scene(root));
                            stage.show();
                            ((Stage) itemSearchButton.getScene().getWindow()).close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        showAlert("ناموفق", "آیتمی پیدا نشد.");
                    }
                });
            });
        });
        favoriteButton.setOnAction(event -> {
            try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/FavoritesPanel.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("رستوران‌های مورد علاقه");
                stage.setScene(new Scene(root));
                stage.show();
                ((Stage) favoriteButton.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loadVendors("", new ArrayList<>());
    }
    public void loadVendors(String vendorName, List<String> keywords) {
        BuyerNetwork.getVendorsList(vendorName, keywords, new Consumer<ApiResponse>() {
            @Override
            public void accept(ApiResponse response) {
                if (response.getStatusCode() == 200) {
                    JsonArray vendorsArray = JsonParser.parseString(response.getBody()).getAsJsonArray();
                    List<JsonObject> sortedList = new ArrayList<>();

                    for (JsonElement element : vendorsArray) {
                        sortedList.add(element.getAsJsonObject());
                    }

                    sortedList.sort((a, b) -> {
                        double avgA = calculateAverageRating(a);
                        double avgB = calculateAverageRating(b);
                        return Double.compare(avgB, avgA); // نزولی
                    });

                    Platform.runLater(() -> {
                        vendorsContainer.getChildren().clear();
                        for (JsonObject vendorJson : sortedList) {
                            VBox vendorBox = createVendorBox(vendorJson);
                            vendorsContainer.getChildren().add(vendorBox);
                        }
                    });
                } else {
                    System.out.println("خطا در دریافت رستوران‌ها: " + response.getBody());
                }
            }
        });
    }
    private double calculateAverageRating(JsonObject vendorJson) {
        if (!vendorJson.has("ratings")) return 0.0;

        JsonArray ratingsArray = vendorJson.getAsJsonArray("ratings");
        if (ratingsArray.size() == 0) return 0.0;

        double sum = 0;
        int count = 0;
        for (JsonElement ratingElement : ratingsArray) {
            JsonObject ratingObj = ratingElement.getAsJsonObject();
            if (ratingObj.has("score")) {
                try {
                    sum += ratingObj.get("score").getAsDouble();
                    count++;
                } catch (Exception ignored) {}
            }
        }

        return count == 0 ? 0.0 : sum / count;
    }
    private VBox createVendorBox(JsonObject vendorJson) {
        String name = vendorJson.get("name").getAsString();
        String address = vendorJson.has("address") ? vendorJson.get("address").getAsString() : "بدون آدرس";
        double avgRating = calculateAverageRating(vendorJson);

        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #FF9800; -fx-border-radius: 8; -fx-background-color: #FFF3E0; -fx-background-radius: 8; -fx-padding: 15;");

        Label nameLabel = new Label(name);
        nameLabel.setFont(new Font(16));
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        Label addressLabel = new Label("آدرس: " + address);
        addressLabel.setStyle("-fx-text-fill: #666666;");

        Label ratingLabel = new Label("میانگین امتیاز: " + String.format("%.2f", avgRating));
        ratingLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 14;");

        Button viewMenuButton = new Button("مشاهده منو");
        viewMenuButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 6;");
        viewMenuButton.setOnAction(e -> {
            // نمایش منو
        });

        box.getChildren().addAll(nameLabel, addressLabel, ratingLabel, viewMenuButton);
        return box;
    }
    public boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}