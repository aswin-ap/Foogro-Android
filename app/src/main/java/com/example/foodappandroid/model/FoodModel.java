package com.example.foodappandroid.model;


import java.io.Serializable;

public class FoodModel implements Serializable {

    private String name;
    private String image;
    private String price;
    private String restaurant;
    private int quantity;

    public FoodModel(String name, String image, String price, String restaurant) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.restaurant = restaurant;
    }

    public FoodModel(String name, String image, String price, String restaurant, int quantity) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.restaurant = restaurant;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public FoodModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "FoodModel{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", price='" + price + '\'' +
                ", restaurant='" + restaurant + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
