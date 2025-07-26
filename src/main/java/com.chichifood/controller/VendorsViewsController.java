package com.chichifood.controller;

import com.chichifood.model.Restaurant;
import com.chichifood.network.BuyerNetwork;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class VendorsViewsController {

    @FXML
    private VBox vendorsListBox;

    @FXML
    private TextField vendorSearchField;

    @FXML
    private void handleSearchAction() {
        String vendorName = vendorSearchField.getText().trim();
        String keywords = ""; // اگر کلیدواژه داری، مقدارشو اینجا بگیر

        BuyerNetwork.getVendorsList(vendorName, keywords, apiResponse -> {
            if (apiResponse.getStatusCode() == 200) {
                try {
                    JsonObject jsonObject = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();
                    JsonArray vendorsArray = jsonObject.getAsJsonArray("vendors");

                    List<Restaurant> vendors = new ArrayList<>();
                    for (JsonElement el : vendorsArray) {
                        JsonObject vendorJson = el.getAsJsonObject();
                        Restaurant restaurant = new Restaurant(
                                vendorJson.get("id").getAsInt(),
                                vendorJson.get("name").getAsString(),
                                vendorJson.get("address").getAsString(),
                                vendorJson.get("phone").getAsString(),
                                vendorJson.get("logo").getAsString(),
                                vendorJson.get("tax_fee").getAsInt(),
                                vendorJson.get("additional_fee").getAsInt()
                        );

                    vendors.add(restaurant);
                    }

                    Platform.runLater(() -> {
                        vendorsListBox.getChildren().clear();
                        if (vendors.isEmpty()) {
                            vendorsListBox.getChildren().add(new Label("هیچ رستورانی پیدا نشد."));
                        } else {
                            for (Restaurant vendor : vendors) {
                                vendorsListBox.getChildren().add(createVendorItem(vendor));
                            }
                        }
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        vendorsListBox.getChildren().clear();
                        vendorsListBox.getChildren().add(new Label("خطا در پردازش داده‌ها."));
                    });
                }

            } else {
                Platform.runLater(() -> {
                    vendorsListBox.getChildren().clear();
                    vendorsListBox.getChildren().add(new Label("خطا: " + apiResponse.getBody()));
                });
            }
        });
    }

    private HBox createVendorItem(Restaurant vendor) {
        HBox itemBox = new HBox(15);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1;");

        ImageView logoView = new ImageView();
        try {
            byte[] imageBytes = Base64.getDecoder().decode(vendor.getLogoBase64());
            logoView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
        } catch (Exception e) {
            logoView.setImage(new Image("https://via.placeholder.com/80"));
        }
        logoView.setFitWidth(80);
        logoView.setFitHeight(80);

        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(
                new Label("ID: " + vendor.getId()),
                new Label("Name: " + vendor.getName()),
                new Label("Address: " + vendor.getAddress()),
                new Label("Phone: " + vendor.getPhone()),
                new Label("Tax Fee: " + vendor.getTaxFee()),
                new Label("Additional Fee: " + vendor.getAdditionalFee())
        );

        itemBox.getChildren().addAll(logoView, infoBox);

        return itemBox;
    }

    private List<Restaurant> vendors;

    public void setVendors(List<Restaurant> vendors) {
        this.vendors = vendors;
        loadVendors();
    }

    private void loadVendors() {
        Platform.runLater(() -> {
            vendorsListBox.getChildren().clear();

            if (vendors == null || vendors.isEmpty()) {
                vendorsListBox.getChildren().add(new Label("هیچ رستورانی پیدا نشد."));
                return;
            }

            for (Restaurant vendor : vendors) {
                HBox itemBox = new HBox(15);
                itemBox.setAlignment(Pos.CENTER_LEFT);
                itemBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1;");

                ImageView logoView = new ImageView();
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(vendor.getLogoBase64());
                    logoView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
                } catch (Exception e) {
                    logoView.setImage(new Image("https://via.placeholder.com/100"));
                }
                logoView.setFitWidth(80);
                logoView.setFitHeight(80);

                VBox infoBox = new VBox(5);
                infoBox.getChildren().addAll(
                        styledLabel("ID: " + vendor.getId()),
                        styledLabel("Name: " + vendor.getName()),
                        styledLabel("Address: " + vendor.getAddress()),
                        styledLabel("Phone: " + vendor.getPhone()),
                        styledLabel("Tax Fee: " + vendor.getTaxFee()),
                        styledLabel("Additional Fee: " + vendor.getAdditionalFee())
                );

                itemBox.getChildren().addAll(logoView, infoBox);
                vendorsListBox.getChildren().add(itemBox);
            }
        });
    }

    private Label styledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px;");
        return label;
    }

}