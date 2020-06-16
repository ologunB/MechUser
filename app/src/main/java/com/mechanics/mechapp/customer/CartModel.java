package com.mechanics.mechapp.customer;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CartItems")
public class CartModel {

    @NonNull
    @PrimaryKey
    private String _productId;

    public String get_productId() {
        return _productId;
    }


    private String name, price, seller, image, status;

    public CartModel(String _productId, String name, String price, String seller, String image) {
        this.name = name;
        this.price = price;
        this.seller = seller;
        this.image = image;
        this._productId = _productId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }


    public String getSeller() {
        return seller;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
