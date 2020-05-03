package com.si.parkings;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ParkingIDValueEventListener implements ValueEventListener {
    private String readValue;

    public ParkingIDValueEventListener(String readValue){
        this.readValue = readValue;
    }
    private String correctParkingLotID;

    public String getParkingLotID() {
        return correctParkingLotID;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot parkingLotSnapshot: dataSnapshot.getChildren()){
            String parkingLotID = parkingLotSnapshot.getKey();
            ParkingLots parkingLot = parkingLotSnapshot.getValue(ParkingLots.class);
            if(parkingLot.qr_code_enter.equals(readValue))
                correctParkingLotID = parkingLotID;
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
