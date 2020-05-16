package com.si.parkings.menuActivities.parkingFlow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.si.parkings.entities.ParkingLots;
import com.si.parkings.R;
import com.si.parkings.entities.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EnterParkingActivity extends QRScan {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
    Activity currentActivity;
    private int currentUserParkingLotPrice;

    public EnterParkingActivity(){
        setCurrentActivity(this);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_enter_parking);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
    }

    @Override
    protected void process(final String readValue) {
        if(readValue.startsWith(getString(R.string.parkingEnterMessage))){
            databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    sendLiftBarrierCommand(readValue, user);

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

    private void sendLiftBarrierCommand(final String readValue, User user) {
        final DatabaseReference databaseReferenceParkingLots = FirebaseDatabase.getInstance().getReference("parking_lots");
        databaseReferenceParkingLots.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> parkingUpdate = new HashMap<>();
                for (DataSnapshot parkingLotSnapshot: dataSnapshot.getChildren()) {
                    ParkingLots parkingLot = parkingLotSnapshot.getValue(ParkingLots.class);
                    if (parkingLot.qr_code_enter.equals(readValue)) {
                        parkingUpdate.put(parkingLotSnapshot.getKey()+ "/needs_to_lift_enter", true);
                        databaseReferenceParkingLots.updateChildren(parkingUpdate);
                        user.setParkingLotPrice(Integer.parseInt(parkingLot.price));

                        Map<String, Object> userUpdate = new HashMap<>();
                        userUpdate.put("enterTime/year", LocalDateTime.now().getYear());
                        userUpdate.put("enterTime/dayOfYear", LocalDateTime.now().getDayOfYear());
                        userUpdate.put("enterTime/hour", LocalDateTime.now().getHour());
                        userUpdate.put("enterTime/minute", LocalDateTime.now().getMinute());
                        userUpdate.put("enterTime/second", LocalDateTime.now().getSecond());
                        userUpdate.put("parkingLotID", readValue);
                        userUpdate.put("parkingLotPrice", user.getParkingLotPrice());
                        databaseReferenceCurrentUser.updateChildren(userUpdate);

                        startActivity(new Intent(EnterParkingActivity.this, SeeAssignedParkPlaceActivity.class));
                        currentActivity.finish();

                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
