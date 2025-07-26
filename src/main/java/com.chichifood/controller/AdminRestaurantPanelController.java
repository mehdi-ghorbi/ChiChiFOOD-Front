package com.chichifood.controller;

import com.chichifood.model.Restaurant;
import com.chichifood.model.User;
import com.chichifood.network.AdminNetwork;
import com.chichifood.network.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static com.chichifood.network.SessionManager.showAlert;

public class AdminRestaurantPanelController {

    @FXML
    private TableView<Restaurant> restaurantTable;

    @FXML
    private TableColumn<Restaurant, Integer> restaurantIdColumn;

    @FXML
    private TableColumn<Restaurant, String> restaurantNameColumn;

    @FXML
    private TableColumn<Restaurant, String> restaurantStatusColumn;

    @FXML
    private Button enableRestaurantBtn;

    @FXML
    private Button disableRestaurantBtn;

    @FXML
    private Button backBtn;
    @FXML
    public void initialize() {
        restaurantIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        restaurantNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        restaurantStatusColumn.setCellValueFactory(new PropertyValueFactory<>("isRestaurantConfirmed"));
        backBtn.setOnAction(event -> {
            handleBack();
        });
        disableRestaurantBtn.setOnAction(event -> {
            handleDisableRestaurant();
        });
        enableRestaurantBtn.setOnAction(event -> {
            handleEnableRestaurant();
        });
        seedSampleData();
    }
    private void handleBack() {
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
    }

    private void handleEnableRestaurant() {
        Restaurant selectedRestaurant = restaurantTable.getSelectionModel().getSelectedItem();

        if (selectedRestaurant == null) {
            showAlert("No Selection", "Please select a restaurant from the table.");
            return;
        }

        int id = selectedRestaurant.getId();
        Boolean isRestaurantConfirmed = selectedRestaurant.getIsRestaurantConfirmed();

        if (isRestaurantConfirmed ) {
            showAlert("Restaurant Already Confirmed", "This Restaurant is already confirmed.");
            return;
        }

        AdminNetwork.enableRestaurant(id, apiResponse -> {
            Platform.runLater(() -> {
                if (apiResponse.getStatusCode() == 200) {
                    showAlert(String.valueOf(apiResponse.getStatusCode()), "Restaurant has been successfully enabled.");
                    seedSampleData();
                } else {
                    showAlert("Error " + apiResponse.getStatusCode(), apiResponse.getBody());
                }
            });
        });
    }
    private void handleDisableRestaurant() {
        Restaurant selectedRestaurant = restaurantTable.getSelectionModel().getSelectedItem();

        if (selectedRestaurant == null) {
            showAlert("No Selection", "Please select a restaurant from the table.");
            return;
        }

        int id = selectedRestaurant.getId();
        Boolean isRestaurantConfirmed = selectedRestaurant.getIsRestaurantConfirmed();

        if (! isRestaurantConfirmed ) {
            showAlert("Restaurant Already Confirmed", "This Restaurant is already confirmed.");
            return;
        }

        AdminNetwork.disableRestaurant(id, apiResponse -> {
            Platform.runLater(() -> {
                if (apiResponse.getStatusCode() == 200) {
                    showAlert(String.valueOf(apiResponse.getStatusCode()), "Restaurant has been successfully disabled.");
                    seedSampleData();
                } else {
                    showAlert("Error " + apiResponse.getStatusCode(), apiResponse.getBody());
                }
            });
        });
    }

    public void seedSampleData() {
        AdminNetwork.getAllRestaurants(apiResponse -> {
            Platform.runLater(() -> {
                ObservableList<Restaurant> restaurants = FXCollections.observableArrayList();
                System.out.println(apiResponse.getBody());
                try {
                    JSONArray jsonArray = new JSONArray(apiResponse.getBody());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String fullName = obj.getString("full_name");
                        boolean isRestaurantConfirmed = obj.getBoolean("isRestaurantConfirmed");

                        Restaurant restaurant = new Restaurant(id, fullName, isRestaurantConfirmed);
                        restaurants.add(restaurant);
                    }
                    restaurantTable.setItems(restaurants);

                } catch (Exception e) {
                    e.printStackTrace();
                    SessionManager.showAlert(String.valueOf(apiResponse.getStatusCode()), apiResponse.getBody());
                }
            });
        });
    }
}
