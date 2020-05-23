package com.si.parkings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.si.parkings.entities.User;
import com.si.parkings.entities.UserDate;
import com.si.parkings.menuActivities.AmountActivity;
import com.si.parkings.menuActivities.SeeParkingLotsActivity;
import com.si.parkings.menuActivities.parkingFlow.EnterParkingActivity;
import com.si.parkings.menuActivities.parkingFlow.ExitParkingActivity;

import java.lang.reflect.Array;
import java.time.LocalDateTime;

public class MenuActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private Activity currentActivity;

    DialogInterface.OnClickListener dialogClickListener = (dialog, variant) -> {
        switch (variant){
            case DialogInterface.BUTTON_POSITIVE:
                //Yes button clicked
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
        }
    };

    public void signOut(){
        Button signoutButton = findViewById(R.id.signout_button);
        signoutButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("If you sign out, you will lose all your data. Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void setText() {
        TextView helloText = findViewById(R.id.helloText);
        TextView amountText = findViewById(R.id.amountText);
        TextView parkText = findViewById(R.id.parkText);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        helloText.setText("Welcome, " + currentUser.getDisplayName());
        DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                amountText.setText("Your amount: " +  user.getCash());
                String parkingLot = user.getParkingLotID();
                String parkingSpot = user.getParkingSpotID();
                if (parkingLot != null) {
                    parkingLot = (String)Array.get(parkingLot.split("-", 3),2);
                    if (parkingSpot == null)
                        parkingSpot = "";
                    parkText.setText("You parked in: " + parkingLot + "," + parkingSpot);
                }
                else
                    parkText.setText("You haven't parked yet.");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addAmount(){
        Button addAmountButton = findViewById(R.id.enter_amount_button);
        addAmountButton.setOnClickListener(
                v -> startActivity(new Intent(MenuActivity.this, AmountActivity.class)));
    }

    private void setupUI(){
        setText();
        searchParking();
        addAmount();
        enterExitParking();
        signOut();
    }

    private void enterExitParking() {
        Button parkingButton = findViewById(R.id.enter_exit_parking_button);
        DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getParkingLotID() == null){
                    parkingButton.setText("Enter parking");
                    parkingButton.setOnClickListener(
                            v -> {
                                startActivity(new Intent(MenuActivity.this, EnterParkingActivity.class));
                                currentActivity.finish(); //should be deleted??
                            });

                }
                else{
                    parkingButton.setText("Exit parking");
                    parkingButton.setOnClickListener(v -> {
                        UserDate enterDate = user.getEnterTime();
                        UserDate currentDate = new UserDate();
                        currentDate.setDayOfYear(LocalDateTime.now().getDayOfYear());
                        currentDate.setYear(LocalDateTime.now().getYear());
                        currentDate.setHour(LocalDateTime.now().getHour());
                        currentDate.setMinute(LocalDateTime.now().getMinute());
                        currentDate.setSecond(LocalDateTime.now().getSecond());
                        Float amount = user.calculateOwedSum(enterDate, currentDate, user.getParkingLotPrice());
                        user.setAmountToPay(amount);
                        if(user.getAmountToPay() < user.getCash()) {
                            Intent intent = new Intent(MenuActivity.this, ExitParkingActivity.class);
                            intent.putExtra("userCash", user.getCash());
                            intent.putExtra("userAmountToPay", user.getAmountToPay());
                            intent.putExtra("userParkingSpotID", user.getParkingSpotID());
                            startActivity(intent);
                            currentActivity.finish();  //should be deleted??
                        }
                        else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Not enough money to pay!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void searchParking() {
        Button searchParkingButton = findViewById(R.id.search_parking_button);
        searchParkingButton.setOnClickListener(
                v -> startActivity(new Intent(MenuActivity.this, SeeParkingLotsActivity.class)));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        currentActivity = this;
        this.setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Boolean refresh = intent.getBooleanExtra("refresh",false);
        if (refresh) {
            intent.removeExtra("refresh");
            this.finish();
            startActivity(intent);
        }
    }
}
