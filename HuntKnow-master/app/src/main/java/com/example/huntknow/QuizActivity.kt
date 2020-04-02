package com.example.huntknow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.os.CountDownTimer
import android.widget.Button
import android.widget.Toast

import com.example.huntknow.models.Question
import com.google.firebase.database.*
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import com.example.huntknow.ui.main.SectionsPagerAdapter
import java.util.concurrent.TimeUnit
import com.example.huntknow.GlobalVariables.Companion.right_answers
import com.example.huntknow.GlobalVariables.Companion.qrList
import com.example.huntknow.GlobalVariables.Companion.total_answers
import com.example.huntknow.models.QrCode
import com.example.huntknow.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_scan.*


class QuizActivity : AppCompatActivity() {

    var quiz: MutableList<Question> = mutableListOf()

    lateinit var ref : DatabaseReference
    lateinit var quiz_timer : TextView
    lateinit var questionNo : TextView
    lateinit var finishQuiz : Button
    val context = this
    var time_spent = 60*1000
    val timerForQuizActivity = object: CountDownTimer(time_spent.toLong(), 1000){
        override fun onFinish() {
            val time = (10 - right_answers)*30*1000
            intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("timeBlocked",time.toString())
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        override fun onTick(millisUntilFinished: Long) {
            quiz_timer.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
            time_spent -=1000
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val qrCode: String?
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                qrCode = null
            } else {
                qrCode = extras.getString("qrResult")
            }
        } else {
            qrCode = savedInstanceState.getSerializable("qrResult") as String
        }

        ref = FirebaseDatabase.getInstance().getReference("questions")

        val context = this
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.mapNotNullTo(quiz) {
                    it.getValue<Question>(Question::class.java)
                }

                val iterator = quiz.listIterator()
                val aux: MutableList<Question> = mutableListOf()
                for (question in iterator)
                    if (question.qr_group == qrCode)
                        aux.add(question)
                quiz = aux
                quiz.shuffle()
                quiz_timer = findViewById(R.id.quizTimer)
                questionNo = findViewById(R.id.questionNo)

                timerForQuizActivity.start()
                questionNo.text = String.format("%d/%d", 0, 10)
                val sectionsPagerAdapter =
                    SectionsPagerAdapter(context, supportFragmentManager, quiz)
                val viewPager: ViewPager = findViewById(R.id.view_pager)
                viewPager.adapter = sectionsPagerAdapter
                viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                    override fun onPageScrolled( position: Int, positionOffset: Float, positionOffsetPixels: Int ) {}
                    override fun onPageSelected( position: Int ) { questionNo.text = String.format("%d/%d", right_answers, 10) }
                    override fun onPageScrollStateChanged(state: Int) {}

                })
                val tabs: TabLayout = findViewById(R.id.slidingTabs)
                tabs.setupWithViewPager(viewPager)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        finishQuiz = findViewById(R.id.finish)
        finishQuiz.setOnClickListener{

            timerForQuizActivity.cancel()
            val user = FirebaseAuth.getInstance().currentUser!!

            val userId = user.uid
            val mRef = FirebaseDatabase.getInstance().getReference("users")
            val childRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
            total_answers += right_answers
            childRef.child("total_answers").setValue(total_answers)

            if(qrCode == "complex") {
                val intentaux = Intent(context, WinActivity::class.java)
                intentaux.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intentaux)
                finish()
                return@setOnClickListener
            }
            intent = Intent(context, HomeActivity::class.java)
            if(qrList.isEmpty())
            {
                val qRef = FirebaseDatabase.getInstance().getReference("qr_codes")
                qRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.mapNotNullTo(qrList) {
                            it.getValue<QrCode>(QrCode::class.java)
                        }

                        val iterator = qrList.listIterator()
                        var aux = mutableListOf<QrCode>()
                        for (qr in iterator)
                            if (qr.qr_code == "complex") {
                                intent.putExtra("location", qr.place)
                                childRef.child("qr_current").setValue(qr.qr_code)
                                aux.add(qr)
                            }
                        qrList = aux
                        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) { }

                            override fun onDataChange(p0: DataSnapshot) {
                                var userList : MutableList<User> = mutableListOf()
                                p0.children.mapNotNullTo(userList) {
                                    it.getValue<User>(User::class.java)
                                }
                                var iterator2 = userList.listIterator()
                                var aux = 0
                                loop@ for (user in iterator2)
                                    if (user.uid == userId) {
                                        aux = user.visited_places

                                        childRef.child("visited_places").setValue(aux + 1)
                                        childRef.child("uid").setValue(userId)
                                        break@loop
                                    }
                            }
                        })
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                })
            }

            mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) { }

                override fun onDataChange(p0: DataSnapshot) {
                    var userList : MutableList<User> = mutableListOf()
                    p0.children.mapNotNullTo(userList) {
                        it.getValue<User>(User::class.java)
                    }
                    var iterator2 = userList.listIterator()
                    var aux = 0
                    loop@ for (user in iterator2)
                        if (user.uid == userId) {
                            aux = user.visited_places
                            var nextPlace = qrList.first()
                            childRef.child("qr_current").setValue(nextPlace.qr_code)
                            childRef.child("visited_places").setValue(aux + 1)
                            childRef.child("uid").setValue(userId)
                            val time = ((10 - right_answers)*30*1000).toString()
                            intent.putExtra("timeBlocked",time)
                            intent.putExtra("location", nextPlace.place)
                            intent.putExtra("done", "false")
                            qrList.remove(nextPlace)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                            finish()
                            break@loop
                        }
                }
            })
        }

    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "Finish the quiz first", Toast.LENGTH_SHORT).show()
    }
}

