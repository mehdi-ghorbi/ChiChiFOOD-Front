package com.chichifood.network;

import com.chichifood.model.Menu;
import com.chichifood.model.Restaurant;
import com.chichifood.model.Item;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.*;

import java.net.URI;
import java.net.http.*;
import java.util.function.Consumer;

public class BuyerNetwork {
    public static void walletTopUp(int amount, Consumer<ApiResponse> consumer) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            consumer.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("amount", amount);
        System.out.println(jsonRequest);
        String json = new Gson().toJson(jsonRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/payment/online"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> consumer.accept(new ApiResponse(response.statusCode(), response.body())))
                .exceptionally(e -> {
                    consumer.accept(new ApiResponse(500, "Server Error: " + e.getMessage()));
                    return null;
                });
    }
    public static void updateOrder(int orderId, String method, Consumer<ApiResponse> consumer){
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            consumer.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("status", method);


        System.out.println(jsonRequest);
        String json = new Gson().toJson(jsonRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/orders/status/" + orderId))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> consumer.accept(new ApiResponse(response.statusCode(), response.body())))
                .exceptionally(e -> {
                    consumer.accept(new ApiResponse(500, "Server Error: " + e.getMessage()));
                    return null;
                });
    }
    public static void pay(int orderId, String method, Consumer<ApiResponse> consumer) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            consumer.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("order_id", orderId);
        jsonRequest.addProperty("method", method);
        System.out.println(jsonRequest);
        String json = new Gson().toJson(jsonRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/payment/online"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> consumer.accept(new ApiResponse(response.statusCode(), response.body())))
                .exceptionally(e -> {
                    consumer.accept(new ApiResponse(500, "Server Error: " + e.getMessage()));
                    return null;
                });
    }
    public static void getTransactions(Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/transactions";
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
    public static void getAllUserOrders(int userId, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8569/orders/getalluserorders/";
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

    public static void submitOrder(String deliveryAddress, int vendorId, Integer couponId, List<ItemRequest> items, Consumer<ApiResponse> consumer) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            consumer.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("delivery_address", deliveryAddress);
        jsonRequest.addProperty("vendor_id", vendorId);
        if (couponId != null) {
            jsonRequest.addProperty("coupon_id", couponId);
        }

        JsonArray itemsArray = new JsonArray();
        for (ItemRequest item : items) {
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("item_id", item.itemId());
            itemJson.addProperty("quantity", item.quantity());
            itemsArray.add(itemJson);
        }
        jsonRequest.add("items", itemsArray);

        String json = new Gson().toJson(jsonRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/orders"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> consumer.accept(new ApiResponse(response.statusCode(), response.body())))
                .exceptionally(e -> {
                    consumer.accept(new ApiResponse(500, "Server Error: " + e.getMessage()));
                    return null;
                });
    }

    public static void getOrder(int orderId, Consumer<ApiResponse> consumer) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            consumer.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/orders/" + orderId))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> consumer.accept(new ApiResponse(response.statusCode(), response.body())))
                .exceptionally(e -> {
                    consumer.accept(new ApiResponse(500, "Server Error: " + e.getMessage()));
                    return null;
                });
    }

    public static void serachOrders(String itemName, String vendorName, Consumer<ApiResponse> consumer) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            consumer.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        StringBuilder uriBuilder = new StringBuilder("http://localhost:8569/orders/history?");
        if (itemName != null && !itemName.isEmpty()) {
            uriBuilder.append("search=").append(itemName).append("&");
        }
        if (vendorName != null && !vendorName.isEmpty()) {
            uriBuilder.append("vendor=").append(vendorName);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriBuilder.toString()))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> consumer.accept(new ApiResponse(response.statusCode(), response.body())))
                .exceptionally(e -> {
                    consumer.accept(new ApiResponse(500, "Server Error: " + e.getMessage()));
                    return null;
                });
    }

    // کلاس کمکی برای ساختن JSON آیتم‌ها
    public record ItemRequest(int itemId, int quantity) {}

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

    public static void getVendorMenus(int vendorId, Consumer<Restaurant> consumer) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            System.out.println("Unauthorized: Token is missing");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/vendors/" + vendorId))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();

                    if (statusCode == 200) {
                        try {
                            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

                            // گرفتن اطلاعات رستوران
                            JsonObject vendorJson = json.getAsJsonObject("vendor");
                            Restaurant vendor = new Restaurant(
                                    vendorJson.get("id").getAsInt(),
                                    vendorJson.get("name").getAsString(),
                                    vendorJson.get("address").getAsString(),
                                    vendorJson.get("phone").getAsString(),
                                    vendorJson.get("logoBase64").getAsString(),
                                    vendorJson.get("tax_fee").getAsInt(),
                                    vendorJson.get("additional_fee").getAsInt()
                            );



                            JsonArray menuTitlesArray = json.getAsJsonArray("menu_titles");
                            List<Menu> menus = new ArrayList<>();

                            for (JsonElement menuIdElem : menuTitlesArray) {
                                String menuId = menuIdElem.getAsString();

                                JsonArray itemsArray = json.getAsJsonArray(menuId);
                                List<Item> items = new ArrayList<>();

                                for (JsonElement itemElement : itemsArray) {
                                    JsonObject itemJson = itemElement.getAsJsonObject();

                                    List<String> keywords = new ArrayList<>();
                                    for (JsonElement k : itemJson.getAsJsonArray("keywords")) {
                                        keywords.add(k.getAsString());
                                    }

                                    Item item = new Item(
                                            itemJson.get("id").getAsInt(),
                                            itemJson.get("name").getAsString(),
                                            itemJson.get("imageBase64").getAsString(),
                                            itemJson.get("description").getAsString(),
                                            itemJson.get("vendor_id").getAsInt(),
                                            itemJson.get("price").getAsInt(),
                                            itemJson.get("supply").getAsInt(),
                                            keywords
                                    );
                                    items.add(item);
                                }

                                Menu menu = new Menu(menuId, items);
                                menus.add(menu);
                            }

                            vendor.setMenus(menus);
                            consumer.accept(vendor);

                        } catch (Exception e) {
                            System.out.println("Error parsing response: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Server error (" + statusCode + "): " + body);
                    }
                })
                .exceptionally(e -> {
                    System.out.println("Connection error: " + e.getMessage());
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

    public static void addFavoritesRestaurants(int id, Consumer<ApiResponse> callback){
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/favorites/" + id))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .thenAccept(status -> {
                    if (status == 200)
                        callback.accept(new ApiResponse(200, "Added successfully"));
                    else
                        callback.accept(new ApiResponse(status, "Failed to add"));
                })
                .exceptionally(ex -> {
                    callback.accept(new ApiResponse(500, "Network error: " + ex.getMessage()));
                    return null;
                });

    }

    public static void removeFavoritesRestaurants(int id, Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/favorites/" + id))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .thenAccept(status -> {
                    if (status == 200)
                        callback.accept(new ApiResponse(200, "Deleted successfully"));
                    else
                        callback.accept(new ApiResponse(status, "Failed to delete"));
                })
                .exceptionally(ex -> {
                    callback.accept(new ApiResponse(500, "Network error: " + ex.getMessage()));
                    return null;
                });
    }

    public static void getFavoritesRestaurants(Consumer<ApiResponse> callback) {
        String token = SessionManager.getToken();
        if (token == null || token.isEmpty()) {
            callback.accept(new ApiResponse(401, "Unauthorized: Token is missing"));
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8569/favorites"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> callback.accept(new ApiResponse(200, body)))
                .exceptionally(ex -> {
                    callback.accept(new ApiResponse(500, "Network error: " + ex.getMessage()));
                    return null;
                });
    }
}