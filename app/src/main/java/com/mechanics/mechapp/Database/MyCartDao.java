package com.mechanics.mechapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mechanics.mechapp.customer.CartModel;

import java.util.List;

@Dao
public interface MyCartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPost(CartModel cartModel);

    @Query("SELECT * FROM CartItems")
    List<CartModel> getItems();

    @Delete
    void delete(CartModel cartModel);

    @Query("DELETE FROM CartItems")
    void deleteAll();


}
