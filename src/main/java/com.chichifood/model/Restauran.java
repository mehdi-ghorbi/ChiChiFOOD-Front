package com.chichifood.model;

public class Restauran {

    private int id;
    private String name;
    private String address;
    private String phone;
    private String logoBase64;
    private Integer taxFee;
    private Integer additionalFee;
    private boolean isRestaurantConfirmed;

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

    public Integer getTaxFee() {
        return taxFee;
    }

    public void setTaxFee(Integer taxFee) {
        this.taxFee = taxFee;
    }

    public Integer getAdditionalFee() {
        return additionalFee;
    }

    public void setAdditionalFee(Integer additionalFee) {
        this.additionalFee = additionalFee;
    }

    public boolean isRestaurantConfirmed() {
        return isRestaurantConfirmed;
    }

    public void setRestaurantConfirmed(boolean restaurantConfirmed) {
        isRestaurantConfirmed = restaurantConfirmed;
    }
}
