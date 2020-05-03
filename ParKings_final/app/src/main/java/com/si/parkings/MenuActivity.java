package com.si.parkings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private void setText() {
        TextView helloText = findViewById(R.id.helloText);
       currentUser = FirebaseAuth.getInstance().getCurrentUser();
       helloText.setText("Welcome, " + currentUser.getDisplayName());
    }
    private void setupUI(){
        setText();
        searchParking();
        enterParking();
        signOut();
    }

    private void enterParking() {
        Button enterParkingButton = findViewById(R.id.enter_parking_button);
        enterParkingButton.setEnabled(true);
        enterParkingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, EnterParkingActivity.class));
            }
        });
    }

    private void searchParking() {
        Button searchParkingButton = findViewById(R.id.search_parking_button);
        searchParkingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, SearchParkingActivity.class));
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
