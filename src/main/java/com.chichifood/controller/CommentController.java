package com.chichifood.controller;

import com.chichifood.network.ApiResponse;
import com.chichifood.network.BuyerNetwork;
import com.chichifood.network.SessionManager;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;

public class CommentController {

    @FXML
    private VBox ratingsContainer;

    @FXML
    private Button homeButton, backButton;

    @FXML
    public void initialize() {

        loadRatings();
    }

    private void loadRatings() {
        List<String> keywords = List.of();
        String vendorName = "";

        BuyerNetwork.getVendorsList(vendorName, keywords, response -> {
            if (response.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    JsonObject responseJson = JsonParser.parseString(response.getBody()).getAsJsonObject();
                    if (responseJson.has("ratings") && responseJson.get("ratings").isJsonArray()) {
                        JsonArray ratingsArray = responseJson.getAsJsonArray("ratings");
                        for (JsonElement element : ratingsArray) {
                            JsonObject rating = element.getAsJsonObject();
                            addRatingRow(rating);
                        }
                    }
                });
            } else {
                System.out.println("Error loading ratings: " + response.getBody());
            }
        });
    }

    private void addRatingRow(JsonObject rating) {
        String base64Image = rating.has("imageBase64") ? rating.get("imageBase64").getAsString() : "";
        String comment = rating.has("comment") ? rating.get("comment").getAsString() : "";
        int score = rating.has("score") ? rating.get("score").getAsInt() : 0;
        String restaurantName = rating.has("restaurantName") ? rating.get("restaurantName").getAsString() : "";
        String itemName = rating.has("itemName") ? rating.get("itemName").getAsString() : "";

        HBox row = new HBox(15);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5;");

        ImageView imageView = new ImageView();
        if (!base64Image.isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            imageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
        }
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        VBox commentBox = new VBox(5);
        commentBox.getChildren().addAll(
                new Label("💬 " + comment),
                new Label("⭐ امتیاز: " + score)
        );

        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(
                new Label("🍽 رستوران: " + restaurantName),
                new Label("🥗 آیتم: " + itemName)
        );

        row.getChildren().addAll(imageView, commentBox, infoBox);
        ratingsContainer.getChildren().add(row);
    }
}