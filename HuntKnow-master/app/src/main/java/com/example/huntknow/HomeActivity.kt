package com.example.huntknow

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible

import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import com.example.huntknow.GlobalVariables.Companion.right_answers
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {
    private fun signOut() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        FirebaseAuth.getInstance().signOut()
    }
    private fun setupUI() {
        open_sign_out.setOnClickListener {
            signOut()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val goToQRScan: Button = findViewById(R.id.open_scan)
        val newLocation: TextView = findViewById(R.id.newLocation)
        newLocation.isVisible = false
        var timeBlocked: String?
        val nextLocation: String?
        val isFinished: String?
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                timeBlocked = null
                nextLocation = null
                isFinished = null
            } else {
                timeBlocked = extras.getString("timeBlocked")
                nextLocation = extras.getString("location")
                isFinished = extras.getString("done")
            }
        } else {
            timeBlocked = savedInstanceState.getSerializable("timeBlocked") as String
            nextLocation = savedInstanceState.getSerializable("location") as String
            isFinished = savedInstanceState.getSerializable("done") as String
        }

        if(timeBlocked != null) {
            if(isFinished == "true") {
                timeBlocked = "0"
                goToQRScan.isVisible = false
                newLocation.isVisible = false
            }
            var time : Int = timeBlocked.toInt()
            val timeForScanActivity = object: CountDownTimer(time.toLong(), 1000){
                override fun onFinish() {
                    goToQRScan.text = "Scan QR"
                    goToQRScan.isEnabled = true
                    newLocation.isVisible = true
                    if(isFinished == "false")
                        newLocation.text = String.format("Next Location: %s",nextLocation)
                    else
                        newLocation.text = String.format("You finished the hunt!")
                }

                override fun onTick(millisUntilFinished: Long) {
                    goToQRScan.text = (String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                    time -=1000
                }
            }
            goToQRScan.isEnabled = false
            timeForScanActivity.start()
        }


        goToQRScan.setOnClickListener {
            right_answers = 0
            val intent = Intent(this, ScanActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        val openLeaderboard: Button = findViewById(R.id.open_leaderboard)
        openLeaderboard.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)

        }
        val openLocation: Button = findViewById(R.id.open_location)
        openLocation.setOnClickListener {
//            val intent = Intent(this, LocationActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
            Toast.makeText(this, "Coming Soon! :)", Toast.LENGTH_LONG).show()
        }

        setupUI()

    }


    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}
