package com.chichifood.controller;



import com.chichifood.model.Restaurant;
import com.chichifood.network.BuyerNetwork;
import com.chichifood.localdata.*;
import com.chichifood.network.NetworkService;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.chichifood.network.SessionManager.showAlert;

public class OrdersBuyerController {

    @FXML private Button homeButton;
    @FXML private Button transactionsButton;
    @FXML private TextField itemSearchField;
    @FXML private TextField vendorSearchField;
    @FXML private Button searchButton;

    @FXML private VBox pendingOrdersContainer;
    @FXML private VBox activeOrdersContainer;
    @FXML private VBox pastOrdersContainer;



    public void setPreviousStage(Stage previousStage) {
        this.previousStage = previousStage;
    }

    private Stage previousStage;
    private Stage currentStage;

    @FXML
    public void initialize() {

        homeButton.setOnAction(e -> {
            Stage currentStage = (Stage) homeButton.getScene().getWindow();
            currentStage.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/BuyerPanel.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("خانه");
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        transactionsButton.setOnAction(e -> {
            Stage currentStage = (Stage) homeButton.getScene().getWindow();
            currentStage.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Transactions.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("تراکنش");
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

//        searchButton.setOnAction(event -> {
//            if (itemSearchField.getText().isEmpty() && vendorSearchField.getText().isEmpty()) {
//                showAlert("ورودی نامعتبر" , "لطفا فیلد ها را پر کنید. ");
//            }
//            String itemName = itemSearchField.getText();
//            String vendorName = vendorSearchField.getText();
//
//            BuyerNetwork.serachOrders(itemName, vendorName, apiResponse -> {
//                Platform.runLater(() -> {
//                    int statusCode = apiResponse.getStatusCode();
//                    String body = apiResponse.getBody();
//
//                    if (statusCode == 200) {
//                        JsonArray vendorsArray = JsonParser.parseString(body).getAsJsonArray();
//
//                        for (JsonElement vendorElement : vendorsArray) {
//                            JsonObject vendorJson = vendorElement.getAsJsonObject();
//                            Restaurant restaurant = new Restaurant(
//                                    vendorJson.get("id").getAsInt(),
//                                    vendorJson.get("name").getAsString(),
//                                    vendorJson.get("address").getAsString(),
//                                    vendorJson.get("phone").getAsString(),
//                                    vendorJson.get("logoBase64").getAsString(),
//                                    vendorJson.get("tax_fee").getAsInt(),
//                                    vendorJson.get("additional_fee").getAsInt()
//                            );
//                            restaurantList.add(restaurant);
//                        }
//
//                        try {
//                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/OrderBuyerSearch.fxml"));
//                            Parent root = loader.load();
//
//                            VendorsViewsController controller = loader.getController();
//                            controller.setVendors(restaurantList);
//
//                            Stage stage = new Stage();
//                            stage.setTitle("Orders");
//                            stage.setScene(new Scene(root));
//                            stage.show();
//                            ((Stage) searchButton.getScene().getWindow()).close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            });
//        });

        showPendingOrders();
        showActiveOrders();
        showPastOrders();
    }
    public void setStage(Stage stage) {
        this.currentStage = stage;
    }
    private void showPendingOrders() {
        Map<Integer, List<CartItem>> carts = CartManager.getAllCarts();
        pendingOrdersContainer.getChildren().clear();

        if (carts.isEmpty()) {
            Label emptyLabel = new Label("سبد خرید شما خالی است.");
            pendingOrdersContainer.getChildren().add(emptyLabel);
            return;
        }

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setPrefWrapLength(800);

        for (Map.Entry<Integer, List<CartItem>> entry : carts.entrySet()) {
            int vendorId = entry.getKey();
            List<CartItem> cartItems = entry.getValue();

            VBox cartBox = new VBox(5);
            cartBox.setPrefWidth(200);
            cartBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #fefefe; -fx-background-radius: 6;");

            Label vendorLabel = new Label("رستوران ID: " + vendorId);
            vendorLabel.setStyle("-fx-font-weight: bold;");
            cartBox.getChildren().add(vendorLabel);

            for (CartItem ci : cartItems) {
                String text = ci.getItem().getName() + " × " + ci.getQuantity();
                Label itemLabel = new Label(text);
                cartBox.getChildren().add(itemLabel);
            }

            TextField couponIdField = new TextField();
            couponIdField.setPromptText("آیدی کوپن");
            couponIdField.setMaxWidth(180);
            cartBox.getChildren().add(couponIdField);

            TextField addressField = new TextField();
            addressField.setPromptText("آدرس تحویل");
            addressField.setMaxWidth(180);
            cartBox.getChildren().add(addressField);

            Button submitButton = new Button("ثبت سفارش");
            submitButton.setMaxWidth(180);
            cartBox.getChildren().add(submitButton);

            submitButton.setOnAction(event -> {
                String address = addressField.getText().trim();
                String couponIdText = couponIdField.getText().trim();
                if (address.isEmpty()) {
                    showAlert("خطا", "لطفاً آدرس تحویل را وارد کنید.");
                    return;
                }

                int couponId = couponIdText.isEmpty() ? -1 : Integer.parseInt(couponIdText);
                List<BuyerNetwork.ItemRequest> itemRequests = cartItems.stream()
                        .map(ci -> new BuyerNetwork.ItemRequest(ci.getItem().getId(), ci.getQuantity()))
                        .toList();

                BuyerNetwork.submitOrder(address, vendorId, couponId, itemRequests, response -> {
                    Platform.runLater(() -> {
                        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
                            CartManager.clearCart(vendorId);

                            JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
                            int orderID = json.get("id").getAsInt();
                            int price = json.get("pay_price").getAsInt();
                            int walletBalance = json.get("wallet_ballance").getAsInt();

                            showPendingOrders();

                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Payment.fxml"));
                                Parent root = loader.load();

                                PaymentController controller = loader.getController();
                                controller.setPaymentInfo(price, orderID, walletBalance);

                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.setTitle("پرداخت");
                                stage.show();

                            } catch (IOException ex) {
                                ex.printStackTrace();
                                showAlert("خطا", "مشکلی در نمایش صفحه پرداخت به وجود آمد.");
                            }

                        } else {
                            showAlert("خطا", "ثبت سفارش ناموفق بود: " + response.getBody());
                        }
                    });
                });

            });

            flowPane.getChildren().add(cartBox);
        }

        pendingOrdersContainer.getChildren().add(flowPane);
    }




    private void showOrder(int orderId) {
        BuyerNetwork.getOrder(orderId, response -> {
            if (response.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    activeOrdersContainer.getChildren().clear();
                    Label label = new Label("سفارش فعال: " + response.getBody());
                    activeOrdersContainer.getChildren().add(label);
                });
            } else {
                Platform.runLater(() -> {
                    activeOrdersContainer.getChildren().clear();
                    Label label = new Label("خطا در دریافت سفارش فعال: " + response.getBody());
                    activeOrdersContainer.getChildren().add(label);
                });
            }
        });
    }

    private void showActiveOrders() {
        int userId = buyerId();
        BuyerNetwork.getAllUserOrders(userId, response -> {
            if (response.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    activeOrdersContainer.getChildren().clear();

                    JsonArray orders = JsonParser.parseString(response.getBody()).getAsJsonArray();

                    FlowPane flowPane = new FlowPane();
                    flowPane.setHgap(15);
                    flowPane.setVgap(15);
                    flowPane.setPrefWidth(750);

                    for (JsonElement elem : orders) {
                        JsonObject order = elem.getAsJsonObject();

                        String status = order.get("status").getAsString();
                        if ((status.equals("COMPLETED") || status.equals("CANCELLED")
                                || status.equals("UNPAID_AND_CANCELLED") || status.equals("SUBMITTED"))) continue;

                        int orderId = order.get("id").getAsInt();
                        String vendorName = order.get("vendorName").getAsString();
                        int payPrice = order.get("payPrice").getAsInt();

                        VBox card = new VBox(5);
                        card.setPadding(new Insets(10));
                        card.setStyle("-fx-background-color: #FFF3E0; -fx-border-color: #FB8C00; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
                        card.setPrefWidth(220);

                        Label title = new Label("سفارش #" + orderId);
                        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                        title.setCursor(Cursor.HAND);
                        title.setOnMouseEntered(e -> title.setStyle(" -fx-font-size: 16px; -fx-font-weight: bold;"));
                        title.setOnMouseExited(e -> title.setStyle(" -fx-font-size: 14px;"));
                        title.setOnMouseClicked(e -> {
                            BuyerNetwork.getOrder(orderId, apiResponse -> {
                                Platform.runLater(() -> {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/OneOrderView.fxml"));
                                        Parent root = loader.load();

                                        OneOrderController controller = loader.getController();
                                        controller.setOrderId(orderId);

                                        Stage stage = new Stage();
                                        stage.setTitle("سفارش شماره: " + orderId);
                                        stage.setScene(new Scene(root));
                                        stage.show();

                                        Stage currentStage = (Stage) title.getScene().getWindow();
                                        currentStage.close();

                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                });
                            });
                        });


                        Label vendorLabel = new Label("رستوران: " + vendorName);
                        vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 13px;");
                        vendorLabel.setCursor(Cursor.HAND);
                        vendorLabel.setOnMouseEntered(e -> vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 15px; -fx-font-weight: bold;"));
                        vendorLabel.setOnMouseExited(e -> vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 13px;"));


                        vendorLabel.setOnMouseClicked(e -> {
                            System.out.println("باز کردن صفحه رستوران " + vendorName);
                            BuyerNetwork.getVendorMenus(Integer.parseInt(order.get("vendorID").toString()), fullVendor -> {
                                Platform.runLater(() -> {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/VendorsMenuView.fxml"));
                                        Parent root = loader.load();

                                        VendorMenuController controller = loader.getController();
                                        controller.setVendor(fullVendor);

                                        Stage stage = new Stage();
                                        stage.setTitle("منوی رستوران " + fullVendor.getName());
                                        stage.setScene(new Scene(root));
                                        stage.show();

                                        // بستن صفحه فعلی
                                        Stage currentStage = (Stage) vendorLabel.getScene().getWindow();
                                        currentStage.close();
                                    } catch (IOException ee) {
                                        ee.printStackTrace();
                                    }
                                });
                            });
                        });

                        Label priceLabel = new Label("مبلغ: " + payPrice + " تومان");

                        card.getChildren().addAll(title, vendorLabel, priceLabel);
                        flowPane.getChildren().add(card);
                    }

                    if (flowPane.getChildren().isEmpty()) {
                        activeOrdersContainer.getChildren().add(new Label("هیچ سفارش فعالی وجود ندارد."));
                    } else {
                        activeOrdersContainer.getChildren().add(flowPane);
                    }
                });
            } else {
                Platform.runLater(() -> {
                    activeOrdersContainer.getChildren().clear();
                    Label label = new Label("خطا در دریافت سفارش‌های فعال: " + response.getBody());
                    activeOrdersContainer.getChildren().add(label);
                });
            }
        });
    }
    private void showPastOrders() {
        int userId = buyerId();
        BuyerNetwork.getAllUserOrders(userId, response -> {
            if (response.getStatusCode() == 200) {
                Platform.runLater(() -> {
                    pastOrdersContainer.getChildren().clear();

                    JsonArray orders = JsonParser.parseString(response.getBody()).getAsJsonArray();

                    FlowPane flowPane = new FlowPane();
                    flowPane.setHgap(15);
                    flowPane.setVgap(15);
                    flowPane.setPrefWidth(750);

                    for (JsonElement elem : orders) {
                        JsonObject order = elem.getAsJsonObject();

                        String status = order.get("status").getAsString();
                        if (!(status.equals("COMPLETED") || status.equals("CANCELLED")
                                || status.equals("UNPAID_AND_CANCELLED"))) continue;

                        int orderId = order.get("id").getAsInt();
                        String vendorName = order.get("vendorName").getAsString();
                        int payPrice = order.get("payPrice").getAsInt();

                        VBox card = new VBox(5);
                        card.setPadding(new Insets(10));
                        card.setStyle("-fx-background-color: #FFF3E0; -fx-border-color: #FB8C00; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
                        card.setPrefWidth(220);

                        Label title = new Label("سفارش #" + orderId);
                        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                        title.setCursor(Cursor.HAND);
                        title.setOnMouseEntered(e -> title.setStyle(" -fx-font-size: 16px; -fx-font-weight: bold;"));
                        title.setOnMouseExited(e -> title.setStyle(" -fx-font-size: 14px;"));
                        title.setOnMouseClicked(e -> {
                            BuyerNetwork.getOrder(orderId, apiResponse -> {
                                Platform.runLater(() -> {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/OneOrderView.fxml"));
                                        Parent root = loader.load();

                                        OneOrderController controller = loader.getController();
                                        controller.setOrderId(orderId);

                                        Stage stage = new Stage();
                                        stage.setTitle("سفارش شماره: " + orderId);
                                        stage.setScene(new Scene(root));
                                        stage.show();

                                        Stage currentStage = (Stage) title.getScene().getWindow();
                                        currentStage.close();

                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                });
                            });
                        });

                        Label vendorLabel = new Label("رستوران: " + vendorName);
                        vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 13px;");
                        vendorLabel.setCursor(Cursor.HAND);
                        vendorLabel.setOnMouseEntered(e -> vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 15px; -fx-font-weight: bold;"));
                        vendorLabel.setOnMouseExited(e -> vendorLabel.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 13px;"));

                        vendorLabel.setOnMouseClicked(e -> {
                            System.out.println("باز کردن صفحه رستوران " + vendorName);
                            BuyerNetwork.getVendorMenus(Integer.parseInt(order.get("vendorID").toString()), fullVendor -> {
                                Platform.runLater(() -> {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/VendorsMenuView.fxml"));
                                        Parent root = loader.load();

                                        VendorMenuController controller = loader.getController();
                                        controller.setVendor(fullVendor);

                                        Stage stage = new Stage();
                                        stage.setTitle("منوی رستوران " + fullVendor.getName());
                                        stage.setScene(new Scene(root));
                                        stage.show();

                                        Stage currentStage = (Stage) vendorLabel.getScene().getWindow();
                                        currentStage.close();
                                    } catch (IOException ee) {
                                        ee.printStackTrace();
                                    }
                                });
                            });
                        });

                        Label priceLabel = new Label("مبلغ: " + payPrice + " تومان");
                        Label addComment = new Label("ثبت نظر");
                        addComment.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 11px;");
                        addComment.setCursor(Cursor.HAND);
                        addComment.setOnMouseEntered(e -> addComment.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 13px; -fx-font-weight: bold;"));
                        addComment.setOnMouseExited(e -> addComment.setStyle("-fx-text-fill: #EF6C00; -fx-font-size: 11px;"));

                        addComment.setOnMouseClicked(e -> {

                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AddComment.fxml"));
                                        Parent root = loader.load();


                                        Stage stage = new Stage();
                                        stage.setTitle("ثبت نظر " );
                                        stage.setScene(new Scene(root));
                                        stage.show();

                                        Stage currentStage = (Stage) addComment.getScene().getWindow();
                                        currentStage.close();
                                    } catch (IOException ee) {
                                        ee.printStackTrace();
                                    }


                        });
                        card.getChildren().addAll(title, vendorLabel, priceLabel, addComment);
                        flowPane.getChildren().add(card);
                    }

                    if (flowPane.getChildren().isEmpty()) {
                        pastOrdersContainer.getChildren().add(new Label("هیچ سفارش تحویل‌داده‌شده‌ای وجود ندارد."));
                    } else {
                        pastOrdersContainer.getChildren().add(flowPane);
                    }
                });
            } else {
                Platform.runLater(() -> {
                    pastOrdersContainer.getChildren().clear();
                    Label label = new Label("خطا در دریافت سفارش‌های گذشته: " + response.getBody());
                    pastOrdersContainer.getChildren().add(label);
                });
            }
        });
    }
    public int buyerId(){
        final int[] id = {0};
        NetworkService.getProfile(apiResponse -> {
            JsonObject json = JsonParser.parseString(apiResponse.getBody()).getAsJsonObject();
            id[0] = json.get("id").getAsInt();

        });
        return id[0];
    }
}
