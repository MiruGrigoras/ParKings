package com.si.parkings.entities;

public class User {
    private String uid;
    private String parkingSpotID;
    private String parkingLotID;
    private UserDate enterTime;
    private UserDate exitTime;
    private Float amountToPay;
    private Float cash;
    private int parkingLotPrice;

    public User (){}

    public User(String uid){
        this.uid = uid;
    }

    public Float calculateOwedSum(UserDate enterDate, UserDate exitDate, int parkingLotPrice) {
        float hoursNumber = (float) ((enterDate.getSecond() - exitDate.getSecond()) / 3600.0);
        hoursNumber += (enterDate.getMinute() - exitDate.getMinute()) / 60.0;
        hoursNumber += enterDate.getHour() - exitDate.getHour();
        hoursNumber += (enterDate.getDayOfYear() - exitDate.getDayOfYear()) * 24;
        hoursNumber += (enterDate.getYear() - exitDate.getYear()) * 365;
        return hoursNumber * parkingLotPrice;
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

    public UserDate getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(UserDate enterTime) {
        this.enterTime = enterTime;
    }

    public UserDate getExitTime() {
        return exitTime;
    }

    public void setExitTime(UserDate exitTime) {
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

    public int getParkingLotPrice() {
        return parkingLotPrice;
    }

    public void setParkingLotPrice(int parkingLotPrice) {
        this.parkingLotPrice = parkingLotPrice;
    }

}
