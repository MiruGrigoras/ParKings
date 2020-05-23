package com.si.parkings.menuActivities.parkingFlow;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.si.parkings.R;

public class SeeAssignedParkPlaceActivity extends AppCompatActivity {
    private String spot_name;

    @SuppressLint("SetTextI18n")
    private void getPicture()
    {
        Intent intent = getIntent();
        String image_url = intent.getStringExtra("image_url");
        spot_name = intent.getStringExtra("spot_name");
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
        setContentView(R.layout.activity_see_assigned_place);
        getPicture();
        Button scanPlace = findViewById(R.id.scan_place_button);
        scanPlace.setOnClickListener(
                v -> {
                    Intent intent = new Intent(SeeAssignedParkPlaceActivity.this, ParkPlaceActivity.class);
                    intent.putExtra("spot_name", spot_name);
                    startActivity(intent);
                });
    }
}
