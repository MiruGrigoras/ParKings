package com.si.parkings.menuActivities.parkingFlow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.si.parkings.R;

public class SeeAssignedParkPlaceActivity extends AppCompatActivity {

    private void getPicture()
    {
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("/Iulius_Mall_Spots/spot_1.png");
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
    }
}
