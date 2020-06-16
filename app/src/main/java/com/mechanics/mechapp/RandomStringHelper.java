package com.mechanics.mechapp;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.util.Calendar;

public class RandomStringHelper {
    @NonNull
    public static String randomString() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder stringBuilder = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            stringBuilder.append(AlphaNumericString.charAt(index));
        }
        return stringBuilder.toString();
    }

    @NonNull
    public static String presentTimeString() {
        return DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    }
}
