package com.chichifood.network;

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

                        callback.accept(new ApiResponse(200, body));

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

                       callback.accept(new ApiResponse(statusCode, body));

                })
                .exceptionally(e -> {
                    callback.accept(new ApiResponse(500, "Request failed: " + e.getMessage()));
                    return null;
                });
    }

    public static void addItem(String restaurantId, JsonObject jsonObject, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/restaurants/" + restaurantId + "/item";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
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

    public static void addMenu(String restaurantId, JsonObject jsonObject, Consumer<ApiResponse> callback) {
        if (SessionManager.getToken() == null || SessionManager.getToken().isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/restaurants/" + restaurantId + "/menu";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + SessionManager.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
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

    public static void deleteMenu(String restaurantId,String title, Consumer<ApiResponse> callback) {
        if (SessionManager.getToken() == null || SessionManager.getToken().isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/restaurants/" + restaurantId + "/menu/" + title;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + SessionManager.getToken())
                .DELETE()
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

    public static void updateItem(String restaurantId, JsonObject jsonObject, String itemID, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/restaurants/" + restaurantId+"/item/" + itemID;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
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

    public static void addItemToMenu(String restaurantId, String title, String itemID, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("item_id", itemID);
        String url = "http://localhost:8569/restaurants/" + restaurantId+"/menu/" + title;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
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

    public static void deleteItemFromMenu(String restaurantId,String title, String itemID, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/restaurants/" + restaurantId+"/menu/" + title + "/" + itemID;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .DELETE()
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

    public static void getAllOrders(String restaurantId, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/orders/getallorders/" + restaurantId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
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

    public static void deleteItem(String restaurantId,String itemID, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/restaurants/" + restaurantId + "/item/" + itemID;
        System.out.println(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .DELETE()
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

    public static void changeOrderStatus(int orderID, String status, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/restaurants/orders/" + orderID;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", status);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
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
