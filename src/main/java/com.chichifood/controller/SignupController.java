    package com.chichifood.controller;

    import com.chichifood.model.User;
    import com.chichifood.network.NetworkService;
    import com.chichifood.network.SessionManager;
    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.image.*;
    import javafx.scene.text.Text;
    import javafx.stage.FileChooser;
    import javafx.stage.Stage;

    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.*;
    import java.util.Base64;

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
         private ToggleGroup roleButton= new ToggleGroup();
        @FXML private ImageView imageView;
        @FXML private Button ImageButton;
        @FXML private Button SignupButton;
        @FXML private Text loginText;

        private File selectedImageFile;
        private String base64Image;

        @FXML
        public void initialize() {
            BuyerRadioButton.setToggleGroup(roleButton);
            SellerRadioButton.setToggleGroup(roleButton);
            CourierRadioButton.setToggleGroup(roleButton);
            AdminRadioButton.setToggleGroup(roleButton);
            ImageButton.setOnAction(e -> handleSelectImage());
            loginText.setOnMouseClicked(e -> goToLoginPage());
            SignupButton.setOnAction(this::handleSignup);
        }

        private void handleSelectImage() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            selectedImageFile = fileChooser.showOpenDialog(ImageButton.getScene().getWindow());

            if (selectedImageFile != null) {
                Image image = new Image(selectedImageFile.toURI().toString());
                imageView.setImage(image);
                base64Image = imageToBase64(selectedImageFile);
            }
        }

        private String imageToBase64(File file) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                return Base64.getEncoder().encodeToString(baos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
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

        public void handleSignup(ActionEvent e) {
            String fullName = FullNameTextFiled.getText();
            String phone = PhoneTextFiled.getText();
            String email = EmailTextFiled.getText();
            String password = PassTextFiled.getText();
            String address = AddressTextFiled.getText();
            String bankName = BankNameTextFiled.getText();
            String accountNumber = AccuontNumberTextFiled.getText();
            System.out.println(roleButton.getSelectedToggle().toString());
            String role = ((RadioButton) roleButton.getSelectedToggle()).getText().toLowerCase();
            if (!validateForm()) {
                showAlert("Validation Error", "Please fill all fields and select an image.");
                return;
            }
            User user = new User(fullName, phone, email, password, role, address, bankName, accountNumber);

            NetworkService.signup(user, base64Image, apiResponse -> {
                System.out.println(apiResponse.getStatusCode());
                if (apiResponse.getStatusCode() == 200 ) {
                    System.out.println(apiResponse.getBody());
                    JsonObject jsonObject = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();
                    String token = jsonObject.get("token").getAsString();
                    SessionManager.setToken(token);
                   SessionManager.setRole(user.getRole());

                    openCorrectPanel(user.getRole());
                   showAlert("Success", "Registration successful!");
                } else {
                    showAlert(String.valueOf(apiResponse.getStatusCode()),  "Message:\n" + apiResponse.getBody());
                }
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
                    && base64Image != null;
        }

        private boolean isEmpty(TextField textField) {
            return textField.getText() == null || textField.getText().trim().isEmpty();
        }

        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
        private void openCorrectPanel(String role) {
            String fxmlPath = "";
            switch (role.toLowerCase()) {
                case "admin":
                    fxmlPath = "/Views/adminPanel.fxml";
                    openPanel(fxmlPath,role);
                    break;
                case "seller":
                    fxmlPath = "/Views/sellerPanel.fxml";
                    openPanel(fxmlPath,role);
                    break;
                case "buyer":
                    fxmlPath = "/Views/BuyerPanel.fxml";
                    openPanel(fxmlPath,role);
                    break;
                case "courier":
                    fxmlPath = "/Views/courierPanel.fxml";
                    openPanel(fxmlPath,role);
                    break;
                default:
                    showAlert("Unknown Role", "Role: " + role);
                    return;
            }
        }
        private void openPanel(String fxmlPath, String role) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Stage stage = (Stage) SignupButton.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle(role + "Panel");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Cannot load your panel.");
            }
        }
    }
