package com.chichifood.controller;

import com.chichifood.model.Item;
import com.chichifood.model.Restaurant;
import com.chichifood.model.User;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private VBox ordersBox, favoritesBox;

    @FXML
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
                        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                        JsonArray vendorsArray = jsonObject.getAsJsonArray("vendors");
                        for (JsonElement vendorElement : vendorsArray) {
                            JsonObject vendorJson = vendorElement.getAsJsonObject();
                            Restaurant restaurant = new Restaurant(
                                    vendorJson.get("id").getAsInt(),
                                    vendorJson.get("name").getAsString(),
                                    vendorJson.get("address").getAsString(),
                                    vendorJson.get("phone").getAsString(),
                                    vendorJson.get("logo").getAsString(),
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
//        favoriteButton.setOnAction(event -> {
//            BuyerNetwork.getFavoritesRestaurants(apiResponse -> {
//                Platform.runLater(() -> {
//                    int statusCode = apiResponse.getStatusCode();
//                    String body = apiResponse.getBody();
//
//                    if (statusCode == 200) {
//                        try {
//                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/FavoritesPanel.fxml"));
//                            Parent root = loader.load();
//
//                            FavoritesListController controller = loader.getController();
//
//                            // پارس کردن body و استخراج لیست رستوران‌ها
//                            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
//                            JsonArray favoritesArray = jsonObject.getAsJsonArray("favorites");
//
//                            List<Restaurant> favoriteRestaurants = new ArrayList<>();
//                            for (JsonElement element : favoritesArray) {
//                                JsonObject obj = element.getAsJsonObject();
//                                Restaurant r = new Restaurant(
//                                        obj.get("id").getAsInt(),
//                                        obj.get("name").getAsString(),
//                                        obj.get("address").getAsString(),
//                                        obj.get("phone").getAsString(),
//                                        obj.get("logo").getAsString(),
//                                        obj.get("tax_fee").getAsInt(),
//                                        obj.get("additional_fee").getAsInt()
//                                );
//                                favoriteRestaurants.add(r);
//                            }
//
//                            controller.setFavorites(favoriteRestaurants);
//
//                            Stage stage = new Stage();
//                            stage.setTitle("رستوران‌های مورد علاقه");
//                            stage.setScene(new Scene(root));
//                            stage.show();
//
//                            // بستن پنل قبلی
//                            ((Stage) favoriteButton.getScene().getWindow()).close();
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        // مثلا پیام خطا نمایش بده
//                        System.out.println("خطا در دریافت لیست مورد علاقه‌ها: " + body);
//                    }
//                });
//            });
//        });
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