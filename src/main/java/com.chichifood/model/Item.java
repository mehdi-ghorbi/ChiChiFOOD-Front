package com.chichifood.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty imageBase64 = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    public int getVendorId() {
        return vendorId.get();
    }

    public IntegerProperty vendorIdProperty() {
        return vendorId;
    }

    private final IntegerProperty vendorId = new SimpleIntegerProperty();
    private final IntegerProperty price = new SimpleIntegerProperty();
    private final IntegerProperty supply = new SimpleIntegerProperty();

    private final ListProperty<String> keywords = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Menu> menus = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Item(Integer id, String name, String imageBase64, String description,Integer vendorId, Integer price, Integer supply, List<String> keywords) {
        this.id.set(id);
        this.name.set(name);
        this.imageBase64.set(imageBase64);
        this.description.set(description);
        this.vendorId.set(vendorId);
        this.price.set(price);
        this.supply.set(supply);
        this.keywords.setAll(keywords);
    }
    public Item(){

    }
    private Restaurant restaurant;

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }


    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }


    public String getImageBase64() { return imageBase64.get(); }
    public void setImageBase64(String value) { imageBase64.set(value); }
    public StringProperty imageBase64Property() { return imageBase64; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public int getPrice() { return price.get(); }
    public void setPrice(int value) { price.set(value); }
    public IntegerProperty priceProperty() { return price; }

    public int getSupply() { return supply.get(); }
    public void setSupply(int value) { supply.set(value); }
    public IntegerProperty supplyProperty() { return supply; }

    public List<String> getKeywords() { return keywords.get(); }
    public void setKeywords(List<String> value) { keywords.setAll(value); }
    public ListProperty<String> keywordsProperty() { return keywords; }

    public List<Menu> getMenus() { return menus.get(); }
    public void setMenus(List<Menu> value) { menus.setAll(value); }
    public ListProperty<Menu> menusProperty() { return menus; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
}
