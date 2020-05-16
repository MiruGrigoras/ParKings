package com.si.parkings.entities;

import java.util.ArrayList;
public class ParkingLots {
    public String lat;
    public String lng;
    public String title;
    public String qr_code_enter;
    public String qr_code_exit;
    public Boolean needs_to_lift_enter;
    public Boolean needs_to_lift_exit;
    public ArrayList<Spot> spots;
    public String price;
}
