package com.chichifood.controller;

import com.chichifood.model.Restaurant;
import com.chichifood.network.NetworkService;
import com.chichifood.network.RestaurantNetwork;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.chichifood.network.SessionManager.showAlert;

public class RestaurantProfileController {

    @FXML private Circle profileCircle;
    @FXML private Label nameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Button backBtn;
    @FXML private Button editBtn;

    private Restaurant restaurant;

    public void initialize() {
        seedSampleData();

        backBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/sellerPanel.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) backBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Restaurant Panel");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("خطا", "مشکل در بارگذاری صفحه پنل رستوران");
            }
        });

        editBtn.setOnAction(event -> editProfile());
    }

    private void editProfile() {
        JsonObject json = new JsonObject();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("ویرایش پروفایل رستوران");
        dialog.setHeaderText("اطلاعات خود را ویرایش کنید:");

        ButtonType saveButtonType = new ButtonType("ذخیره", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        TextField profileImageField = new TextField();
        profileImageField.setEditable(false);

        Button browseImageButton = new Button("انتخاب عکس");
        browseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("انتخاب فایل پروفایل");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (selectedFile != null) {
                profileImageField.setText(selectedFile.getAbsolutePath());
            }
        });

        nameField.setPromptText(restaurant.getName());
        phoneField.setPromptText(restaurant.getPhone());
        addressField.setPromptText(restaurant.getAddress());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("نام رستوران:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("تلفن:"), 0, 1);
        grid.add(phoneField, 1, 1);

        grid.add(new Label("آدرس:"), 0, 2);
        grid.add(addressField, 1, 2);

        grid.add(new Label("تصویر پروفایل:"), 0, 3);
        grid.add(new HBox(10, profileImageField, browseImageButton), 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!nameField.getText().isBlank()) {
                    json.addProperty("name", nameField.getText().trim());
                }
                if (!phoneField.getText().isBlank()) {
                    json.addProperty("phone", phoneField.getText().trim());
                }
                if (!addressField.getText().isBlank()) {
                    json.addProperty("address", addressField.getText().trim());
                }
                if (!profileImageField.getText().isBlank()) {
                    json.addProperty("profileImageBase64", profileImageField.getText().trim());
                }

                RestaurantNetwork.updateRestaurant(json, resID, apiResponse -> {
                    Platform.runLater(() -> {
                        if (apiResponse.getStatusCode() == 200) {
                            showAlert("موفقیت", "پروفایل با موفقیت به‌روزرسانی شد.");
                            seedSampleData();
                        } else {
                            showAlert(String.valueOf(apiResponse.getStatusCode()), apiResponse.getBody());
                        }
                    });
                });
            }
            return null;
        });

        dialog.showAndWait();
    }
    public static String resID;
    public void seedSampleData() {
        RestaurantNetwork.getRestaurants(apiResponse -> {
            Platform.runLater(() -> {
                try {
                    JsonArray jsonArray = JsonParser.parseString(apiResponse.getBody()).getAsJsonArray();

                    if (!jsonArray.isEmpty()) {
                        JsonObject json = jsonArray.get(0).getAsJsonObject();
                        resID = json.get("id").getAsString();
                        String name = json.has("name") ? json.get("name").getAsString() : "";
                        String phone = json.has("phone") ? json.get("phone").getAsString() : "";
                        String address = json.has("address") ? json.get("address").getAsString() : "";
                        String imagePath = json.has("logoBase64") ? json.get("logoBase64").getAsString() : ""; // در اصل مسیر فایل

                        restaurant = new Restaurant(name, address, phone, imagePath);

                        nameLabel.setText(name);
                        phoneLabel.setText("تلفن: " + phone);
                        addressLabel.setText("آدرس: " + address);

                        if (!imagePath.isEmpty()) {
                            File file = new File(imagePath);
                            if (file.exists()) {
                                Image image = new Image(file.toURI().toString());
                                profileCircle.setFill(new ImagePattern(image));
                            } else {
                                System.out.println("فایل لوگو یافت نشد: " + imagePath);
                            }
                        }
                    } else {
                        showAlert("خطا", "رستورانی یافت نشد.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("خطا", "خطا در خواندن اطلاعات رستوران.");
                }
            });
        });
    }

}
