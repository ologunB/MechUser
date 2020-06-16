package com.mechanics.mechapp.customer;

 import java.util.List;

public class CartHistoryModel {

    private String TransId;
    private String price;
    private String status;
    private String cusName;
    private String cusUid;
    private String cusNumber;
    private String cusEmail;
    private String Streetname;

    private String city;
    private  List<String> itemNames;
    private  List<String> itemSellers;
    private  List<Integer> itemNumbers;
    private  List<String> itemImages;

  CartHistoryModel(String name, String price,  List<String> sellers,
                            String cusUid, String cusEmail, String cusNumber, String streetname,
                            List<String> items, List<Integer> itemNumbers, String transId,
                            String city, List<String>  itemImages, String status) {
        this.cusName = name;
        this.price = price;
        this.itemSellers = sellers;
         this.cusUid = cusUid;
        this.cusEmail = cusEmail;
        this.itemNames = items;
        this.cusNumber = cusNumber;
        this.Streetname = streetname;
        this.itemNumbers = itemNumbers;
        this.TransId = transId;
        this.city = city;
        this.itemImages = itemImages;
        this.status = status;
    }

    List<String> getItemImages() {
        return itemImages;
    }

    public String getCity() {
        return city;
    }

    List<Integer> getItemNumbers() {
        return itemNumbers;
    }

    List<String> getItemNames() {
        return itemNames;
    }

      String getTransId() {
        return TransId;
    }

      String getCusNumber() {
        return cusNumber;
    }

      String getCusEmail() {
        return cusEmail;
    }

      String getStreetname() {
        return Streetname;
    }

      String getCusUid() {
        return cusUid;
    }

      String getCusName() {
        return cusName;
    }


    public String getPrice() {
        return price;
    }


      List<String> getItemSellers() {
        return itemSellers;
    }

      String getStatus() {
        return status;
    }

}
