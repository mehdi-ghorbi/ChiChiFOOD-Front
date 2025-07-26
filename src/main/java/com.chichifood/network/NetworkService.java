package com.chichifood.network;

import com.chichifood.model.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static com.chichifood.network.SessionManager.*;

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
    public static void signup(User user, String base64Image, Consumer<ApiResponse> callback) {
        HttpClient client = HttpClient.newHttpClient();
        String json = String.format("""
            {
                "full_name": "%s",
                "phone": "%s",
                "email": "%s",
                "password": "%s",
                "role": "%s",
                "address": "%s",
                "profileImageBase64": "%s",
                "bank_info": {
                    "bank_name": "%s",
                    "account_number": "%s"
                }
            }
            """,
                user.getFullName(),
                user.getPhone(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getAddress(),
                base64Image,
                user.getBankName(),
                user.getAccountNumber()
        );


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    ApiResponse apiResponse = new ApiResponse(response.statusCode(), response.body());
                    callback.accept(apiResponse);
                })
                .exceptionally(e -> {
                    callback.accept(new ApiResponse(500, "Server Error"));
                    return null;
                });
    }

    public static void getProfile(Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/auth/profile"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    ApiResponse apiResponse = new ApiResponse(response.statusCode(), response.body());
                    callback.accept(apiResponse);
                })
                .exceptionally(e -> {
                    callback.accept(new ApiResponse(500, "Server Error"));
                    return null;
                });
    }

    public static void updateProfile(JsonObject jsonObject, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/auth/profile"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    ApiResponse apiResponse = new ApiResponse(response.statusCode(), response.body());
                    callback.accept(apiResponse);
                })
                .exceptionally(e -> {
                    callback.accept(new ApiResponse(500, "Server Error: " + e.getMessage()));
                    return null;
                });
    }

}