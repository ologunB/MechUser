package com.mechanics.mechapp.customer;

public class Models {

    private String price, soldBy, materialName, serviceName, carBrand,
            carModel, carYear, carRegNo, cardCVV, cardNUMBER, cardEXPIRY, nearbyMechName, nearbyMechHowfar, nearbyMechNumber;
    private int picture, serviceLogo, carImage, nearbyMechPicture;
    private double mLong, mLat;


    public Models(String cardCVV, String cardNUMBER, String cardEXPIRY) {

        this.cardCVV = cardCVV;
        this.cardNUMBER = cardNUMBER;
        this.cardEXPIRY = cardEXPIRY;
    }

    public Models(String carBrand, String carModel, String carYear, String carRegNo, int carImage) {

        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carYear = carYear;
        this.carRegNo = carRegNo;
        this.carImage = carImage;
    }

    public Models(String price, String soldBy, String materialName, int picture) {

        this.price = price;
        this.soldBy = soldBy;
        this.materialName = materialName;
        this.picture = picture;
    }

    public Models(int nearbyMechPicture, String nearbyMechName, String nearbyMechHowfar, String nearbyMechNumber, double mLat, double mLong) {

        this.nearbyMechName = nearbyMechName;
        this.nearbyMechHowfar = nearbyMechHowfar;
        this.nearbyMechNumber = nearbyMechNumber;
        this.nearbyMechPicture = nearbyMechPicture;
        this.mLat = mLat;
        this.mLong = mLong;
    }


    public Models(){}

    public double getmLong() {
        return mLong;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }


    public String getCardCVV() {
        return cardCVV;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }

    public String getCardNUMBER() {
        return cardNUMBER;
    }

    public void setCardNUMBER(String cardNUMBER) {
        this.cardNUMBER = cardNUMBER;
    }

    public String getCardEXPIRY() {
        return cardEXPIRY;
    }

    public void setCardEXPIRY(String cardEXPIRY) {
        this.cardEXPIRY = cardEXPIRY;
    }

    public String getNearbyMechName() {
        return nearbyMechName;
    }

    public void setNearbyMechName(String nearbyMechName) {
        this.nearbyMechName = nearbyMechName;
    }

    public String getNearbyMechHowfar() {
        return nearbyMechHowfar;
    }

    public void setNearbyMechHowfar(String nearbyMechHowfar) {
        this.nearbyMechHowfar = nearbyMechHowfar;
    }

    public String getNearbyMechNumber() {
        return nearbyMechNumber;
    }

    public void setNearbyMechNumber(String nearbyMechNumber) {
        this.nearbyMechNumber = nearbyMechNumber;
    }

    public int getNearbyMechPicture() {
        return nearbyMechPicture;
    }

    public void setNearbyMechPicture(int nearbyMechPicture) {
        this.nearbyMechPicture = nearbyMechPicture;
    }


    public String getServiceName() {
        return serviceName;

    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public String getCarRegNo() {
        return carRegNo;
    }

    public void setCarRegNo(String carRegNo) {
        this.carRegNo = carRegNo;
    }

    public int getCarImage() {
        return carImage;
    }

    public void setCarImage(int carImage) {
        this.carImage = carImage;
    }



    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServiceLogo() {
        return serviceLogo;
    }

    public void setServiceLogo(int serviceLogo) {
        this.serviceLogo = serviceLogo;
    }

    public Models(String serviceName, int serviceLogo) {

        this.serviceName = serviceName;
        this.serviceLogo = serviceLogo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(String soldBy) {
        this.soldBy = soldBy;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    String Email;



    public String getEmail() {
        return Email;
    }

}
