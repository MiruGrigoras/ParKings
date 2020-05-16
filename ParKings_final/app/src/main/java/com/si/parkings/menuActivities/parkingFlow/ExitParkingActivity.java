package com.si.parkings.menuActivities.parkingFlow;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.si.parkings.MenuActivity;
import com.si.parkings.R;
import com.si.parkings.entities.ParkingLots;
import com.si.parkings.entities.User;
import com.si.parkings.entities.UserDate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExitParkingActivity extends QRScan {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
    private int currentUserParkingLotPrice;

    public ExitParkingActivity(){
        setCurrentActivity(this);
    }


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_exit_parking);
    }

    @Override
    protected void process(final String readValue) {
        System.out.println("*********** Am ajuns in process ***************");
        if(readValue.startsWith(getString(R.string.parkingEnterMessage))){
            sendLiftBarrierCommand(readValue);
            extractSumToPayAndResetParkingStatus();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), R.string.incorrectQRCode, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void extractSumToPayAndResetParkingStatus() {
        databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> userUpdate = new HashMap<>();
                User user = dataSnapshot.getValue(User.class);
                UserDate enterDate = user.getEnterTime();
                UserDate exitDate = user.getExitTime();
                Float amountToPay = user.calculateOwedSum(enterDate, exitDate, currentUserParkingLotPrice);
                user.setAmountToPay(amountToPay);
                user.setCash(user.getCash() - amountToPay);
                userUpdate.put(dataSnapshot.getKey()+"/cash", user.getCash());
                userUpdate.put(dataSnapshot.getKey()+"/amountToPay", user.getAmountToPay());
                userUpdate.put(dataSnapshot.getKey()+"/enterTime", null);
                userUpdate.put(dataSnapshot.getKey()+"/exitTime", null);
                userUpdate.put(dataSnapshot.getKey()+"/parkingLotID", null);
                userUpdate.put(dataSnapshot.getKey()+"/parkingLotPrice", null);
                userUpdate.put(dataSnapshot.getKey()+"/parkingSpotID", null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendLiftBarrierCommand(final String readValue) {
        final DatabaseReference databaseReferenceParkingLots = FirebaseDatabase.getInstance().getReference("parking_lots");
        databaseReferenceParkingLots.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> parkingUpdate = new HashMap<>();
                for (DataSnapshot parkingLotSnapshot: dataSnapshot.getChildren()) {
                    ParkingLots parkingLot = parkingLotSnapshot.getValue(ParkingLots.class);
                    if (parkingLot.qr_code_exit.equals(readValue)) {
                        parkingUpdate.put(parkingLotSnapshot.getKey()+ "/needs_to_lift_exit", true);
                        databaseReferenceParkingLots.updateChildren(parkingUpdate);
                        currentUserParkingLotPrice = Integer.parseInt(parkingLot.price);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intent = new Intent(ExitParkingActivity.this, MenuActivity.class);
        intent.putExtra("qrResult", getQrResult().getText().toString());
        startActivity(intent);
        this.finish();
    }
}
