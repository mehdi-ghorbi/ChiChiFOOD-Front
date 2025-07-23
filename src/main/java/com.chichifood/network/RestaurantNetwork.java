package com.chichifood.network;

import com.chichifood.model.User;
import com.chichifood.network.ApiResponse;
import com.chichifood.network.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RestaurantNetwork {

    public static void getRestaurants(Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/restaurants/mine"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();
                    ApiResponse apiResponse = new ApiResponse(statusCode, body);
                    callback.accept(apiResponse);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    ApiResponse apiResponse = new ApiResponse(500, "Server Error");
                    callback.accept(apiResponse);
                    return null;
                });
    }

    public static void getItems(String restaurantId, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        String url = "http://localhost:8569/restaurants/getItems/" + restaurantId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();

                    if (statusCode == 200) {
                        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
                        List<Map<String, Object>> items = gson.fromJson(body, type);
                        callback.accept(new ApiResponse(200, items));
                    } else {
                        callback.accept(new ApiResponse(statusCode, body));
                    }
                })
                .exceptionally(e -> {
                    callback.accept(new ApiResponse(500, "Request failed: " + e.getMessage()));
                    return null;
                });
    }
    public static void getMenus(String restaurantId, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        String url = "http://localhost:8569/restaurants/getMenus/" + restaurantId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();

                    if (statusCode == 200) {
                        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
                        List<Map<String, Object>> menus = gson.fromJson(body, type);
                        callback.accept(new ApiResponse(200, menus));
                    } else {
                        callback.accept(new ApiResponse(statusCode, body));
                    }
                })
                .exceptionally(e -> {
                    callback.accept(new ApiResponse(500, "Request failed: " + e.getMessage()));
                    return null;
                });
    }




}
