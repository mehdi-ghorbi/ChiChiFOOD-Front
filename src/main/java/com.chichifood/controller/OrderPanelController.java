package com.chichifood.controller;

import com.chichifood.model.Item;
import com.chichifood.model.Order;
import com.chichifood.model.OrderStatus;
import com.chichifood.network.RestaurantNetwork;
import com.chichifood.network.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.chichifood.network.SessionManager.showAlert;

public class OrderPanelController {

    @FXML
    private TableView<Order> tableView;

    @FXML
    private TableColumn<Order, String> colOrderID;

    @FXML
    private TableColumn<Order, String> colStatus;

    @FXML
    private Button detailsBtn;

    @FXML
    private Button statusBtn;

    @FXML
    private Button backBtn;

    public void initialize() {
        colOrderID.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));

        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().name()));

        backBtn.setOnAction(event -> {
            try {
                Parent sellerPanel = FXMLLoader.load(getClass().getResource("/Views/SellerPanel.fxml"));
                Scene scene = new Scene(sellerPanel);
                Stage stage = (Stage) backBtn.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("خطا", "مشکل در باز کردن صفحه قبل.");
            }
        });

        detailsBtn.setOnAction(event -> showOrderDetails());
        statusBtn.setOnAction(event -> showChangeStatusDialog());

        seedSampleData();
    }

    private void seedSampleData() {
        RestaurantNetwork.getAllOrders(SessionManager.getRestaurantID(), response -> {
            try {
                System.out.println("Server response: " + response.getBody());

                JSONArray ordersArray = new JSONArray(response.getBody());

                for (int i = 0; i < ordersArray.length(); i++) {
                    JSONObject orderObj = ordersArray.getJSONObject(i);
                    Order order = new Order();

                    order.setId(orderObj.getInt("id"));
                    order.setDeliveryAddress(orderObj.getString("deliveryAddress"));
                    order.setVendorID(orderObj.getInt("vendorID"));
                    order.setCouponID(orderObj.getInt("couponID"));
                    order.setStatus(OrderStatus.valueOf(orderObj.getString("status")));
                    order.setPayPrice(orderObj.getInt("payPrice"));

                    JSONArray itemsArray = orderObj.getJSONArray("items");
                    List<Item> itemList = new ArrayList<>();

                    for (int j = 0; j < itemsArray.length(); j++) {
                        JSONObject itemObj = itemsArray.getJSONObject(j);
                        Item item = new Item();

                        item.setId(itemObj.getInt("id"));
                        item.setName(itemObj.getString("name"));
                        item.setDescription(itemObj.getString("description"));
                        item.setPrice(itemObj.getInt("price"));
                        item.setSupply(itemObj.getInt("supply"));
                        item.setImageBase64(itemObj.getString("imageBase64"));

                        itemList.add(item);
                    }

                    order.setItems(itemList);
                    tableView.getItems().add(order);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("خطا", "در پردازش داده‌های سفارش مشکلی پیش آمد.");
            }
        });
    }

    private void showOrderDetails() {
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("توجه", "هیچ سفارشی انتخاب نشده است.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("جزئیات سفارش");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        int row = 0;
        grid.add(new Label("شماره سفارش:"), 0, row);
        grid.add(new Label(String.valueOf(selectedOrder.getId())), 1, row++);

        grid.add(new Label("وضعیت فعلی:"), 0, row);
        grid.add(new Label(selectedOrder.getStatus().name()), 1, row++);

        grid.add(new Label("آدرس تحویل:"), 0, row);
        grid.add(new Label(selectedOrder.getDeliveryAddress()), 1, row++);

        grid.add(new Label("قیمت نهایی:"), 0, row);
        grid.add(new Label(String.valueOf(selectedOrder.getPayPrice())), 1, row++);

        if (selectedOrder.getItems() != null && !selectedOrder.getItems().isEmpty()) {
            grid.add(new Label("آیتم‌های سفارش:"), 0, row);

            TableView<Item> itemsTable = new TableView<>();
            itemsTable.setPrefHeight(200);
            itemsTable.setPrefWidth(500);

            TableColumn<Item, String> colName = new TableColumn<>("نام");
            colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

            TableColumn<Item, String> colDesc = new TableColumn<>("توضیح");
            colDesc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

            TableColumn<Item, String> colPrice = new TableColumn<>("قیمت");
            colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));

            TableColumn<Item, ImageView> colImage = new TableColumn<>("تصویر");
            colImage.setCellValueFactory(cellData -> {
                ImageView imageView = new ImageView();
                try {
                    File file = new File(cellData.getValue().getImageBase64());
                    if (file.exists()) {
                        FileInputStream input = new FileInputStream(file);
                        Image img = new Image(input, 50, 50, true, true);
                        imageView.setImage(img);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new javafx.beans.property.SimpleObjectProperty<>(imageView);
            });

            itemsTable.getColumns().addAll(colName, colDesc, colPrice, colImage);
            itemsTable.getItems().addAll(selectedOrder.getItems());

            grid.add(itemsTable, 1, row++);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private void showChangeStatusDialog() {
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("توجه", "هیچ سفارشی انتخاب نشده است.");
            return;
        }

        Dialog<OrderStatus> dialog = new Dialog<>();
        dialog.setTitle("تغییر وضعیت سفارش");

        ButtonType saveButtonType = new ButtonType("ذخیره", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        ComboBox<OrderStatus> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(
                OrderStatus.FINDING_COURIER,
                OrderStatus.WAITING_VENDOR,
                OrderStatus.CANCELLED
        );
        statusComboBox.setValue(selectedOrder.getStatus());
        OrderStatus selectedStatus = statusComboBox.getValue();


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("وضعیت جدید:"), 0, 0);
        grid.add(statusComboBox, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return statusComboBox.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newStatus -> {
            selectedOrder.setStatus(newStatus);
            tableView.refresh();

            // فراخوانی متد برای ارسال به سرور
            RestaurantNetwork.changeOrderStatus(
                    selectedOrder.getId(),
                    newStatus.getValue(),
                    response -> {
                        if (response.getStatusCode() == 200) {
                            Platform.runLater(() ->
                                    showAlert(String.valueOf(response.getStatusCode()), response.getBody())
                            );
                        } else {
                            Platform.runLater(() ->
                                    showAlert(String.valueOf(response.getStatusCode()), response.getBody())
                            );
                        }
                    }
            );
        });

    }
}
