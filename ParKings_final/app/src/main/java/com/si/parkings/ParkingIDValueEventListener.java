package com.si.parkings;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ParkingIDValueEventListener implements ValueEventListener {
    private String readValue;
    private String correctParkingLotID;

    public ParkingIDValueEventListener(String readValue){
        this.readValue = readValue;
    }

    public String getParkingLotID() {
        System.out.println("CorrectParkingLot id: " + correctParkingLotID + "*************");
        return correctParkingLotID;
    }

    public void setCorrectParkingLotID(String correctParkingLotID) {
        this.correctParkingLotID = correctParkingLotID;
    }


    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot parkingLotSnapshot: dataSnapshot.getChildren()) {
            String parkingLotID = parkingLotSnapshot.getKey();
            System.out.println("ParkingLot ID: " + parkingLotID + "\n");
            ParkingLots parkingLot = parkingLotSnapshot.getValue(ParkingLots.class);
            System.out.println("Current QR enter: " + parkingLot.qr_code_enter);
            System.out.println("Egalitate: " + parkingLot.qr_code_enter.equals(readValue));
            if (parkingLot.qr_code_enter.equals(readValue)) {
                setCorrectParkingLotID(parkingLotID);
                break;
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
