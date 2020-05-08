package com.si.parkings;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EnterParkingActivity extends AppCompatActivity {
    private SurfaceView qrScanView;
    private TextView qrResult;
    private BarcodeDetector qrDetector;
    private CameraSource cameraSource;
    private final int RequestCameraPermissionID = 1001;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(qrScanView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_parking);

        qrScanView = (SurfaceView) findViewById(R.id.QRScanView);
        qrResult = (TextView) findViewById(R.id.qrResult);
        qrDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if(!qrDetector.isOperational()){
            this.finish();
        }

        cameraSource = new CameraSource
                .Builder(this, qrDetector)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1920, 1080)
                .build();


        qrScanView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EnterParkingActivity.this,
                            new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(qrScanView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        qrDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                if(detections.getDetectedItems().size()>0){
                    final Barcode qrCodes = detections.getDetectedItems().valueAt(0);
                    if(qrCodes.displayValue != null) {
                        qrResult.post(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    final String readValue = qrCodes.displayValue;
                                    qrResult.setText(readValue);
                                    if(readValue != null){
                                        if(readValue.startsWith(getString(R.string.parkingEnterMessage))){
                                            databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    Map<String, Object> userUpdate = new HashMap<>();
                                                    userUpdate.put("enterTime", new Date(System.currentTimeMillis()));
                                                    userUpdate.put("parkingLotID", getParkingLotIDFromQRResult(readValue));
                                                    databaseReferenceCurrentUser.updateChildren(userUpdate);
                                                    sendLiftBarrierCommand();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                        else{
                                            Toast toast = Toast.makeText(getApplicationContext(), R.string.incorrectQRCode, Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                }catch (IndexOutOfBoundsException e){
                                }

                            }
                        });
                    }
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrDetector.release();
        cameraSource.stop();
        cameraSource.release();
    }

    private void sendLiftBarrierCommand() {
        Intent intent = new Intent(EnterParkingActivity.this, InTheParkingActivity.class);
        intent.putExtra("qrResult", qrResult.getText().toString());
        startActivity(intent);
        this.finish();
    }


    private String getParkingLotIDFromQRResult(final String readValue) {
        DatabaseReference databaseReferenceParkingLots = FirebaseDatabase.getInstance().getReference().child("parking_lots");
        ParkingIDValueEventListener vel = new ParkingIDValueEventListener(readValue);
        databaseReferenceParkingLots.addValueEventListener(vel);
        return vel.getParkingLotID();
    }
}
