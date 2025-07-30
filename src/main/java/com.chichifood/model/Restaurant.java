package com.chichifood.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    private int id;
    private String name;
    private String address;
    private String phone;
    private String logoBase64;
    private int taxFee;
    private int additionalFee;
    private boolean isRestaurantConfirmed;
    private List<Menu> menus = new ArrayList<>();


    public List<Menu> getMenus() { return menus; }
    public void setMenus(List<Menu> menus) { this.menus = menus; }

    public Restaurant(int id, String name, String address, String phone, String logoBase64, int taxFee, int additionalFee) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.logoBase64 = logoBase64;
        this.taxFee = taxFee;
        this.additionalFee = additionalFee;
    }
    public Restaurant(int id, String name, boolean isRestaurantConfirmed) {
        this.id = id;
        this.name = name;
        this.isRestaurantConfirmed = isRestaurantConfirmed;
    }
    public Restaurant(String name, String address, String phone, String logoBase64) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.logoBase64 = logoBase64;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogoBase64() {
        return logoBase64;
    }

    public void setLogoBase64(String logoBase64) {
        this.logoBase64 = logoBase64;
    }

    public int getTaxFee() {
        return taxFee;
    }

    public void setTaxFee(int taxFee) {
        this.taxFee = taxFee;
    }

    public int getAdditionalFee() {
        return additionalFee;
    }

    public void setAdditionalFee(int additionalFee) {
        this.additionalFee = additionalFee;
    }

    public boolean getIsRestaurantConfirmed() {
        return isRestaurantConfirmed;
    }

    public void setRestaurantConfirmed(boolean restaurantConfirmed) {
        isRestaurantConfirmed = restaurantConfirmed;
    }
}
