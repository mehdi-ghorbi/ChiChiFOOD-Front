package com.chichifood.controller;

import com.chichifood.model.Item;
import com.chichifood.model.Menu;
import com.chichifood.model.Restaurant;
import com.chichifood.network.ApiResponse;
import com.chichifood.network.NetworkService;
import com.chichifood.network.RestaurantNetwork;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    @FXML
    private Button serviceBtn;

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
    @FXML
    private Button backBtn;
    private final ObservableList<Menu> menuData = FXCollections.observableArrayList();
    private final ObservableList<Item> foodsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        menuNameColumn.setCellValueFactory(cell -> cell.getValue().titleProperty());
        seedSampleData();

        menuTableView.setItems(menuData);
        foodsTableView.setItems(foodsData);
        foodNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty());
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
                    File file = new File(base64);
                    if (file.exists()) {
                        imageView.setImage(new Image(file.toURI().toString()));
                        setGraphic(imageView);
                    } else {
                        imageView.setImage(new Image(getClass().getResource("C:\\Users\\Surface\\Pictures").toExternalForm()));
                        setGraphic(imageView);                    }
                }
            }
        });

        imageColumn.setCellValueFactory(cell -> cell.getValue().imageBase64Property());

        showFoodsBtn.disableProperty().bind(menuTableView.getSelectionModel().selectedItemProperty().isNull());
        editMenuBtn.disableProperty().bind(menuTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteMenuBtn.disableProperty().bind(menuTableView.getSelectionModel().selectedItemProperty().isNull());
        editFoodBtn.disableProperty().bind(foodsTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteFoodBtn.disableProperty().bind(foodsTableView.getSelectionModel().selectedItemProperty().isNull());
        showFoodsBtn.setOnAction(e -> showFoodSelectorForMenu());
        //editMenuBtn.setOnAction(e -> editSelectedMenu());
        deleteMenuBtn.setOnAction(e -> deleteSelectedMenu());
        addMenuBtn.setOnAction(e -> addMenu());
        addFoodBtn.setOnAction(e -> addFoodtoRestaurant());
        editFoodBtn.setOnAction(e -> editSelectedFood());
        deleteFoodBtn.setOnAction(e -> deleteSelectedFood());
        backBtn.setOnAction(e -> gotoPreviousPage());
        serviceBtn.setOnAction((e -> setRestautantService()));
    }

    private void gotoPreviousPage() {
        try {
            Parent sellerPanel = FXMLLoader.load(getClass().getResource("/Views/SellerPanel.fxml"));
            Scene scene = new Scene(sellerPanel);
            Stage stage = (Stage) backBtn.getScene().getWindow(); // یا هر نودی که داری
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("خطا", "مشکل در باز کردن صفحه قبل.");
        }
    }

    private void showFoodSelectorForMenu() {
        Menu menu = menuTableView.getSelectionModel().getSelectedItem();
        if (menu == null) return;

        Dialog<List<Item>> dialog = new Dialog<>();
        dialog.setTitle("مدیریت آیتم‌های منو");
        dialog.setHeaderText("غذاهای مربوط به منو: " + menu.getTitle());

        ButtonType applyButtonType = new ButtonType("اعمال تغییرات", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

        TableView<Item> table = new TableView<>();
        table.setEditable(true);

        TableColumn<Item, Boolean> selectCol = new TableColumn<>("انتخاب");
        selectCol.setEditable(true);

        TableColumn<Item, String> nameCol = new TableColumn<>("نام غذا");

        Map<Item, BooleanProperty> selectedMap = new HashMap<>();
        for (Item item : allFoods) {
            boolean selected = menu.getItems().stream().anyMatch(i -> i.getId() == item.getId()); // مقایسه براساس ID
            BooleanProperty selectedProp = new SimpleBooleanProperty(selected);
            selectedMap.put(item, selectedProp);
        }

        selectCol.setCellValueFactory(cellData -> selectedMap.get(cellData.getValue()));

        selectCol.setCellFactory(column -> {
            CheckBoxTableCell<Item, Boolean> cell = new CheckBoxTableCell<>();
            cell.setEditable(true);
            return cell;
        });

        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        table.getColumns().addAll(selectCol, nameCol);
        table.setItems(FXCollections.observableArrayList(allFoods));

        VBox vbox = new VBox(table);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                List<Item> selectedItems = new ArrayList<>();
                for (Map.Entry<Item, BooleanProperty> entry : selectedMap.entrySet()) {
                    if (entry.getValue().get()) {
                        selectedItems.add(entry.getKey());
                    }
                }
                return selectedItems;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newItemList -> {
            List<Item> previousItems = new ArrayList<>(menu.getItems());
            List<Item> addedItems = new ArrayList<>();
            List<Item> removedItems = new ArrayList<>();

            Set<Integer> newIds = newItemList.stream().map(Item::getId).collect(Collectors.toSet());
            Set<Integer> oldIds = previousItems.stream().map(Item::getId).collect(Collectors.toSet());

            for (Item item : newItemList) {
                if (!oldIds.contains(item.getId())) {
                    addedItems.add(item);
                }
            }

            for (Item item : previousItems) {
                if (!newIds.contains(item.getId())) {
                    removedItems.add(item);
                }
            }

            addedItems.forEach(i -> System.out.println("➕ " + i.getId() + " - " + i.getName()));

            removedItems.forEach(i -> System.out.println("➖ " + i.getId() + " - " + i.getName()));

            updateMenuItems(resID, menu.getTitle(), addedItems, removedItems);
        });
    }

    private void updateMenuItems(String restaurantId, String menuTitle, List<Item> addedItems, List<Item> removedItems) {
        int totalRequests = addedItems.size() + removedItems.size();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger completedCount = new AtomicInteger(0);

        if (totalRequests == 0) {
            System.out.println("هیچ آیتمی برای بروزرسانی وجود ندارد.");
            return;
        }

        Consumer<ApiResponse> callback = response -> {
            int completed = completedCount.incrementAndGet();

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                successCount.incrementAndGet();
            } else {
                System.out.println(" خطا در ارسال درخواست: " + response.getStatusCode() + " | " + response.getBody());
            }

            if (completed == totalRequests) {
                if (successCount.get() == totalRequests) {
                    System.out.println(" همه آیتم‌ها با موفقیت بروزرسانی شدند.");
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "همه آیتم‌ها با موفقیت بروزرسانی شدند.");
                        seedSampleData();
                        alert.showAndWait();
                    });
                } else {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "برخی از آیتم‌ها بروزرسانی نشدند. جزئیات در کنسول.");
                        alert.showAndWait();
                    });
                }
            }
        };

        for (Item item : addedItems) {
            RestaurantNetwork.addItemToMenu(restaurantId, menuTitle, String.valueOf(item.getId()), callback);
        }

        for (Item item : removedItems) {
            RestaurantNetwork.deleteItemFromMenu(restaurantId, menuTitle, String.valueOf(item.getId()), callback);
        }
    }

    private void addMenu() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("اضافه کردن منو");
        dialog.setHeaderText("عنوان منو را وارد کنید:");

        ButtonType addButtonType = new ButtonType("افزودن", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField();
        titleField.setPromptText("عنوان");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("عنوان:"), 0, 0);
        grid.add(titleField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String title = titleField.getText();
                if (title == null || title.trim().isEmpty()) {
                    showAlert("خطا", "عنوان نمی‌تواند خالی باشد.");
                    return null;
                }
                return title.trim();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(title -> {
            JsonObject json = new JsonObject();
            json.addProperty("title", title);

            RestaurantNetwork.addMenu(resID, json, apiResponse -> {
                Platform.runLater(() -> {
                    if (apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300) {
                        loadMenus(resID);
                        showAlert("موفقیت", "منو با موفقیت اضافه شد.");
                    } else {
                        showAlert("خطا " + apiResponse.getStatusCode(), apiResponse.getBody());
                    }
                });
            });
        });
    }

//    private void editSelectedMenu() {
//        Menu menu = menuTableView.getSelectionModel().getSelectedItem();
//        if (menu == null) return;
//        Dialog<String> dialog = new Dialog<>();
//        dialog.setTitle("تغییر دادن منو");
//        dialog.setHeaderText("عنوان منو را وارد کنید:");
//
//        ButtonType addButtonType = new ButtonType("تغییر", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
//
//        TextField titleField = new TextField();
//        titleField.setPromptText(menu.getTitle());
//
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 150, 10, 10));
//        grid.add(new Label("عنوان:"), 0, 0);
//        grid.add(titleField, 1, 0);
//
//        dialog.getDialogPane().setContent(grid);
//
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == addButtonType) {
//                String title = titleField.getText();
//                if (title == null || title.trim().isEmpty()) {
//                    return menu.getTitle();
//                }
//                return title.trim();
//            }
//            return null;
//        });
//
//        dialog.showAndWait().ifPresent(title -> {
//            JsonObject json = new JsonObject();
//            json.addProperty("title", title);
//            RestaurantNetwork.addMenu(resID, json, apiResponse -> {
//                Platform.runLater(() -> {
//                    if (apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300) {
//                        loadMenus(resID);
//                        showAlert("موفقیت", "منو با موفقیت اضافه شد.");
//                    } else {
//                        showAlert("خطا " + apiResponse.getStatusCode(), apiResponse.getBody());
//                    }
//                });
//            });
//        });
//    }

    private void deleteSelectedMenu() {
        Menu menu = menuTableView.getSelectionModel().getSelectedItem();
        if (menu == null) return;

        if (confirm("حذف منو", "آیا مطمئن هستید؟")) {
            RestaurantNetwork.deleteMenu(resID,menu.getTitle(),apiResponse -> {
                Platform.runLater(() -> {
                    if (apiResponse.getStatusCode() == 200 ) {
                        seedSampleData();
                        showAlert("موفقیت", "منو با موفقیت حذف شد.");
                    } else {
                        showAlert(String.valueOf(apiResponse.getStatusCode()),  apiResponse.getBody());
                    }
                });
            });
        }
    }

    private void editSelectedFood() {
        Item item = foodsTableView.getSelectionModel().getSelectedItem();
        JsonObject json = new JsonObject();
        if (item == null) return;
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("تغیر غذا ");
        dialog.setHeaderText("مشخصات غذا را وارد کنید:");
        ButtonType addButtonType = new ButtonType("تغیر دادن", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        TextField nameField = new TextField();
        TextField imageField = new TextField();
        imageField.setEditable(false);

        Button browseImageButton = new Button("انتخاب عکس");
        browseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("انتخاب فایل تصویر");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg", "*.bmp")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (selectedFile != null) {
                imageField.setText(selectedFile.getAbsolutePath());
            }
        });
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();
        TextField supplyField = new TextField();
        TextField keywordsField = new TextField();
        nameField.setPromptText(item.getName());
        imageField.setPromptText(item.getImageBase64());
        descriptionField.setPromptText(item.getDescription());
        priceField.setPromptText(String.valueOf(item.getPrice()));
        supplyField.setPromptText(String.valueOf(item.getSupply()));
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
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {

                    Item item2 = new Item();
                    if (!(nameField.getText().isEmpty() || nameField.getText().equals("") || nameField.getText().isBlank())) {
                        item.setName(nameField.getText());
                        json.addProperty("name", item.getName());
                    }
                    if (!(priceField.getText().isEmpty() || priceField.getText().equals("") || priceField.getText().isBlank())) {
                        item.setPrice(Integer.parseInt(priceField.getText()));
                        json.addProperty("price", item.getPrice());

                    }
                    if (!(imageField.getText().isEmpty() || imageField.getText().equals("") || imageField.getText().isBlank())) {
                        item.setImageBase64(imageField.getText());
                        json.addProperty("imageBase64", item.getImageBase64());
                    }
                    if (!(descriptionField.getText().isEmpty() || descriptionField.getText().equals("") || descriptionField.getText().isBlank())) {
                        item.setDescription(descriptionField.getText());
                        json.addProperty("description", item.getDescription());
                    }
                    if (!(supplyField.getText().isEmpty() || supplyField.getText().equals("") || supplyField.getText().isBlank())) {
                        item.setSupply(Integer.parseInt(supplyField.getText()));
                        json.addProperty("supply", item.getSupply());

                    }
                    if (!(keywordsField.getText().isEmpty() || keywordsField.getText().isBlank() || keywordsField.getText().equals(""))) {
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
                        JsonArray keywordsArray = new JsonArray();
                        for (String keyword : item.getKeywords()) {
                            keywordsArray.add(keyword);
                        }
                        json.add("keywords", keywordsArray);
                    }
                    return item;
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("خطا", "لطفاً مقادیر را درست وارد کنید.");
                    return null;
                }
            }
            return null;
        });
        System.out.println("editing...");
        dialog.showAndWait().ifPresent(item5 -> {
            System.out.println(json.toString());
            RestaurantNetwork.updateItem(resID, json, String.valueOf(item.getId()), apiResponse -> {
                System.out.println("RESPONSE: " + apiResponse.getStatusCode());

                Platform.runLater(() -> {
                    if (apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300) {
                        System.out.println(apiResponse.getBody());
                        seedSampleData();
                        showAlert("موفقیت", apiResponse.getBody());
                    } else {
                        showAlert(String.valueOf(apiResponse.getStatusCode()),  apiResponse.getBody());
                    }
                });
            });
        });
    }

    private void deleteSelectedFood() {
        Item item = foodsTableView.getSelectionModel().getSelectedItem();
        if (item == null) return;

        if (confirm("حذف غذا", "غذا حذف شود؟")) {
            RestaurantNetwork.deleteItem(resID,String.valueOf(item.getId()),apiResponse -> {
                Platform.runLater(() -> {
                    if (apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300) {
                        System.out.println(apiResponse.getBody());
                        seedSampleData();
                        showAlert("موفقیت", "آیتم با موفقیت حذف شد.");
                    } else {
                        showAlert(String.valueOf(apiResponse.getStatusCode()),  apiResponse.getBody());
                    }
                });
            });
        }
    }

    private void setRestautantService() {
        Dialog<Restaurant> dialog = new Dialog<>();
        dialog.setTitle("تغییر دادن هزینه خدمات رستوران");
        dialog.setHeaderText("لطفاً مقادیر زیر را وارد کنید:");

        // دکمه‌ها
        ButtonType applyButtonType = new ButtonType("اعمال", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

        // فرم
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField serviceFeeField = new TextField();
        serviceFeeField.setPromptText("هزینه سرویس");

        TextField packagingFeeField = new TextField();
        packagingFeeField.setPromptText("هزینه بسته‌بندی");

        TextField taxField = new TextField();
        taxField.setPromptText("مقدار مالیات");

        ComboBox<String> taxTypeBox = new ComboBox<>();
        taxTypeBox.getItems().addAll("FIXED", "PERCENTAGE");
        taxTypeBox.setValue("FIXED");

        grid.add(new Label("هزینه سرویس:"), 0, 0);
        grid.add(serviceFeeField, 1, 0);

        grid.add(new Label("هزینه بسته‌بندی:"), 0, 1);
        grid.add(packagingFeeField, 1, 1);

        grid.add(new Label("مقدار مالیات:"), 0, 2);
        grid.add(taxField, 1, 2);

        grid.add(new Label("نوع مالیات:"), 0, 3);
        grid.add(taxTypeBox, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(serviceFeeField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                try {
                    int serviceFee = Integer.parseInt(serviceFeeField.getText().trim());
                    int packagingFee = Integer.parseInt(packagingFeeField.getText().trim());
                    double tax = Double.parseDouble(taxField.getText().trim());
                    String taxType = taxTypeBox.getValue();
                    JsonObject json = new JsonObject();
                    json.addProperty("additional_fee", packagingFee);
                    json.addProperty("tax_fee", tax);
                    json.addProperty("tax_type", taxType);
                    json.addProperty("service_fee", serviceFee);
                    RestaurantNetwork.updateRestaurant(json,resID,apiResponse -> {
                        Platform.runLater(() -> {
                           if (apiResponse.getStatusCode() == 200){
                               showAlert(String.valueOf(apiResponse.getStatusCode()),  apiResponse.getBody());
                           }else
                                showAlert(String.valueOf(apiResponse.getStatusCode()),  apiResponse.getBody());
                        });
                    });


                } catch (NumberFormatException e) {
                    showAlert("خطا", "لطفاً مقادیر را به درستی وارد کنید.");
                }
            }
            return null;
        });

        Optional<Restaurant> result = dialog.showAndWait();
        result.ifPresent(restaurant -> {

        });
    }


    private void addFoodtoRestaurant() {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("اضافه کردن غذای جدید");
        dialog.setHeaderText("مشخصات غذا را وارد کنید:");

        ButtonType addButtonType = new ButtonType("اضافه کردن", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        TextField imageField = new TextField();
        imageField.setEditable(false);

        Button browseImageButton = new Button("انتخاب عکس");
        browseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("انتخاب فایل تصویر");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg", "*.bmp")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (selectedFile != null) {
                imageField.setText(selectedFile.getAbsolutePath());
            }
        });
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();
        TextField supplyField = new TextField();
        TextField keywordsField = new TextField();

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
                    e.printStackTrace();
                    showAlert("خطا", "لطفاً مقادیر را درست وارد کنید.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item -> {
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
                Platform.runLater(() -> {
                    if (apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300) {
                        System.out.println(apiResponse.getBody());
                        seedSampleData();
                        showAlert("موفقیت", "آیتم با موفقیت اضافه شد.");
                    } else {
                        showAlert(String.valueOf(apiResponse.getStatusCode()),  apiResponse.getBody());
                    }
                });
            });
        });
    }


    private boolean confirm(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(btn -> btn == ButtonType.YES).isPresent();
    }

    public static String resID;
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
            if (apiResponse.getBody() == null) return;
            System.out.println(apiResponse.getStatusCode());
            if (apiResponse.getStatusCode() == 200) {
                System.out.println("imdone");
                String json = (String) apiResponse.getBody();
                Gson gson = new Gson();
                Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> menus = gson.fromJson(json, type);

                menuData.clear();

                for (Map<String, Object> map : menus) {
                    Menu menu = new Menu();
                    menu.setId(Long.parseLong(map.get("id").toString()));
                    menu.setTitle(map.get("title").toString());
                    List<Item> items = new ArrayList<>();
                    List<Map<String, Object>> itemMaps = (List<Map<String, Object>>) map.get("items");
                    if (itemMaps != null) {
                        for (Map<String, Object> itemMap : itemMaps) {
                            Item item = new Item();
                            item.setId((int) Double.parseDouble(itemMap.get("id").toString()));
                            item.setName(itemMap.get("name").toString());
                            item.setDescription(itemMap.get("description").toString());
                            item.setPrice((int) Double.parseDouble(itemMap.get("price").toString()));
                            item.setSupply((int) Double.parseDouble(itemMap.get("supply").toString()));
                            item.setImageBase64(itemMap.get("imageBase64") != null ? itemMap.get("imageBase64").toString() : "");
                            items.add(item);
                        }
                    }

                    menu.setItems(FXCollections.observableArrayList(items));
                    menuData.add(menu);
                }
            } else {
                System.out.println("خطا در دریافت منوها: " + apiResponse.getBody());
            }
        });

    }
    private List <Item> allFoods = new ArrayList<>();
    private void loadItems(String restaurantId) {
        RestaurantNetwork.getItems(restaurantId, apiResponse -> {
            if (apiResponse.getStatusCode() == 200) {
                String json = (String) apiResponse.getBody();

                Gson gson = new Gson();
                Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> items = gson.fromJson(json, type);

                if (items == null) {
                    return;
                }

                Platform.runLater(() -> {
                    foodsData.clear();
                    allFoods.clear();
                    for (Map<String, Object> map : items) {
                        Item item = new Item();
                        item.setId((int) Double.parseDouble(map.get("id").toString()));
                        item.setName(map.get("name").toString());
                        item.setPrice((int) Double.parseDouble(map.get("price").toString()));
                        item.setImageBase64(map.get("image") != null ? map.get("image").toString() : "");

                        foodsData.add(item);
                        allFoods.add(item);
                    }
                });

            } else {
                System.out.println("خطا در دریافت آیتم‌ها: " + apiResponse.getBody());
            }
        });
    }



}




