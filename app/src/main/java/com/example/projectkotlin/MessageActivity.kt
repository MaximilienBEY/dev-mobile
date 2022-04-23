package com.example.projectkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class MessageActivity : AppCompatActivity() {

    private lateinit var viewRecycler: RecyclerView
    private lateinit var viewBack: ImageView
    private lateinit var viewReceiver: TextView
    private lateinit var viewContent: EditText
    private lateinit var viewButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase
            .getInstance("https://kotlinproject-73b65-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/")

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = mAuth.currentUser?.uid

        val senderRoom = receiverUid + senderUid

        viewRecycler = findViewById(R.id.message_recycler)
        viewBack = findViewById(R.id.message_back)
        viewReceiver = findViewById(R.id.message_receiver)
        viewContent = findViewById(R.id.message_input)
        viewButton = findViewById(R.id.message_button)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        viewRecycler.layoutManager = LinearLayoutManager(this)
        viewRecycler.adapter = messageAdapter

        mDbRef.child("conversations").child(senderRoom).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
                viewRecycler.smoothScrollToPosition(messageAdapter.itemCount)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity, "Unknown error", Toast.LENGTH_SHORT).show()
            }
        })


        viewReceiver.text = name
        viewBack.setOnClickListener {
            finish()
        }
        viewButton.setOnClickListener {
            val content = viewContent.text.toString()
            storeMessage(content, receiverUid!!)
        }
    }

    private fun storeMessage(content: String, receiverUid: String) {
        val senderUid = mAuth.currentUser?.uid
        val senderRoom = receiverUid + senderUid
        val receiverRoom = senderUid + receiverUid

        val message = Message(content.trim(), senderUid, Date())

        mDbRef.child("conversations").child(senderRoom).push().setValue(message).addOnSuccessListener {
            mDbRef.child("conversations").child(receiverRoom).push().setValue(message)
        }
        viewContent.setText("")
    }
}