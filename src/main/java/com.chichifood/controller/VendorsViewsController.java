package com.chichifood.controller;

import com.chichifood.model.Restaurant;
import com.chichifood.network.BuyerNetwork;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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
                logoView.setPreserveRatio(true);
                logoView.setStyle("-fx-cursor: hand;");

// افکت hover
                logoView.setOnMouseEntered(event -> {
                    logoView.setScaleX(1.1);
                    logoView.setScaleY(1.1);
                });
                logoView.setOnMouseExited(event -> {
                    logoView.setScaleX(1.0);
                    logoView.setScaleY(1.0);
                });

                logoView.setOnMouseClicked(event -> openVendorMenu(vendor));

                Button nameButton = new Button(vendor.getName());
                nameButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #333; -fx-cursor: hand;");
                nameButton.setPadding(new Insets(0)); // برای کم کردن padding دکمه که شبیه label بشه

                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), nameButton);
                scaleUp.setToX(1.2);
                scaleUp.setToY(1.2);

                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), nameButton);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);

                nameButton.setOnMouseEntered(event -> {
                    scaleDown.stop();
                    scaleUp.playFromStart();
                });

                nameButton.setOnMouseExited(event -> {
                    scaleUp.stop();
                    scaleDown.playFromStart();
                });

// کلیک رو
                // کلیک روی نام رستوران -> باز کردن صفحه منوی رستوران
                nameButton.setOnMouseClicked(event -> openVendorMenu(vendor));

                VBox infoBox = new VBox(5);
                infoBox.getChildren().addAll(
                        styledLabel("ID: " + vendor.getId()),
                        styledLabel("Address: " + vendor.getAddress()),
                        styledLabel("Phone: " + vendor.getPhone()),
                        styledLabel("Tax Fee: " + vendor.getTaxFee()),
                        styledLabel("Additional Fee: " + vendor.getAdditionalFee())
                );

                itemBox.getChildren().addAll(logoView,nameButton, infoBox);
                vendorsListBox.getChildren().add(itemBox);
            }
        });
    }

    private Label styledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px;");
        return label;
    }
    private void openVendorMenu(Restaurant vendor) {
        BuyerNetwork.getVendorMenus(vendor.getId(), fullVendor -> {
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

                    Stage currentStage = (Stage) vendorsListBox.getScene().getWindow();
                    currentStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}