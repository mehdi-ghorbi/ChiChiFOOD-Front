package com.chichifood.network;

import com.chichifood.model.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class NetworkService {

    public static void login(User user, Consumer<ApiResponse> callback) {
        HttpClient client = HttpClient.newHttpClient();

        String json = String.format("{\"phone\":\"%s\", \"password\":\"%s\"}", user.getPhone(), user.getPassword());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/auth/login"))
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
                    ApiResponse apiResponse = new ApiResponse(500, "Server Error");
                    callback.accept(apiResponse);
                    return null;
                });
    }
    public static void logout(Consumer<ApiResponse> callback) {

    }

}
