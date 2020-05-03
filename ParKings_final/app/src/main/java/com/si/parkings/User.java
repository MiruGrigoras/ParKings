package com.si.parkings;

import java.util.Date;

public class User {
    private String uid;
    private String parkingSpotID;
    private String parkingLotID;
    private Date enterTime;
    private Date exitTime;
    private Float amountToPay;
    private Float cash;

    public User(String uid){
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getParkingSpotID() {
        return parkingSpotID;
    }

    public void setParkingSpotID(String parkingSpotID) {
        this.parkingSpotID = parkingSpotID;
    }

    public Date getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }

    public Float getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(Float amountToPay) {
        this.amountToPay = amountToPay;
    }

    public String getParkingLotID() {
        return parkingLotID;
    }

    public void setParkingLotID(String parkingLotID) {
        this.parkingLotID = parkingLotID;
    }

    public Float getCash() {
        return cash;
    }

    public void setCash(Float cash) {
        this.cash = cash;
    }
}
