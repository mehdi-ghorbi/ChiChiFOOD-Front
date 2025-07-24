package com.chichifood.controller;

import com.chichifood.model.Item;
import com.chichifood.model.Menu;
import com.chichifood.network.RestaurantNetwork;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.chichifood.network.SessionManager.showAlert;

public class RestaurantPanelController {
    public RestaurantPanelController() {
        System.out.println("### CONSTRUCTOR CALLED");
    }


    // ----- منو -----
    @FXML
    private TableView<Menu> menuTableView;
    @FXML
    private TableColumn<Menu, String> menuNameColumn;
    @FXML
    private Button showFoodsBtn;
    @FXML
    private Button editMenuBtn;
    @FXML
    private Button deleteMenuBtn;
    @FXML
    private Button addMenuBtn;

    // ----- غذا -----
    @FXML
    private TableView<Item> foodsTableView;
    @FXML
    private TableColumn<Item, String> foodNameColumn;
    @FXML
    private TableColumn<Item, Number> priceColumn;
    @FXML
    private TableColumn<Item, String> imageColumn;
    @FXML
    private Button editFoodBtn;
    @FXML
    private Button deleteFoodBtn;
    @FXML
    private Button addFoodBtn;

    private final ObservableList<Menu> menuData = FXCollections.observableArrayList();
    private final ObservableList<Item> foodsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ستون نام منو
        menuNameColumn.setCellValueFactory(cell -> cell.getValue().titleProperty());
        System.out.println("### initialize CALLED");

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
        addFoodBtn.setOnAction(e -> addFoodtoRestaurant());
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
            RestaurantNetwork.deleteItem(resID,String.valueOf(item.getId()),apiResponse -> {
                Platform.runLater(() -> {  // چون ممکنه این callback در thread غیر UI باشه، حتما UI update رو توی Platform.runLater انجام بده
                    if (apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300) {
                        // موفقیت — جدول رو آپدیت کن
                        System.out.println(apiResponse.getBody());
                        seedSampleData();
                        showAlert("موفقیت", "آیتم با موفقیت حذف شد.");
                    } else {
                        // خطا — ارور رو نشون بده
                        showAlert(String.valueOf(apiResponse.getStatusCode()),  apiResponse.getBody());
                    }
                });
            });
        }
    }


    private void addFoodtoRestaurant() {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("اضافه کردن غذای جدید");
        dialog.setHeaderText("مشخصات غذا را وارد کنید:");

        ButtonType addButtonType = new ButtonType("اضافه کردن", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // فیلدهای ورودی
        TextField nameField = new TextField();
        TextField imageField = new TextField();
        imageField.setEditable(false); // نذار دستی تغییر بده

        Button browseImageButton = new Button("انتخاب عکس");
        browseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("انتخاب فایل تصویر");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg", "*.bmp")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (selectedFile != null) {
                imageField.setText(selectedFile.getAbsolutePath()); // ذخیره مسیر کامل
            }
        });
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();
        TextField supplyField = new TextField();
        TextField keywordsField = new TextField(); // با کاما جدا بشن مثلا: pizza,cheese,fast

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("نام:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("تصویر (مسیر فایل):"), 0, 1);
        HBox imageBox = new HBox(10, imageField, browseImageButton);
        grid.add(imageBox, 1, 1);
        grid.add(new Label("توضیحات:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("قیمت:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("موجودی:"), 0, 4);
        grid.add(supplyField, 1, 4);
        grid.add(new Label("کلمات کلیدی (با , جدا کن):"), 0, 5);
        grid.add(keywordsField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // مقدار خروجی دیالوگ وقتی OK زده شد
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Item item = new Item();
                    item.setName(nameField.getText());
                    item.setImageBase64(imageField.getText());
                    item.setDescription(descriptionField.getText());
                    item.setPrice(Integer.parseInt(priceField.getText()));
                    item.setSupply(Integer.parseInt(supplyField.getText()));

                    String rawKeywords = keywordsField.getText();
                    List<String> keywords = new ArrayList<>();
                    if (rawKeywords != null && !rawKeywords.isBlank()) {
                        String[] keywordsArray = rawKeywords.split(",");
                        for (String k : keywordsArray) {
                            String trimmed = k.trim();
                            if (!trimmed.isEmpty()) {
                                keywords.add(trimmed);
                            }
                        }
                    }
                    item.setKeywords(keywords);


                    return item;
                } catch (Exception e) {
                    e.printStackTrace(); // نمایش بهتر خطا
                    showAlert("خطا", "لطفاً مقادیر را درست وارد کنید.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item -> {
            // بعد از وارد شدن اطلاعات توسط کاربر
            JsonObject json = new JsonObject();
            json.addProperty("name", item.getName());
            json.addProperty("imageBase64", item.getImageBase64());
            json.addProperty("description", item.getDescription());
            json.addProperty("price", item.getPrice());
            json.addProperty("supply", item.getSupply());

            JsonArray keywordsArray = new JsonArray();
            for (String keyword : item.getKeywords()) {
                keywordsArray.add(keyword);
            }
            json.add("keywords", keywordsArray);
            System.out.println(json.toString());
            RestaurantNetwork.addItem(resID, json, apiResponse -> {
                Platform.runLater(() -> {  // چون ممکنه این callback در thread غیر UI باشه، حتما UI update رو توی Platform.runLater انجام بده
                    if (apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300) {
                        // موفقیت — جدول رو آپدیت کن
                        System.out.println(apiResponse.getBody());
                        seedSampleData();
                        showAlert("موفقیت", "آیتم با موفقیت اضافه شد.");
                    } else {
                        // خطا — ارور رو نشون بده
                        showAlert(String.valueOf(apiResponse.getStatusCode()),  apiResponse.getBody());
                    }
                });
            });
        });
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
    public static String resID;
    // داده تستی
    private void seedSampleData() {
        RestaurantNetwork.getRestaurants(apiResponse -> {
            if (apiResponse.getStatusCode() == 200) {
                JsonArray jsonArray = JsonParser.parseString(apiResponse.getBody()).getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                    String restaurantID = jsonObject.get("id").getAsString();
                    resID = restaurantID;
                    loadItems(restaurantID);
                    loadMenus(restaurantID);
                }
            } else {
                System.out.println("خطا در گرفتن رستوران‌ها: " + apiResponse.getBody());
            }
        });

    }

    private void loadMenus(String restaurantId) {
        RestaurantNetwork.getMenus(restaurantId, apiResponse -> {
            System.out.println("im calling");

            if (apiResponse.getBody() == null) {
                return;
            }

            if (apiResponse.getStatusCode() == 200) {
                String json = (String) apiResponse.getBody(); // چون نوع body هنوز String هست
                System.out.println("kiram to gpt");
                Gson gson = new Gson();
                Type type = new TypeToken<List<Map<String, Object>>>() {
                }.getType();
                List<Map<String, Object>> menus = gson.fromJson(json, type);

                for (Map<String, Object> map : menus) {
                    Menu menu = new Menu();
                    menu.setId(Long.parseLong(map.get("id").toString()));
                    menu.setTitle(map.get("title").toString());
                    menuData.add(menu);
                }
            } else {
                System.out.println("خطا در دریافت منوها: " + apiResponse.getBody());
            }
        });
    }

    private void loadItems(String restaurantId) {
        RestaurantNetwork.getItems(restaurantId, apiResponse -> {
            if (apiResponse.getStatusCode() == 200) {
                String json = (String) apiResponse.getBody();

                Gson gson = new Gson();
                Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> items = gson.fromJson(json, type);

                if (items == null) {
                    System.out.println("⚠️ لیست آیتم‌ها خالی یا نامعتبره!");
                    return;
                }

                Platform.runLater(() -> {
                    foodsData.clear();
                    for (Map<String, Object> map : items) {
                        Item item = new Item();
                        item.setId((int) Double.parseDouble(map.get("id").toString()));
                        item.setName(map.get("name").toString());
                        item.setPrice((int) Double.parseDouble(map.get("price").toString()));
                        item.setImageBase64(map.get("image") != null ? map.get("image").toString() : "");

                        foodsData.add(item);
                    }
                });

            } else {
                System.out.println("خطا در دریافت آیتم‌ها: " + apiResponse.getBody());
            }
        });
    }



}




