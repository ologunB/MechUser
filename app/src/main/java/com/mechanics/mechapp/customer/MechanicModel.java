package com.mechanics.mechapp.customer;

import java.util.ArrayList;

public class MechanicModel {

    private String imageUrl;
    private String cacImageUrl;
    private ArrayList<String> specificationList;
    private String mechanicPhoneNumber;
    private Double latitude;
    private Double longitude;
    private ArrayList<String> categoryList;
    private String streetName;
    private String type;
    private String websiteUrl;
    private String previousImage2Url;
    private String previousImage1Url;
    private String password;
    private String locality;
    private String city;
    private String companyName;
    private String description;
    private String email;
    private String jobs_done;
    private String rating;
    private String review;
    private String MechUID;


    MechanicModel(String imageUrl, String cacImageUrl,
                  ArrayList<String> specificationList, String mechanicPhoneNumber,
                  Double latitude, Double longitude, ArrayList<String> categoryList,
                  String streetName, String type, String websiteUrl,
                  String previousImage2Url, String previousImage1Url,
                  String password, String locality, String city,
                  String companyName, String description, String email,
                  String jobs_done, String rating, String review, String MechUID) {
        this.imageUrl = imageUrl;
        this.cacImageUrl = cacImageUrl;
        this.specificationList = specificationList;
        this.mechanicPhoneNumber = mechanicPhoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categoryList = categoryList;
        this.streetName = streetName;
        this.type = type;
        this.websiteUrl = websiteUrl;
        this.previousImage2Url = previousImage2Url;
        this.previousImage1Url = previousImage1Url;
        this.password = password;
        this.locality = locality;
        this.city = city;
        this.companyName = companyName;
        this.description = description;
        this.email = email;
        this.jobs_done = jobs_done;
        this.rating = rating;
        this.review = review;
        this.MechUID = MechUID;
    }

    String getJobs_done() {
        return jobs_done;
    }

    String getRating() {
        return rating;
    }

    String getReview() {
        return review;
    }

    String getMechUID() {
        return MechUID;
    }

    String getImageUrl() {
        return imageUrl;
    }

    String getMechanicPhoneNumber() {
        return mechanicPhoneNumber;
    }


    Double getLatitude() {
        return latitude;
    }

    Double getLongitude() {
        return longitude;
    }

    ArrayList<String> getCategoryList() {
        return categoryList;
    }

    String getStreetName() {
        return streetName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String getLocality() {
        return locality;
    }

    String getCity() {
        return city;
    }

    String getCompanyName() {
        return companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}