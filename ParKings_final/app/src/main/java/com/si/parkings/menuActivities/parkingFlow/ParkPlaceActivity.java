package com.si.parkings.menuActivities.parkingFlow;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.si.parkings.MenuActivity;
import com.si.parkings.R;
import com.si.parkings.menuActivities.AmountActivity;

public class ParkPlaceActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    private void getPicture()
    {
        Intent intent = getIntent();
        String image_url = intent.getStringExtra("image_url");
        String spot_name = intent.getStringExtra("spot_name");
        TextView parkSpotText = findViewById(R.id.parkSpotText);
        parkSpotText.setText("Your assigned place: " + spot_name);
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(image_url);
        ImageView parkSpot = findViewById(R.id.parkSpot);
        Glide.with(this)
                .load(imageReference)
                .into(parkSpot);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_place);
        getPicture();
        Button scanPlace = findViewById(R.id.scan_place_button);
        scanPlace.setOnClickListener(
                v -> startActivity(new Intent(ParkPlaceActivity.this, AmountActivity.class)));
    }
}
