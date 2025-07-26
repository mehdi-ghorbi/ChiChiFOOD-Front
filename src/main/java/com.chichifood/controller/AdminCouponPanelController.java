package com.chichifood.controller;

import com.chichifood.model.Coupon;
import com.chichifood.network.AdminNetwork;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.chichifood.network.SessionManager.showAlert;

public class AdminCouponPanelController {

    @FXML
    private TableView<Coupon> couponTable;

    @FXML private TableColumn<Coupon, Long> idColumn;
    @FXML private TableColumn<Coupon, String> codeColumn;
    @FXML private TableColumn<Coupon, String> typeColumn;
    @FXML private TableColumn<Coupon, Integer> valueColumn;
    @FXML private TableColumn<Coupon, Integer> minPriceColumn;
    @FXML private TableColumn<Coupon, Integer> userCountColumn;
    @FXML private TableColumn<Coupon, String> startDateColumn;
    @FXML private TableColumn<Coupon, String> endDateColumn;

    @FXML private Button addCouponsBtn;
    @FXML private Button editCouponsBtn;
    @FXML private Button removeCouponsBtn;
    @FXML private Button backBtn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        minPriceColumn.setCellValueFactory(new PropertyValueFactory<>("minPrice"));
        userCountColumn.setCellValueFactory(new PropertyValueFactory<>("userCount"));
        typeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType().name())
        );

        startDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStartDate().toString())
        );

        endDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEndDate().toString())
        );
        seedSampleData();
        backBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/adminPanel.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) backBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("AdminPanel");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "Cannot load the admin panel.");
            }
        });

        addCouponsBtn.setOnAction(event -> {
            addCoupon();
        });
        editCouponsBtn.setOnAction(event -> {
            editCoupon();
        });

        removeCouponsBtn.setOnAction(event -> {
            Coupon selectedCoupon = couponTable.getSelectionModel().getSelectedItem();

            if (selectedCoupon == null) {
                showAlert("No Selection", "Please select a coupon to remove.");
                return;
            }

            AdminNetwork.deleteCoupon(selectedCoupon.getId(), response -> {
                if (response.getStatusCode() == 200) {
                    Platform.runLater(() -> {
                        couponTable.getItems().remove(selectedCoupon);
                        showAlert("Success", "Coupon deleted successfully.");
                    });
                } else {
                    Platform.runLater(() -> showAlert(String.valueOf(response.getStatusCode()), response.getBody()));
                }
            });
        });

    }

    private void editCoupon() {
        Coupon selectedCoupon = couponTable.getSelectionModel().getSelectedItem();

        if (selectedCoupon == null) {
            showAlert("خطا", "لطفاً یک کوپن برای ویرایش انتخاب کنید.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("ویرایش کوپن");
        dialog.setHeaderText("فقط فیلدهای تغییریافته ارسال می‌شوند:");

        ButtonType saveButtonType = new ButtonType("ذخیره", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField codeField = new TextField(selectedCoupon.getCode());
        ChoiceBox<String> typeChoice = new ChoiceBox<>(FXCollections.observableArrayList("FIXED", "PERCENTAGE"));
        typeChoice.setValue(selectedCoupon.getType().name());

        TextField valueField = new TextField(String.valueOf(selectedCoupon.getValue()));
        TextField minPriceField = new TextField(String.valueOf(selectedCoupon.getMinPrice()));
        TextField userCountField = new TextField(String.valueOf(selectedCoupon.getUserCount()));
        TextField startDateField = new TextField(selectedCoupon.getStartDate().toString());
        TextField endDateField = new TextField(selectedCoupon.getEndDate().toString());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("کد کوپن:"), 0, 0); grid.add(codeField, 1, 0);
        grid.add(new Label("نوع:"), 0, 1); grid.add(typeChoice, 1, 1);
        grid.add(new Label("مقدار:"), 0, 2); grid.add(valueField, 1, 2);
        grid.add(new Label("حداقل قیمت:"), 0, 3); grid.add(minPriceField, 1, 3);
        grid.add(new Label("تعداد استفاده:"), 0, 4); grid.add(userCountField, 1, 4);
        grid.add(new Label("تاریخ شروع:"), 0, 5); grid.add(startDateField, 1, 5);
        grid.add(new Label("تاریخ پایان:"), 0, 6); grid.add(endDateField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    JSONObject json = new JSONObject();

                    // بررسی تغییرات
                    if (!codeField.getText().trim().equals(selectedCoupon.getCode())) {
                        json.put("coupon_code", codeField.getText().trim());
                    }

                    if (!typeChoice.getValue().equalsIgnoreCase(selectedCoupon.getType().name())) {
                        json.put("type", typeChoice.getValue().toLowerCase());
                    }

                    if (Integer.parseInt(valueField.getText().trim()) != selectedCoupon.getValue()) {
                        json.put("value", Integer.parseInt(valueField.getText().trim()));
                    }

                    if (Integer.parseInt(minPriceField.getText().trim()) != selectedCoupon.getMinPrice()) {
                        json.put("min_price", Integer.parseInt(minPriceField.getText().trim()));
                    }

                    if (Integer.parseInt(userCountField.getText().trim()) != selectedCoupon.getUserCount()) {
                        json.put("user_count", Integer.parseInt(userCountField.getText().trim()));
                    }

                    if (!startDateField.getText().trim().equals(selectedCoupon.getStartDate().toString())) {
                        json.put("start_date", startDateField.getText().trim());
                    }

                    if (!endDateField.getText().trim().equals(selectedCoupon.getEndDate().toString())) {
                        json.put("end_date", endDateField.getText().trim());
                    }

                    if (json.isEmpty()) {
                        showAlert("بدون تغییر", "هیچ فیلدی تغییر نکرده است.");
                        return null;
                    }

                    // ارسال به سرور
                    AdminNetwork.updateCoupon(selectedCoupon.getId(), json, apiResponse -> {
                        Platform.runLater(() -> {
                            if (apiResponse.getStatusCode() == 200) {
                                seedSampleData();
                                showAlert("موفقیت", "کوپن با موفقیت ویرایش شد.");
                            } else {
                                showAlert("خطا " + apiResponse.getStatusCode(), apiResponse.getBody());
                            }
                        });
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("خطا", "مقادیر وارد شده نامعتبر هستند.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void addCoupon() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("افزودن کوپن جدید");
        dialog.setHeaderText("لطفاً اطلاعات کوپن را وارد کنید:");

        ButtonType addButtonType = new ButtonType("افزودن", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // فیلدهای ورودی
        TextField codeField = new TextField();
        codeField.setPromptText("کد کوپن");

        ChoiceBox<String> typeChoice = new ChoiceBox<>(FXCollections.observableArrayList("FIXED", "PERCENTAGE"));
        typeChoice.getSelectionModel().selectFirst();

        TextField valueField = new TextField();
        valueField.setPromptText("مقدار تخفیف");

        TextField minPriceField = new TextField();
        minPriceField.setPromptText("حداقل خرید");

        TextField userCountField = new TextField();
        userCountField.setPromptText("تعداد دفعات استفاده");

        TextField startDateField = new TextField();
        startDateField.setPromptText("تاریخ شروع (مثلاً 2025-07-26)");

        TextField endDateField = new TextField();
        endDateField.setPromptText("تاریخ پایان (مثلاً 2025-08-01)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("کد کوپن:"), 0, 0); grid.add(codeField, 1, 0);
        grid.add(new Label("نوع:"), 0, 1); grid.add(typeChoice, 1, 1);
        grid.add(new Label("مقدار:"), 0, 2); grid.add(valueField, 1, 2);
        grid.add(new Label("حداقل قیمت:"), 0, 3); grid.add(minPriceField, 1, 3);
        grid.add(new Label("تعداد استفاده:"), 0, 4); grid.add(userCountField, 1, 4);
        grid.add(new Label("تاریخ شروع:"), 0, 5); grid.add(startDateField, 1, 5);
        grid.add(new Label("تاریخ پایان:"), 0, 6); grid.add(endDateField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String code = codeField.getText().trim();
                    String type = typeChoice.getValue().toLowerCase();
                    int value = Integer.parseInt(valueField.getText().trim());
                    int minPrice = Integer.parseInt(minPriceField.getText().trim());
                    int userCount = Integer.parseInt(userCountField.getText().trim());
                    String startDate = startDateField.getText().trim();
                    String endDate = endDateField.getText().trim();

                    if (code.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                        showAlert("خطا", "همه‌ی فیلدها باید پر شوند.");
                        return null;
                    }

                    // ساخت JSON برای ارسال به سرور
                    JSONObject json = new JSONObject();
                    json.put("coupon_code", code);
                    json.put("type", type);
                    json.put("value", value);
                    json.put("min_price", minPrice);
                    json.put("user_count", userCount);
                    json.put("start_date", startDate);
                    json.put("end_date", endDate);

                    // ارسال به سرور
                    AdminNetwork.addCoupon(json, apiResponse -> {
                        Platform.runLater(() -> {
                            if ( apiResponse.getStatusCode() == 200) {
                                seedSampleData(); // رفرش جدول
                                showAlert("موفقیت", "کوپن با موفقیت اضافه شد.");
                            } else {
                                showAlert("خطا " + apiResponse.getStatusCode(), apiResponse.getBody());
                            }
                        });
                    });
                } catch (Exception e) {
                    showAlert("خطا", "مشکلی در افزودن کوپن پیش آمد.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void seedSampleData() {
        AdminNetwork.getAllCoupons(apiResponse -> {
            List<Coupon> coupons = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(apiResponse.getBody());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    Coupon coupon = new Coupon();
                    coupon.setId(obj.getLong("id"));
                    coupon.setCode(obj.getString("coupon_code"));
                    coupon.setType(obj.getString("type")); // دستی به enum تبدیل می‌کنه
                    coupon.setValue(obj.getInt("value"));
                    coupon.setMinPrice(obj.getInt("min_price"));
                    coupon.setUserCount(obj.getInt("user_count"));

                    // تاریخ‌ها رو اگر null نبودن، تبدیل کن
                    if (!obj.isNull("start_date")) {
                        coupon.setStartDate(LocalDate.parse(obj.getString("start_date")));
                    }
                    if (!obj.isNull("end_date")) {
                        coupon.setEndDate(LocalDate.parse(obj.getString("end_date")));
                    }

                    coupons.add(coupon);
                }

                // تنظیم در جدول (روی JavaFX thread)
                Platform.runLater(() -> {
                    couponTable.setItems(FXCollections.observableArrayList(coupons));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
