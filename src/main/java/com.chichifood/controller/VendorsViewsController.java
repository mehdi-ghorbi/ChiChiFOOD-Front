package com.chichifood.controller;

import com.chichifood.model.Restaurant;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class VendorsViewsController {

    @FXML
    private VBox vendorsListBox;

    @FXML
    private Button backButton;

    private List<Restaurant> vendors;

    @FXML
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
                itemBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1;");
                itemBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

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