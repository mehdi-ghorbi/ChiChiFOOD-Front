package com.chichifood.network;

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
}
