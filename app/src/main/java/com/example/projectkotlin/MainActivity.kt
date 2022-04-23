package com.example.projectkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var userRecycler: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var messageList: ArrayList<LastMessage>
    private lateinit var adapter: UserAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase
            .getInstance("https://kotlinproject-73b65-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/")

        userList = ArrayList()
        messageList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecycler = findViewById(R.id.user_recycler)

        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.adapter = adapter

        val Uid = mAuth.currentUser?.uid

        mDbRef.child("conversations").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (cSnapshot in snapshot.children) {
                    val mList = ArrayList<Message>()
                    for (postSnapshot in cSnapshot.children) {
                        mList.add(postSnapshot.getValue(Message::class.java)!!)
//                        messageList.add(LastMessage(postSnapshot.key, postSnapshot.getValue(Message::class.java)))
                    }
                    mList.sortByDescending { it.createdAt }
                    messageList.add(LastMessage(cSnapshot.key, mList[0]))
                    mList.clear()
                }
                mDbRef.child("users").addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userList.clear()
                        for (postSnapshot in snapshot.children) {
                            val user = postSnapshot.getValue(User::class.java)
                            if (user == null || user.uid == Uid) continue

                            val message = messageList.find {m -> m.id == (Uid + user.uid)}
                            val content = message?.message?.content
                            val date = message?.message?.createdAt

                            if (content != null) user.last_message = content
                            else user.last_message = "Start a conversation"
                            user.last_date = date

                            userList.add(user)
                        }
                        messageList.clear()
                        userList.sortByDescending { it.last_date }
                        adapter.notifyDataSetChanged()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MainActivity, "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Unknown error", Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sign_out) {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}