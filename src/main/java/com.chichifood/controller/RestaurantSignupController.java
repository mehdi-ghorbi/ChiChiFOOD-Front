package com.chichifood.controller;

import com.chichifood.network.RestaurantNetwork;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.chichifood.network.SessionManager.showAlert;

public class RestaurantSignupController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField taxField;

    @FXML
    private TextField serviceFeeField;

    @FXML
    private Button selectImageButton;

    @FXML
    private Button backButton;

    @FXML
    private ImageView imagePreview;

    @FXML
    private Button signupButton;

    private File selectedImageFile;

    @FXML
    public void initialize() {
        selectImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("انتخاب تصویر رستوران");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            selectedImageFile = fileChooser.showOpenDialog(selectImageButton.getScene().getWindow());

            if (selectedImageFile != null) {
                Image image = new Image(selectedImageFile.toURI().toString());
                imagePreview.setImage(image);
            }
        });

        signupButton.setOnAction(e -> handleSignup());
        backButton.setOnAction(e -> {
            try {
                Parent sellerPanel = FXMLLoader.load(getClass().getResource("/Views/SellerPanel.fxml"));
                Scene scene = new Scene(sellerPanel);
                Stage stage = (Stage) signupButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("خطا", "مشکل در باز کردن صفحه قبل.");
            }
        });
    }

    private void handleSignup() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String tax = taxField.getText().trim();
        String serviceFee = serviceFeeField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || selectedImageFile == null) {
            showAlert("خطا", "لطفاً فیلد های اجباری  را پر کرده و تصویر را انتخاب کنید.");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("address", address);
        json.addProperty("phone", phone);
        json.addProperty("logoBase64", selectedImageFile.getAbsolutePath());
        json.addProperty("tax_fee", tax.isEmpty() ? 0 : Integer.parseInt(tax));
        json.addProperty("additional_fee", serviceFee.isEmpty() ? 0 : Integer.parseInt(serviceFee));
        RestaurantNetwork.signupRestaurant(json, apiResponse -> {
            Platform.runLater(() -> {
                if (apiResponse.getStatusCode() == 200) {
                    try {
                        showAlert(String.valueOf(apiResponse.getStatusCode()), "ثبت نام انجام شد، منتظر تایید ادمین بمانید.");
                        Parent sellerPanel = FXMLLoader.load(getClass().getResource("/Views/SellerPanel.fxml"));
                        Scene scene = new Scene(sellerPanel);
                        Stage stage = (Stage) signupButton.getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("خطا", "مشکل در باز کردن صفحه قبل.");
                    }
                } else {
                    showAlert(String.valueOf(apiResponse.getStatusCode()), apiResponse.getBody());
                }
            });
        });


    }


}
