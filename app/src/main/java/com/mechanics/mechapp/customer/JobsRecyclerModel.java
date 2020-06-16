package com.mechanics.mechapp.customer;

public class JobsRecyclerModel {
    public String getUID() {
        return UID;
    }


    private String UID;
    private String amount_paid;
    private String car_type;
     private String transact_time;
    private String transact_history;



    private String serverData;
    private String Image;
    private String Name;
    private String Number;
    private String MyOwnName;
    private String TransactionID;
    public String getServerData() {
        return serverData;
    }
    public String getHasReviewed() {
        return hasReviewed;
    }

    private String hasReviewed;

    public String getCusConfirmation() {
        return cusConfirmation;
    }


    private String cusConfirmation;

    public String getMechConfirmation() {
        return mechConfirmation;
    }

    private String mechConfirmation;

    public String getTransactionID() {
        return TransactionID;
    }


    public String getMyOwnName() {
        return MyOwnName;
    }


    public String getAmount_paid() {
        return amount_paid;
    }


    public String getCar_type() {
        return car_type;
    }


    public String getTransact_time() {
        return transact_time;
    }


    public JobsRecyclerModel(String amount_paid, String car_type, String transact_time,
                             String transact_history, String name, String number,
                             String transactionID, String cusConfirmation, String mechConfirmation, String UID, String serverData) {
        this.amount_paid = amount_paid;
        this.car_type = car_type;
        this.transact_time = transact_time;
        this.transact_history = transact_history;
        this.cusConfirmation = cusConfirmation;
        this.mechConfirmation = mechConfirmation;
        this.serverData = serverData;
        this.UID = UID;
        Name = name;
        Number = number;
        TransactionID = transactionID;
    }

    public JobsRecyclerModel(String amount_paid, String car_type, String transact_time,
                             String transact_history, String image, String name, String number,
                             String customerName, String transactionID, String cusConfirmation,
                             String UID, String mechConfirmation, String hasReviewed, String serverData) {
        this.amount_paid = amount_paid;
        this.car_type = car_type;
        this.transact_time = transact_time;
        this.transact_history = transact_history;
        Image = image;
        Name = name;
        Number = number;
        this.MyOwnName = customerName;
        TransactionID = transactionID;
        this.serverData = serverData;

        this.cusConfirmation = cusConfirmation;
        this.UID = UID;
        this.hasReviewed = hasReviewed;
        this.mechConfirmation = mechConfirmation;

    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}
