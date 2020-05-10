package com.si.parkings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MenuActivity extends AppCompatActivity {
    private FirebaseUser currentUser;

    public void signOut(){
        Button signoutButton = findViewById(R.id.signout_button);
        signoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void setText() {
        TextView helloText = findViewById(R.id.helloText);
        TextView amountText = findViewById(R.id.amountText);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // get current user
        helloText.setText("Welcome " + currentUser.getDisplayName());
        // set current amount
        // amountText.setText("Your amount: " + );

    }

    private void addAmount(){
        Button addAmountButton = findViewById(R.id.enter_amount_button);
        addAmountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, AmountActivity.class));
            }
        });
    }
    private void setupUI(){
        setText();
        searchParking();
        addAmount();
        enterParking();
        signOut();
    }

    private void enterParking() {
        Button enterParkingButton = findViewById(R.id.enter_parking_button);
        enterParkingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MenuActivity.this, EnterParkingActivity.class));
            }
        });
    }

    private void searchParking() {
        Button searchParkingButton = findViewById(R.id.search_parking_button);
        searchParkingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, ParkingPlacesActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.setupUI();
    }
}
