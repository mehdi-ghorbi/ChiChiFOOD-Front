package com.chichifood.network;

import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.chichifood.model.User;
import com.chichifood.network.ApiResponse;
import com.chichifood.network.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AdminNetwork {
    public static void getAllUsers(Consumer<ApiResponse> callback){
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/admin/users";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();
                    callback.accept(new ApiResponse(statusCode, body));
                })
                .exceptionally(e -> {
                    callback.accept(new ApiResponse(500, "Request failed: " + e.getMessage()));
                    return null;
                });
    }
}
