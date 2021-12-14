package com.example.foodappandroid.model;

public class ShopModel {
    public String name;
    public String Image;
    public String open;
    public String rating;

    public ShopModel(String name, String image, String open, String rating) {
        this.name = name;
        Image = image;
        this.open = open;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
