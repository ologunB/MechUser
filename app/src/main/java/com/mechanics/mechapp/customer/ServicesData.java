package com.mechanics.mechapp.customer;

import com.mechanics.mechapp.R;

import java.util.ArrayList;
import java.util.List;

public class ServicesData {
    public static List<Models> list;

    ServicesData() {
        list = new ArrayList<>();
        list.add(new Models("Accidented Vehicle", R.drawable.ic_accident));
        list.add(new Models("Air Conditioner", R.drawable.ic_fan));
        list.add(new Models("Brake System", R.drawable.ic_brakes));
        list.add(new Models("Brake pad replacement", R.drawable.ic_brakes));
        list.add(new Models("Call Us", R.drawable.ic_worker));
        list.add(new Models("Car Scan", R.drawable.ic_car));
        list.add(new Models("Car Tint", R.drawable.ic_spray_gun));
        list.add(new Models("Electrician", R.drawable.ic_electrician));
        list.add(new Models("Engine Expert", R.drawable.ic_engine));
        list.add(new Models("Exhaust System", R.drawable.ic_exhaust_pipe));
        list.add(new Models("Locking & Keys/Security", R.drawable.ic_car_key));
        list.add(new Models("Oil & Filter Change", R.drawable.ic_oil));
        list.add(new Models("Painter", R.drawable.ic_airbrush));
        list.add(new Models("Panel Beater", R.drawable.ic_spray_gun));
        list.add(new Models("Tow trucks", R.drawable.ic_crane_truck));
        list.add(new Models("Upholstery & Interior", R.drawable.ic_seat));
        list.add(new Models("Wheel Balancing & Alignment", R.drawable.ic_tire));
    }

    public List<Models> getList() {
        return list;
    }
}