package com.mechanics.mechapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mechanics.mechapp.customer.CartModel;

@Database(entities = {CartModel.class}, version = 1)
public abstract class MyCartDatabase extends RoomDatabase {

    public abstract MyCartDao myCartDao();

/*     private static volatile MyCartDatabase INSTANCE;

   static MyCartDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyCartDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyCartDatabase.class, "cart_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }*/
}
