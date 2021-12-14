package com.example.foodappandroid.data.room;

import androidx.room.TypeConverter;

import com.example.foodappandroid.model.FoodModel;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class CartConverter {

    @TypeConverter
    public static FoodModel storedStringToMyObjects(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return new FoodModel();
        }
        Type listType = new TypeToken<FoodModel>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String myObjectsToStoredString(FoodModel myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

}
