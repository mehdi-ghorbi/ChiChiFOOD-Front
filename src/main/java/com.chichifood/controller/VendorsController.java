//package com.chichifood.controller;
//
//import com.chichifood.model.Restaurant;
//import javafx.fxml.FXML;
//import javafx.geometry.Pos;
//import javafx.scene.control.*;
//import javafx.scene.image.*;
//import javafx.scene.layout.*;
//import javafx.scene.text.Font;
//
//import java.io.ByteArrayInputStream;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.List;
//
//public class VendorsController {
//
//    @FXML
//    private TextField vendorSearchField;
//
//    @FXML
//    private VBox vendorsListBox;
//
//    @FXML
//    private void initialize() {
//        // وقتی صفحه باز میشه، یه بار همه رستوران‌ها لود شن
//        loadVendors("");
//    }
//
//    @FXML
//    private void onVendorSearch() {
//        String keywords = vendorSearchField.getText().trim();
//        if (keywords.isEmpty()) {
//            showError("Please enter at least one keyword.");
//            return;
//        }
//
//        // سرچ بر اساس کلمات کلیدی
//        loadVendors(keywords);
//    }
//
//    private void loadVendors(String keywords) {
//        // پاک کردن لیست قبلی
//        vendorsListBox.getChildren().clear();
//
//        // گرفتن دیتا از سرور یا فعلاً mock data
//        List<Restaurant> vendors = fetchVendors(keywords);
//
//        if (vendors.isEmpty()) {
//            vendorsListBox.getChildren().add(new Label("No vendors found."));
//            return;
//        }
//
//        for (Restaurant vendor : vendors) {
//            HBox itemBox = new HBox(15);
//            itemBox.setAlignment(Pos.CENTER_LEFT);
//            itemBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1;");
//
//            // عکس لوگو
//            ImageView logoView = new ImageView();
//            try {
//                byte[] imageBytes = Base64.getDecoder().decode(vendor.getLogoBase64());
//                logoView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
//            } catch (Exception e) {
//                // عکس پیش‌فرض در صورت ارور
//                logoView.setImage(new Image("https://via.placeholder.com/100"));
//            }
//            logoView.setFitWidth(80);
//            logoView.setFitHeight(80);
//
//            // اطلاعات
//            VBox infoBox = new VBox(5);
//            infoBox.getChildren().addAll(
//                    styledLabel("ID: " + vendor.getId()),
//                    styledLabel("Name: " + vendor.getName()),
//                    styledLabel("Address: " + vendor.getAddress()),
//                    styledLabel("Phone: " + vendor.getPhone()),
//                    styledLabel("Tax Fee: " + vendor.getTaxFee()),
//                    styledLabel("Additional Fee: " + vendor.getAdditionalFee())
//            );
//
//            itemBox.getChildren().addAll(logoView, infoBox);
//            vendorsListBox.getChildren().add(itemBox);
//        }
//    }
//
//    private Label styledLabel(String text) {
//        Label label = new Label(text);
//        label.setFont(Font.font("Arial", 13));
//        return label;
//    }
//
//    private void showError(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Search Error");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    // شبیه‌سازی گرفتن دیتا از سرور
//    private List<Restaurant> fetchVendors(String keywords) {
//        List<Restaurant> vendors = new ArrayList<>();
//
//        // اینجا بعداً از سرور واقعی می‌گیری
//        //vendors.add(new Restaurant(1, "Pizza King", "123 Street", "09121234567", SAMPLE_IMAGE_BASE64, 9.0, 2.5));
//       // vendors.add(new Restaurant(2, "Grill House", "456 Road", "09337654321", SAMPLE_IMAGE_BASE64, 10.0, 3.0));
//
//        return vendors;
//    }
//
//    // تصویر نمونه (Base64 کوچک)
//    private static final String SAMPLE_IMAGE_BASE64 =
//            "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAA...";
//}