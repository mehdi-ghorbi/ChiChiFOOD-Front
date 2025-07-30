package com.chichifood.localdata;

import com.chichifood.model.Item;

import java.util.*;

public class CartManager {

    private static final Map<Integer, List<CartItem>> cartsByVendorId = new HashMap<>();

    public static void addItemToCart(Item item) {
        int vendorId = item.getVendorId();

        List<CartItem> cartItems = cartsByVendorId.getOrDefault(vendorId, new ArrayList<>());

        Optional<CartItem> existing = cartItems.stream()
                .filter(ci -> ci.getItem().getId() == item.getId())
                .findFirst();

        if (existing.isPresent()) {
            existing.get().incrementQuantity();
        } else {
            cartItems.add(new CartItem(item, 1));
        }

        cartsByVendorId.put(vendorId, cartItems);
    }

    public static void decreaseItemQuantity(Item item) {
        int vendorId = item.getVendorId();

        List<CartItem> cartItems = cartsByVendorId.get(vendorId);
        if (cartItems == null) return;

        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem ci = iterator.next();
            if (ci.getItem().getId() == item.getId()) {
                if (ci.getQuantity() > 1) {
                    ci.setQuantity(ci.getQuantity() - 1);
                } else {
                    iterator.remove();
                }
                break;
            }
        }

        if (cartItems.isEmpty()) {
            cartsByVendorId.remove(vendorId);
        }
    }

    public static List<CartItem> getCartForVendor(int vendorId) {
        return cartsByVendorId.getOrDefault(vendorId, new ArrayList<>());
    }

    public static Map<Integer, List<CartItem>> getAllCarts() {
        return cartsByVendorId;
    }

    public static void clearCart(int vendorId) {
        cartsByVendorId.remove(vendorId);
    }

    public static void clearAllCarts() {
        cartsByVendorId.clear();
    }

    public static int calculateTotalPrice(int vendorId) {
        List<CartItem> items = cartsByVendorId.get(vendorId);
        if (items == null) return 0;

        int total = 0;
        for (CartItem ci : items) {
            total += ci.getItem().getPrice() * ci.getQuantity();
        }
        return total;
    }
}
