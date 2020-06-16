package com.mechanics.mechapp.customer;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

class MechanicData {

    static MechanicModel convertDocumentToDriverModel(DocumentSnapshot documentSnapshot) {
        String cacImageUrl = "";
        //String cacImageUrl = documentSnapshot.get("CAC Image Url").toString();
        ArrayList<String> categories = (ArrayList<String>) documentSnapshot.get("Categories");
        ArrayList<String> specifications = (ArrayList<String>) documentSnapshot.get("Specifications");
        String city = documentSnapshot.get("City").toString();
        String companyName = documentSnapshot.get("Company Name").toString();
        String description = documentSnapshot.get("Description").toString();
        String email = documentSnapshot.get("Email").toString();
        String imageUrl = documentSnapshot.get("Image Url").toString();
        Double longitude = (Double) documentSnapshot.get("LOc Longitude");
        Double latitude = (Double) documentSnapshot.get("Loc Latitude");
        String locality = documentSnapshot.get("Locality").toString();
       // String password = documentSnapshot.get("Password").toString();
        String phoneNumber = documentSnapshot.get("Phone Number").toString();
        String previousImage1 = documentSnapshot.getString("PreviousImage1 Url");
        String previousImage2 = documentSnapshot.getString("PreviousImage2 Url");
        String streetName = documentSnapshot.get("Street Name").toString();
        String type = documentSnapshot.get("Type").toString();
        String websiteUrl = documentSnapshot.get("Website Url").toString();
        String jobs_done = documentSnapshot.get("Jobs Done").toString();
        String rating = documentSnapshot.get("Rating").toString();
        String review = documentSnapshot.get("Reviews").toString();
        String MechUID = documentSnapshot.get("Mech Uid").toString();

        return new MechanicModel(imageUrl, cacImageUrl, specifications, phoneNumber, latitude, longitude, categories, streetName, type, websiteUrl, previousImage2, previousImage1, "password", locality, city, companyName, description, email, jobs_done, rating, review, MechUID);
    }

}
