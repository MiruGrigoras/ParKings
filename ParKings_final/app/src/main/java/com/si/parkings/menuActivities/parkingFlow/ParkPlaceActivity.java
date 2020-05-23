package com.si.parkings.menuActivities.parkingFlow;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.si.parkings.MenuActivity;
import com.si.parkings.R;
import com.si.parkings.entities.ParkingLots;

import java.util.HashMap;
import java.util.Map;

public class ParkPlaceActivity extends QRScan {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_park_place);
    }

    @Override
    protected void process(String readValue) {
        Intent intent = getIntent();
        String assignedSpot = intent.getStringExtra("spot_name");

        if(readValue.contains(assignedSpot)){
            setSpotToOccupied(assignedSpot);
        }
    }

    private void setSpotToOccupied(String assignedSpot) {
        final DatabaseReference databaseReferenceParkingLots = FirebaseDatabase.getInstance().getReference("parking_lots");
        databaseReferenceParkingLots.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> parkingUpdate = new HashMap<>();
                for (DataSnapshot parkingLotSnapshot: dataSnapshot.getChildren()) {
                    ParkingLots parkingLot = parkingLotSnapshot.getValue(ParkingLots.class);
                    for(int i = 0; i < parkingLot.spots.size(); i++){
                        if(assignedSpot.equals(parkingLot.spots.get(i).spot_id)){
                            parkingUpdate.put(parkingLotSnapshot.getKey()+ "/spots/" + i+ "/occupied", true);
                            databaseReferenceParkingLots.updateChildren(parkingUpdate);
                            return;
                        }
                    }
//                    if (parkingLot.qr_code_exit.equals(readValue)) {
//                        parkingUpdate.put(parkingLotSnapshot.getKey()+ "/needs_to_lift_exit", true);
//                        databaseReferenceParkingLots.updateChildren(parkingUpdate);
//                        return;
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        startActivity(new Intent(ParkPlaceActivity.this, MenuActivity.class));
        currentActivity.finish();
    }
}
