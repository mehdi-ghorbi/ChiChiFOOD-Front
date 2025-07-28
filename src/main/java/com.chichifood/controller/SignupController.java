package com.chichifood.controller;

import com.chichifood.model.User;
import com.chichifood.network.NetworkService;
import com.chichifood.network.SessionManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.chichifood.network.SessionManager.showAlert;

public class SignupController {

    @FXML private TextField FullNameTextFiled;
    @FXML private TextField PhoneTextFiled;
    @FXML private TextField EmailTextFiled;
    @FXML private TextField PassTextFiled;
    @FXML private TextField AddressTextFiled;
    @FXML private TextField BankNameTextFiled;
    @FXML private TextField AccuontNumberTextFiled;
    @FXML private RadioButton BuyerRadioButton;
    @FXML private RadioButton SellerRadioButton;
    @FXML private RadioButton CourierRadioButton;
    @FXML private RadioButton AdminRadioButton;
    private ToggleGroup roleButton = new ToggleGroup();
    @FXML private ImageView imageView;
    @FXML private Button ImageButton;
    @FXML private Button SignupButton;
    @FXML private Text loginText;

    private File selectedImageFile;

    @FXML
    public void initialize() {
        BuyerRadioButton.setToggleGroup(roleButton);
        SellerRadioButton.setToggleGroup(roleButton);
        CourierRadioButton.setToggleGroup(roleButton);
        AdminRadioButton.setToggleGroup(roleButton);

        ImageButton.setOnAction(e -> handleSelectImage());
        loginText.setOnMouseClicked(e -> goToLoginPage());
        SignupButton.setOnAction(e -> handleSignup());
    }

    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(ImageButton.getScene().getWindow());

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    private void goToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginText.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Panel");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSignup() {
        String fullName = FullNameTextFiled.getText();
        String phone = PhoneTextFiled.getText();
        String email = EmailTextFiled.getText();
        String password = PassTextFiled.getText();
        String address = AddressTextFiled.getText();
        String bankName = BankNameTextFiled.getText();
        String accountNumber = AccuontNumberTextFiled.getText();
        String role = ((RadioButton) roleButton.getSelectedToggle()).getText().toLowerCase();

        if (!validateForm()) {
            Platform.runLater(() ->
                    showAlert("Validation Error", "Please fill all fields and select an image.")
            );
            return;
        }

        String imagePath = selectedImageFile.getAbsolutePath().replace("\\", "/");

        User user = new User(fullName, phone, email, role, address, bankName, accountNumber, imagePath, password);

        System.out.println("Sending signup request...");
        NetworkService.signup(user, imagePath, apiResponse -> {
            Platform.runLater(() -> {
                System.out.println("Received signup response...");
                System.out.println(apiResponse.getBody());

                if (apiResponse.getStatusCode() == 200) {
                    JsonObject jsonObject = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();
                    String token = jsonObject.get("token").getAsString();
                    SessionManager.setToken(token);
                    SessionManager.setRole(user.getRole());
                    openCorrectPanel(user.getRole());
                } else {
                    showAlert(String.valueOf(apiResponse.getStatusCode()), "Message:\n" + apiResponse.getBody());
                }
            });
        });
    }


    private boolean validateForm() {
        return !isEmpty(FullNameTextFiled)
                && !isEmpty(PhoneTextFiled)
                && !isEmpty(EmailTextFiled)
                && !isEmpty(PassTextFiled)
                && !isEmpty(AddressTextFiled)
                && !isEmpty(BankNameTextFiled)
                && !isEmpty(AccuontNumberTextFiled)
                && roleButton.getSelectedToggle() != null
                && selectedImageFile != null;
    }

    private boolean isEmpty(TextField textField) {
        return textField.getText() == null || textField.getText().trim().isEmpty();
    }


    private void openCorrectPanel(String role) {
        String fxmlPath;
        switch (role.toLowerCase()) {
            case "admin":
                fxmlPath = "/Views/adminPanel.fxml";
                break;
            case "seller":
                fxmlPath = "/Views/sellerPanel.fxml";
                break;
            case "buyer":
                fxmlPath = "/Views/BuyerPanel.fxml";
                break;
            case "courier":
                fxmlPath = "/Views/courierPanel.fxml";
                break;
            default:
                showAlert("Unknown Role", "Role: " + role);
                return;
        }
        openPanel(fxmlPath, role);
    }

    private void openPanel(String fxmlPath, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage currentStage = (Stage) SignupButton.getScene().getWindow();
            currentStage.close();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle(role);
            newStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Cannot load the admin coupon panel.");
        }
    }
}
