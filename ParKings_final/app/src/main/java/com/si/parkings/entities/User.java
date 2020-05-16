package com.si.parkings.entities;

public class User {
    private String parkingSpotID;
    private String parkingLotID;
    private UserDate enterTime;
    private UserDate exitTime;
    private Float amountToPay;
    private Float cash;
    private int parkingLotPrice;

    public User (){}

    public Float calculateOwedSum(UserDate enterDate, UserDate exitDate, int parkingLotPrice) {
        float hoursNumber = (float) ((exitDate.getSecond() - enterDate.getSecond()) / 3600.0);
        hoursNumber += (exitDate.getMinute() - enterDate.getMinute()) / 60.0;
        hoursNumber += exitDate.getHour() - enterDate.getHour();
        hoursNumber += (exitDate.getDayOfYear() - enterDate.getDayOfYear()) * 24;
        hoursNumber += (exitDate.getYear() - enterDate.getYear()) * 365;
        return hoursNumber * parkingLotPrice;
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
