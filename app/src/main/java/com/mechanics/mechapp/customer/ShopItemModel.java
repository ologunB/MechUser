package com.mechanics.mechapp.customer;

import java.util.List;

class ShopItemModel {
    private String shop_item_ID;
    private String shop_item_name;

    public String getShop_itemType() {
        return shop_itemType;
    }

    private String shop_itemType;
    private String shop_item_price;
    private String shop_item_seller;
    private String shop_item_email;
    private String shop_item_phoneNo;
    private String shop_item_descrpt;
    private List<String> shop_item_image;

    String getShop_item_ID() {
        return shop_item_ID;
    }

    ShopItemModel(String shop_item_name, String shop_item_price,
                  String shop_item_seller, String shop_item_email,
                  String shop_item_phoneNo, String shop_item_descrpt,
                  List<String> shop_item_image, String shop_item_ID, String shop_item_type) {
        this.shop_item_name = shop_item_name;
        this.shop_item_price = shop_item_price;
        this.shop_item_seller = shop_item_seller;
        this.shop_item_email = shop_item_email;
        this.shop_item_phoneNo = shop_item_phoneNo;
        this.shop_item_descrpt = shop_item_descrpt;
        this.shop_item_image = shop_item_image;
        this.shop_item_ID = shop_item_ID;
        this.shop_itemType = shop_item_type;
    }

    String getShop_item_name() {
        return shop_item_name;
    }

    List<String> getShop_item_image() {
        return shop_item_image;
    }

    String getShop_item_price() {
        return shop_item_price;
    }

    String getShop_item_seller() {
        return shop_item_seller;
    }

    String getShop_item_email() {
        return shop_item_email;
    }

    String getShop_item_phoneNo() {
        return shop_item_phoneNo;
    }


    String getShop_item_descrpt() {
        return shop_item_descrpt;
    }


}
