package com.example.huntknow

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.huntknow.GlobalVariables.Companion.qrList
import com.example.huntknow.models.User
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ScanActivity : AppCompatActivity() {

    private lateinit var surfaceScan: SurfaceView
    private lateinit var scanResText: TextView
    private lateinit var qrDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    var context = this
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        val context=this
        surfaceScan = findViewById(R.id.surfaceQRScanner)
        scanResText = findViewById((R.id.qrResult))
        qrDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()

        if(!qrDetector.isOperational){
            this.finish()
        }
        fun goToQuizActivityWithResult(){
            intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("qrResult",scanResText.text.toString())
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            this.finish()
        }
        qrDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val barcodes = detections?.detectedItems?.valueAt(0)

                if (barcodes?.displayValue != null) {
                    scanResText.post {
                        scanResText.text = barcodes.displayValue

                        if(scanResText.text!=null) {
                            var userList : MutableList<User> = mutableListOf()

                            var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                            ref.addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {}

                                override fun onDataChange(p0: DataSnapshot) {
                                    p0.children.mapNotNullTo(userList) {
                                        it.getValue<User>(User::class.java)
                                    }
                                    val currentUser =FirebaseAuth.getInstance().currentUser!!.uid
                                    val currQr = userList.first{user -> user.uid== currentUser}.qr_current
                                    if(currQr==scanResText.text) {
                                        goToQuizActivityWithResult()
                                        return
                                    }
                                    else {
                                        Toast.makeText(context, "Incorrect QR", Toast.LENGTH_LONG).show()
                                        val intent = Intent(context, HomeActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        startActivity(intent)
                                    }
                                }
                            })
                        }
                    }
                }
                return
            }

        })

        cameraSource = CameraSource.Builder(this, qrDetector)
            .setRequestedPreviewSize(2340, 1080)
            .setRequestedFps(25f).setAutoFocusEnabled(true).build()
//
        surfaceScan.holder.addCallback(object:SurfaceHolder.Callback2{
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
            }

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
            }


            override fun surfaceCreated(holder: SurfaceHolder?) {
                if(ContextCompat.checkSelfPermission(this@ScanActivity,
                        Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                cameraSource.start(holder)
                else
                    ActivityCompat.requestPermissions(this@ScanActivity, arrayOf(Manifest.permission.CAMERA),111)
            }
            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }
        })
        }
    //
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111)
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                cameraSource.start(surfaceScan.holder)
    }

    override fun onDestroy() {
        super.onDestroy()
        qrDetector.release()
        cameraSource.stop()
        cameraSource.release()
    }
    }
