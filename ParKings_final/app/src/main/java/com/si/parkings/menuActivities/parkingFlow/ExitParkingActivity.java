package com.si.parkings.menuActivities.parkingFlow;

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
import com.si.parkings.MenuActivity;
import com.si.parkings.R;
import com.si.parkings.entities.ParkingLots;
import com.si.parkings.entities.User;

import java.util.HashMap;
import java.util.Map;

public class ExitParkingActivity extends QRScan {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_exit_parking);
    }

    @Override
    protected void process(final String readValue) {
        if(readValue.startsWith(getString(R.string.parkingExitMessage))){
            extractSumToPayAndResetParkingStatus(readValue);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), R.string.incorrectQRCode, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void extractSumToPayAndResetParkingStatus(String readValue) {
        databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = getIntent();
                Float userCash = intent.getFloatExtra("userCash", 0);
                Float userAmountToPay = intent.getFloatExtra("userAmountToPay", 0);

                User user = new User();
                user.setAmountToPay(userAmountToPay);
                user.setCash(userCash - userAmountToPay);

                Map<String, Object> userUpdate = new HashMap<>();
                userUpdate.put("/cash", user.getCash());
                userUpdate.put("/amountToPay", userAmountToPay);
                userUpdate.put("/enterTime", null);
                userUpdate.put("/parkingLotID", null);
                userUpdate.put("/parkingLotPrice", null);
                userUpdate.put("/parkingSpotID", null);

                databaseReferenceCurrentUser.updateChildren(userUpdate);

                sendLiftBarrierCommand(readValue);
                return;

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
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        startActivity(new Intent(ExitParkingActivity.this, MenuActivity.class));
        currentActivity.finish();
    }
}
