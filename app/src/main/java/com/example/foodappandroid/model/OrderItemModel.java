package com.example.foodappandroid.model;

import androidx.annotation.NonNull;

public class OrderItemModel {
    private String restaurantName;
    private String date;
    private String amount;
    private String items;
    private String type;
    private String userId;

    public OrderItemModel(String restaurantName, String date, String amount, String items, String type, String userId) {
        this.restaurantName = restaurantName;
        this.date = date;
        this.amount = amount;
        this.items = items;
        this.type = type;
        this.userId = userId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "OrderItemModel{" +
                "restaurantName='" + restaurantName + '\'' +
                ", date='" + date + '\'' +
                ", amount='" + amount + '\'' +
                ", items='" + items + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
