package com.si.parkings.entities;

public class UserDate {
    private int year;
    private int day_of_year;
    private int hour;
    private int minute;
    private int second;

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public int getDayOfYear() {
        return day_of_year;
    }

    public void setDayOfYear(int dayOfYear) {
        this.day_of_year = dayOfYear;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
