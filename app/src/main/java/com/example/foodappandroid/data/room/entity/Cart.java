package com.example.foodappandroid.data.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.foodappandroid.data.room.CartConverter;
import com.example.foodappandroid.model.FoodModel;

@Entity(tableName = "cart")
public class Cart {

    @PrimaryKey(autoGenerate = true)
    public int orderId;

    @ColumnInfo(name = "user_id")
    public String userId;

    @TypeConverters(CartConverter.class)
    //@ColumnInfo(name = "food")
    public FoodModel food;

    @ColumnInfo(name = "status")
    public int status;

    @ColumnInfo(name = "quantity")
    public int quantity;


}
