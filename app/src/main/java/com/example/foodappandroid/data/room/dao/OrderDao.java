package com.example.foodappandroid.data.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.example.foodappandroid.data.room.CartConverter;
import com.example.foodappandroid.data.room.entity.Cart;
import com.example.foodappandroid.model.FoodModel;

import java.util.List;

@Dao
public interface OrderDao {

    @Query("SELECT * from cart")
    List<Cart> getAll();

    @Query("SELECT * from cart WHERE user_id = :userId")
    List<Cart> getOrderByUserId(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Cart cart);

    @Query("DELETE FROM cart WHERE orderId = :id")
    void deleteCartById(int id);

    @TypeConverters(CartConverter.class)
    @Query("SELECT EXISTS(SELECT * FROM cart WHERE user_id = :userId AND food = :foodModel)")
    Boolean isFoodIsExist(String userId, FoodModel foodModel);

    @Query("DELETE FROM cart")
    void deleteAll();

}
