package com.chichifood.network;

import javafx.scene.control.Alert;

public class SessionManager {
    private static String token = null;
    private static  String role;
    public static void setRole(String s){
       role = s;
    }
    // ذخیره توکن
    public static void setToken(String t) {
        token = t;
    }
    public static String getRole(){
        return role ;
    }

    // گرفتن توکن
    public static String getToken() {
        return token;
    }

    public static void clearToken() {
        token = null;
    }


    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

