//package com.chichifood.controller;
//
//import com.chichifood.model.Item;
//import com.chichifood.model.Restaurant;
//import com.chichifood.model.User;
//import com.chichifood.network.BuyerNetwork;
//import com.chichifood.network.NetworkService;
//import com.chichifood.network.SessionManager;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.chichifood.network.SessionManager.showAlert;
//
//public class BuyerPanelController {
//
//    @FXML
//    private TextField searchRestaurant, vendorKeywords;
//    @FXML
//    private TextField itemName, itemPrice, itemKeywords;
//    @FXML
//    private Button vendorSearchButton, itemSearchButton, favoriteButton, orderButton;
//    @FXML
//    private VBox ordersBox, favoritesBox;
//
//    @FXML
//    public void initialize() {
//orderButton.setOnAction(event -> {
//
//    try {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/orderPanel.fxml"));
//        Parent root = loader.load();
//
//
//
//        Stage stage = new Stage();
//        stage.setTitle("Orders");
//        stage.setScene(new Scene(root));
//        stage.show();
//        ((Stage) vendorSearchButton.getScene().getWindow()).close();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//});
//        vendorSearchButton.setOnAction(event -> {
//            String search = searchRestaurant.getText();
//            List<String> keywords = Arrays.stream(vendorKeywords.getText().split(","))
//                    .map(String::trim)
//                    .filter(s -> !s.isEmpty())
//                    .collect(Collectors.toList());
//            BuyerNetwork.getVendorsList(search, keywords, apiResponse -> {
//                Platform.runLater(() -> {
//                    int statusCode = apiResponse.getStatusCode();
//                    String body = apiResponse.getBody();
//
//                    List<Restaurant> restaurantList = new ArrayList<>();
//                    if (statusCode == 200) {
//                        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
//                        JsonArray vendorsArray = jsonObject.getAsJsonArray("vendors");
//                        for (JsonElement vendorElement : vendorsArray) {
//                            JsonObject vendorJson = vendorElement.getAsJsonObject();
//                            Restaurant restaurant = new Restaurant(
//                                    vendorJson.get("id").getAsInt(),
//                                    vendorJson.get("name").getAsString(),
//                                    vendorJson.get("address").getAsString(),
//                                    vendorJson.get("phone").getAsString(),
//                                    vendorJson.get("logo").getAsString(),
//                                    vendorJson.get("tax_fee").getAsInt(),
//                                    vendorJson.get("additional_fee").getAsInt()
//                            );
//                            restaurantList.add(restaurant);
//                        }
//
//                        try {
//                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/VendorsList.fxml"));
//                            Parent root = loader.load();
//
//                            VendorsViewsController controller = loader.getController();
//                            controller.setVendors(restaurantList);
//
//                            Stage stage = new Stage();
//                            stage.setTitle("Vendors List");
//                            stage.setScene(new Scene(root));
//                            stage.show();
//                            ((Stage) vendorSearchButton.getScene().getWindow()).close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            });
//        });
//        itemSearchButton.setOnAction(event -> {
//            String name = itemName.getText();
//            String price = itemPrice.getText();
//            List<String> keywords = Arrays.stream(itemKeywords.getText().split(","))
//                            .map(String::trim)
//                                    .filter(s -> !s.isEmpty())
//                                            .collect(Collectors.toList());
//            BuyerNetwork.getItemsList(name, Integer.parseInt(price), keywords, apiResponse -> {
//                Platform.runLater(() -> {
//                    int statusCode = apiResponse.getStatusCode();
//                    String body = apiResponse.getBody();
//
//                    List<Item> itemList = new ArrayList<>();
//                    if (statusCode == 200) {
//                        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
//                        JsonArray vendorsArray = jsonObject.getAsJsonArray("vendors");
//                        for (JsonElement itemElement : vendorsArray) {
//                            JsonObject itemJson = itemElement.getAsJsonObject();
//                            Item item = new Item(
//                                    itemJson.get("id").getAsInt(),
//                                    itemJson.get("name").getAsString(),
//                                    itemJson.get("imageBase64").getAsString(),
//                                    itemJson.get("description").getAsString(),
//                                    itemJson.get("vendorId").getAsInt(),
//                                    itemJson.get("price").getAsInt(),
//                                    itemJson.get("supply").getAsInt(),
//                                    keywords
//                            );
//                            itemList.add(item);
//                        }
//
//                        try {
//                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ItemsList.fxml"));
//                            Parent root = loader.load();
//
//                            ItemsListController controller = loader.getController();
//                            controller.setItems(itemList);
//
//                            Stage stage = new Stage();
//                            stage.setTitle("Items List");
//                            stage.setScene(new Scene(root));
//                            stage.show();
//                            ((Stage) itemSearchButton.getScene().getWindow()).close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            });
//        });
//    }
//
//}