package com.example.foodappandroid.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.foodappandroid.data.room.dao.OrderDao;
import com.example.foodappandroid.data.room.entity.Cart;

@Database(entities = {Cart.class}, version = 2)
public abstract class CartDatabase extends RoomDatabase {
    private static CartDatabase instance;

    public abstract OrderDao orderDao();

    public static CartDatabase getAppDatabase(Context context) {
        if (instance == null) {
            instance =
                    Room.databaseBuilder(context.getApplicationContext(), CartDatabase.class, "cart-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }


}
