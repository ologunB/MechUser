package com.mechanics.mechapp.customer;

public class CarRecyclerModel {

    public int getCar_mark() {
        return car_mark;
    }

    public void setCar_mark(int car_mark) {
        this.car_mark = car_mark;
    }

    private int car_mark;

    String getCar_brand() {
        return car_brand;
    }

    void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
    }

    String getCar_model() {
        return car_model;
    }

    void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    String getCar_date() {
        return car_date;
    }

    void setCar_date(String car_date) {
        this.car_date = car_date;
    }

    public String getCar_num() {
        return car_num;
    }

    void setCar_num(String car_num) {
        this.car_num = car_num;
    }

    private String car_brand;
    private String car_model;
    private String car_date;
    private String car_num;
    private String car_image;

    String getCar_image() {
        return car_image;
    }

    void setCar_image(String car_image) {
        this.car_image = car_image;
    }

    CarRecyclerModel(String car_brand, String car_model, String car_date, String car_num, String car_image) {
        this.car_brand = car_brand;
        this.car_model = car_model;
        this.car_date = car_date;
        this.car_num = car_num;
        this.car_image = car_image;
    }

    CarRecyclerModel(){}


}
