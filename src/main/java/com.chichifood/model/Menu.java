package com.chichifood.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Menu {

    private LongProperty id = new SimpleLongProperty();
    private StringProperty title = new SimpleStringProperty();
    private ObjectProperty<Restaurant> restaurant = new SimpleObjectProperty<>();
    private ObservableList<Item> items = FXCollections.observableArrayList();

    public Menu() {
    }
    public Menu(String title, List<Item> items) {
        this.title.set(title);
        this.items.setAll(items);
    }
    // id property
    public long getId() {
        return id.get();
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public LongProperty idProperty() {
        return id;
    }

    // title property
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    // restaurant property
    public Restaurant getRestaurant() {
        return restaurant.get();
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant.set(restaurant);
    }

    public ObjectProperty<Restaurant> restaurantProperty() {
        return restaurant;
    }

    // items property
    public ObservableList<Item> getItems() {
        return items;
    }

    public void setItems(ObservableList<Item> items) {
        this.items = items;
    }
}
