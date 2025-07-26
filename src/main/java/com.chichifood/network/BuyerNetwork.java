package com.chichifood.network;

import com.chichifood.model.Restaurant;
import com.chichifood.model.Item;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.*;
import java.util.function.Consumer;

public class BuyerNetwork {
    public static void getVendorsList(String vendorName, List<String> keywords, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("search", vendorName);

        JsonArray keywordsArray = new JsonArray();
        for (String keyword : keywords) {
            keywordsArray.add(keyword);
        }
        jsonRequest.add("keywords", keywordsArray);
        String json = new Gson().toJson(jsonRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/vendors"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();
                    ApiResponse apiResponse = new ApiResponse(statusCode, body);
                    callback.accept(apiResponse);
                })
                .exceptionally(e -> {
                    ApiResponse apiResponse = new ApiResponse(500, "Server Error: " + e.getMessage());
                    callback.accept(apiResponse);
                    return null;
                });
    }


    public static void getItemsList(String itemName, int itemPrice, List<String> keywords, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();

        // ساخت JSON با Gson
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("search", itemName);
        jsonRequest.addProperty("price", itemPrice);

        JsonArray keywordsArray = new JsonArray();
        for (String keyword : keywords) {
            keywordsArray.add(keyword);
        }
        jsonRequest.add("keywords", keywordsArray);

        String json = new Gson().toJson(jsonRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/items"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();
                    callback.accept(new ApiResponse(statusCode, body));
                })
                .exceptionally(e -> {
                    callback.accept(new ApiResponse(500, "Server Error: " + e.getMessage()));
                    return null;
                });
    }
}
