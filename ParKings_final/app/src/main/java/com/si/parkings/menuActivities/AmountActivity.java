package com.si.parkings.menuActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.si.parkings.MenuActivity;
import com.si.parkings.R;
import com.si.parkings.entities.User;

import java.util.HashMap;
import java.util.Map;

public class AmountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int)(displayMetrics.widthPixels* .6), (int)(displayMetrics.heightPixels* .5));

        Button addButton = findViewById(R.id.addButton);
        EditText amountText = findViewById(R.id.enterAmount);
        addButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> userUpdate = new HashMap<>();
                    User user = dataSnapshot.getValue(User.class);
                    Integer amount = Integer.parseInt(amountText.getText().toString());
                    userUpdate.put("cash", user.getCash() + amount);
                    databaseReferenceCurrentUser.updateChildren(userUpdate);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            startActivity(new Intent(AmountActivity.this, MenuActivity.class));
        });
    }
}
