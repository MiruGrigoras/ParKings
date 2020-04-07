package com.si.parkings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
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
    private void setupUI(){
        signOut();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.setupUI();
    }
}
