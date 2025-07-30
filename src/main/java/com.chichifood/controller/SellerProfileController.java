package com.chichifood.controller;

import com.chichifood.model.User;
import com.chichifood.network.NetworkService;
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

public class SellerProfileController {

    @FXML private Circle profileCircle;
    @FXML private Label nameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label addressLabel;
    @FXML private Label bankNameLabel;
    @FXML private Label accountNumberLabel;
    @FXML private Button backBtn;
    @FXML private Button editBtn;

    public void initialize() {

        seedSampleData();
        backBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/sellerPanel.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) backBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Seller Profile");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("خطا", "مشکل در بارگذاری صفحه پروفایل");
            }
        });
        editBtn.setOnAction(event -> {
                editProfile();
        });
    }
    private void editProfile() {

        JsonObject json = new JsonObject();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("ویرایش پروفایل");
        dialog.setHeaderText("اطلاعات خود را ویرایش کنید:");

        ButtonType saveButtonType = new ButtonType("ذخیره", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField fullNameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
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

        TextField bankNameField = new TextField();
        TextField accountNumberField = new TextField();

        fullNameField.setPromptText(user.getFullName());
        phoneField.setPromptText(user.getPhone());
        emailField.setPromptText(user.getEmail());
        addressField.setPromptText(user.getAddress());
        profileImageField.setPromptText(user.getPhoto());
        bankNameField.setPromptText(user.getBankName());
        accountNumberField.setPromptText(user.getAccountNumber());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("نام کامل:"), 0, 0);
        grid.add(fullNameField, 1, 0);

        grid.add(new Label("تلفن:"), 0, 1);
        grid.add(phoneField, 1, 1);

        grid.add(new Label("ایمیل:"), 0, 2);
        grid.add(emailField, 1, 2);

        grid.add(new Label("آدرس:"), 0, 3);
        grid.add(addressField, 1, 3);

        grid.add(new Label("تصویر پروفایل:"), 0, 4);
        grid.add(new HBox(10, profileImageField, browseImageButton), 1, 4);

        grid.add(new Label("نام بانک:"), 0, 5);
        grid.add(bankNameField, 1, 5);

        grid.add(new Label("شماره حساب:"), 0, 6);
        grid.add(accountNumberField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!fullNameField.getText().isBlank()) {
                    json.addProperty("full_name", fullNameField.getText().trim());
                }
                if (!phoneField.getText().isBlank()) {
                    json.addProperty("phone", phoneField.getText().trim());
                }
                if (!emailField.getText().isBlank()) {
                    json.addProperty("email", emailField.getText().trim());
                }
                if (!addressField.getText().isBlank()) {
                    json.addProperty("address", addressField.getText().trim());
                }
                if (!profileImageField.getText().isBlank()) {
                    json.addProperty("profileImageBase64", profileImageField.getText().trim());
                }

                JsonObject bankInfo = new JsonObject();
                if (!bankNameField.getText().isBlank()) {
                    bankInfo.addProperty("bank_name", bankNameField.getText().trim());
                }
                if (!accountNumberField.getText().isBlank()) {
                    bankInfo.addProperty("account_number", accountNumberField.getText().trim());
                }
                if (!bankInfo.entrySet().isEmpty()) {
                    json.add("bank_info", bankInfo);
                }

                NetworkService.updateProfile(json, apiResponse -> {
                    Platform.runLater(() -> {
                        if (apiResponse.getStatusCode() == 200) {
                            showAlert("موفقیت", "پروفایل با موفقیت به‌روزرسانی شد.");
                            seedSampleData();
                        } else {
                            showAlert(String.valueOf(apiResponse.getStatusCode()),   apiResponse.getBody());
                        }
                    });
                });

            }
            return null;
        });

        dialog.showAndWait();
    }
    public static User user;
    public void seedSampleData() {
        NetworkService.getProfile(apiResponse -> {
            JsonObject json = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();

            String fullName = json.has("name") ? json.get("name").getAsString() : "";
            String phone = json.has("phone") ? json.get("phone").getAsString() : "";
            String email = json.has("email") ? json.get("email").getAsString() : "";
            String role = json.has("role") ? json.get("role").getAsString() : "";
            String address = json.has("address") ? json.get("address").getAsString() : "";
            String bankName = json.has("bankName") ? json.get("bankName").getAsString() : "";
            String accountNumber = json.has("AccountNumber") ? json.get("AccountNumber").getAsString() : "";
            String imagePath = json.has("profileImageBase64") ? json.get("profileImageBase64").getAsString() : "";
            user = new User(fullName,phone,email,role,address,bankName,accountNumber,imagePath);
            Platform.runLater(() -> {
                nameLabel.setText(fullName);
                phoneLabel.setText("تلفن: " + phone);
                emailLabel.setText("ایمیل: " + email);
                roleLabel.setText("نقش: " + role);
                addressLabel.setText("آدرس: " + address);
                bankNameLabel.setText("بانک: " + bankName);
                accountNumberLabel.setText("شماره حساب: " + accountNumber);

                if (!imagePath.isEmpty()) {
                    File file = new File(imagePath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        profileCircle.setFill(new ImagePattern(image));
                    }
                }
            });
        });
    }
}
