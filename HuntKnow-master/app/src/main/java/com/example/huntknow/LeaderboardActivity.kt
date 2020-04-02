package com.example.huntknow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.example.huntknow.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var listView : ListView
    var userFinalList : MutableList<String> = mutableListOf()
    var context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        listView = findViewById(R.id.users_list_view)
        val mRef = FirebaseDatabase.getInstance().getReference("users")

        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                var userList: MutableList<User> = mutableListOf()
                p0.children.mapNotNullTo(userList) {
                    it.getValue<User>(User::class.java)
                }
                userList.sortByDescending{ user -> user.total_answers }
                var iterator = userList.listIterator()
                for (user in iterator)
                    userFinalList.add(String.format("%s has %d correct answers",user.team_name,user.total_answers))
                val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, userFinalList)
                listView.adapter = adapter
            }
        })

        val openHome: Button = findViewById(R.id.goHome)
        openHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }
}
