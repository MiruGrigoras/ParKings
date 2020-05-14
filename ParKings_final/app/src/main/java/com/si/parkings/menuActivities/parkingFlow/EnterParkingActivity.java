package com.si.parkings;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.si.parkings.entities.ParkingLots;
import com.si.parkings.R;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EnterParkingActivity extends QRScan {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

    public EnterParkingActivity(){
        setCurrentActivity(this);
    }

    @Override
    protected void process(final String readValue) {
        if(readValue.startsWith(getString(R.string.parkingEnterMessage))){
            databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> userUpdate = new HashMap<>();
                    userUpdate.put("enterTime/year", LocalDateTime.now().getYear());
                    userUpdate.put("enterTime/day_of_year", LocalDateTime.now().getDayOfYear());
                    userUpdate.put("enterTime/hour", LocalDateTime.now().getHour());
                    userUpdate.put("enterTime/minute", LocalDateTime.now().getMinute());
                    userUpdate.put("enterTime/second", LocalDateTime.now().getSecond());
                    userUpdate.put("parkingLotID", readValue);
                    databaseReferenceCurrentUser.updateChildren(userUpdate);
                    sendLiftBarrierCommand(readValue);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), R.string.incorrectQRCode, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void sendLiftBarrierCommand(final String readValue) {
        final DatabaseReference databaseReferenceParkingLots = FirebaseDatabase.getInstance().getReference("parking_lots");
        databaseReferenceParkingLots.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> parkingUpdate = new HashMap<>();
                for (DataSnapshot parkingLotSnapshot: dataSnapshot.getChildren()) {
                    ParkingLots parkingLot = parkingLotSnapshot.getValue(ParkingLots.class);
                    if (parkingLot.qr_code.equals(readValue)) {
                        parkingUpdate.put(parkingLotSnapshot.getKey()+ "/needs_to_lift", true);
                        databaseReferenceParkingLots.updateChildren(parkingUpdate);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Intent intent = new Intent(EnterParkingActivity.this, ParkPlaceActivity.class);
        intent.putExtra("qrResult", getQrResult().getText().toString());
        startActivity(intent);
        this.finish();
    }
}
