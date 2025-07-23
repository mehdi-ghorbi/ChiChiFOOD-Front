package com.chichifood.controller;

import com.chichifood.model.Item;
import com.chichifood.model.Menu;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RestaurantPanelController {

    // ----- منو -----
    @FXML private TableView<Menu> menuTableView;
    @FXML private TableColumn<Menu, String> menuNameColumn;
    @FXML private Button showFoodsBtn;
    @FXML private Button editMenuBtn;
    @FXML private Button deleteMenuBtn;

    // ----- غذا -----
    @FXML private TableView<Item> foodsTableView;
    @FXML private TableColumn<Item, String>  foodNameColumn;
    @FXML private TableColumn<Item, Number>  priceColumn;
    @FXML private TableColumn<Item, String>  imageColumn;
    @FXML private Button editFoodBtn;
    @FXML private Button deleteFoodBtn;

    private final ObservableList<Menu> menuData  = FXCollections.observableArrayList();
    private final ObservableList<Item> foodsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ستون نام منو
        menuNameColumn.setCellValueFactory(cell -> cell.getValue().titleProperty());

        // داده تستی منو + غذا
        seedSampleData();

        menuTableView.setItems(menuData);
        foodsTableView.setItems(foodsData);

        // ستون‌های جدول غذا
        foodNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty());

        // ستون تصویر (Base64 -> ImageView)
        imageColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                imageView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String base64, boolean empty) {
                super.updateItem(base64, empty);
                if (empty || base64 == null || base64.isBlank()) {
                    setGraphic(null);
                } else {
                    imageView.setImage(decodeBase64ToImage(base64));
                    setGraphic(imageView);
                }
            }
        });
        imageColumn.setCellValueFactory(cell -> cell.getValue().imageBase64Property());

        // غیرفعال کردن دکمه‌ها وقتی چیزی انتخاب نشده
        showFoodsBtn.disableProperty().bind(menuTableView.getSelectionModel().selectedItemProperty().isNull());
        editMenuBtn.disableProperty().bind(menuTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteMenuBtn.disableProperty().bind(menuTableView.getSelectionModel().selectedItemProperty().isNull());

        editFoodBtn.disableProperty().bind(foodsTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteFoodBtn.disableProperty().bind(foodsTableView.getSelectionModel().selectedItemProperty().isNull());

        // رویداد دکمه‌ها
        showFoodsBtn.setOnAction(e -> loadFoodsForSelectedMenu());
        editMenuBtn.setOnAction(e -> editSelectedMenu());
        deleteMenuBtn.setOnAction(e -> deleteSelectedMenu());

        editFoodBtn.setOnAction(e -> editSelectedFood());
        deleteFoodBtn.setOnAction(e -> deleteSelectedFood());
    }

    // ----- اکشن‌ها -----

    private void loadFoodsForSelectedMenu() {
        Menu menu = menuTableView.getSelectionModel().getSelectedItem();
        if (menu == null) return;
        foodsData.setAll(menu.getItems());
    }

    private void editSelectedMenu() {
        Menu menu = menuTableView.getSelectionModel().getSelectedItem();
        if (menu == null) return;
        System.out.println("ویرایش منو: id=" + menu.getId() + ", name=" + menu.getTitle());
        // TODO: باز کردن Dialog یا صفحه ویرایش
    }

    private void deleteSelectedMenu() {
        Menu menu = menuTableView.getSelectionModel().getSelectedItem();
        if (menu == null) return;

        if (confirm("حذف منو", "آیا مطمئن هستید؟")) {
            // TODO: فراخوانی API حذف منو با menu.getId()
            menuData.remove(menu);
            foodsData.clear();
        }
    }

    private void editSelectedFood() {
        Item item = foodsTableView.getSelectionModel().getSelectedItem();
        if (item == null) return;
        System.out.println("ویرایش غذا: id=" + item.getId() + ", name=" + item.getName());
        // TODO: باز کردن فرم ویرایش غذا
    }

    private void deleteSelectedFood() {
        Item item = foodsTableView.getSelectionModel().getSelectedItem();
        if (item == null) return;

        if (confirm("حذف غذا", "غذا حذف شود؟")) {
            // TODO: حذف از سرور با item.getId()
            foodsData.remove(item);
        }
    }

    // ----- کمکی -----

    private boolean confirm(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(btn -> btn == ButtonType.YES).isPresent();
    }

    private Image decodeBase64ToImage(String b64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(b64);
            return new Image(new ByteArrayInputStream(bytes));
        } catch (Exception ex) {
            // تصویر نامعتبر
            byte[] fallback = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAAB...".getBytes(StandardCharsets.UTF_8); // یا هیچی
            return null;
        }
    }

    // داده تستی
    private void seedSampleData() {

    }
}
