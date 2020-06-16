package com.mechanics.mechapp.customer;

public class Help_and_NotificationModel {
    private String item_message, item_time_value;

    public String getItem_message() {
        return item_message;
    }

    public void setItem_message(String item_message) {
        this.item_message = item_message;
    }

    public String getItem_time_value() {
        return item_time_value;
    }

    public void setItem_time_value(String item_time_value) {
        this.item_time_value = item_time_value;
    }

    public Help_and_NotificationModel(String item_message, String item_time_value) {
        this.item_message = item_message;
        this.item_time_value = item_time_value;
    }
}
