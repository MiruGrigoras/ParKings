package com.si.parkings.menuActivities.parkingFlow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.si.parkings.R;

import java.io.IOException;

public abstract class QRScan extends AppCompatActivity{
    private TextView qrResult;
    private BarcodeDetector qrDetector;
    private final int RequestCameraPermissionID = 1001;
    private SurfaceView qrScanView;
    private CameraSource cameraSource;
    private AppCompatActivity currentActivity;

    public AppCompatActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(AppCompatActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public TextView getQrResult() {
        return qrResult;
    }

    public void setQrResult(TextView qrResult) {
        this.qrResult = qrResult;
    }

    public SurfaceView getQrScanView() {
        return qrScanView;
    }

    public void setQrScanView(SurfaceView qrScanView) {
        this.qrScanView = qrScanView;
    }


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

        qrScanView = findViewById(R.id.QRScanView);
        qrResult = findViewById(R.id.qrResult);
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
                    ActivityCompat.requestPermissions(currentActivity,
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

            public int count = 0;
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
                                    if(readValue != null && count == 0){
                                        process(readValue);
                                        count++;
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

    protected abstract void process(String readValue);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrDetector.release();
        cameraSource.stop();
        cameraSource.release();
    }
}
