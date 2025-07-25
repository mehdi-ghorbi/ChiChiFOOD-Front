package com.chichifood.controller;

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
import com.chichifood.model.User;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static com.chichifood.network.SessionManager.showAlert;

public class AdminUsersPanelController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> userIdColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> userStatusColumn;

    @FXML
    private Button enableUserBtn;

    @FXML
    private Button disableUserBtn;

    @FXML
    private Button backBtn;

    @FXML
    public void initialize() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userStatusColumn.setCellValueFactory(new PropertyValueFactory<>("isUserConfirmed"));

        seedSampleData();
    }

    @FXML
    private void handleEnableUser() {
        // TODO: کد فعال‌سازی کاربر
        System.out.println("Enable user clicked");
    }

    @FXML
    private void handleDisableUser() {
        // TODO: کد غیرفعال‌سازی کاربر
        System.out.println("Disable user clicked");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/adminPanel.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin - Users");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Cannot load the admin users panel.");
        }
    }

    public void seedSampleData() {
        AdminNetwork.getAllUsers(apiResponse -> {
            Platform.runLater(() -> {
                ObservableList<User> users = FXCollections.observableArrayList();
                System.out.println(apiResponse.getBody());
                try {
                    JSONArray jsonArray = new JSONArray(apiResponse.getBody());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String fullName = obj.getString("full_name");
                        int isUserConfirmed = obj.getInt("isUserConfirmed");

                        User user = new User(id, fullName, isUserConfirmed);
                        users.add(user);
                    }
                    userTable.setItems(users);

                } catch (Exception e) {
                    e.printStackTrace(); // یا نمایش پیام خطا
                    SessionManager.showAlert(String.valueOf(apiResponse.getStatusCode()), apiResponse.getBody());
                }
            });
        });
    }
}
