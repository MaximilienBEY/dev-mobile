package com.example.projectkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECIEVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ITEM_RECIEVE) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_received_layout, parent, false)
            ReceivedViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_sent_layout, parent, false)
            SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.javaClass == SentViewHolder::class.java) {
            val message = messageList[position]

            val viewHolder = holder as SentViewHolder
            viewHolder.content.text = message.content
        } else {
            val message = messageList[position]

            val viewHolder = holder as ReceivedViewHolder
            viewHolder.content.text = message.content
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(message.authorId)) return ITEM_SENT
        return ITEM_RECIEVE
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val content = itemView.findViewById<TextView>(R.id.message_sent_content)
    }
    class ReceivedViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val content = itemView.findViewById<TextView>(R.id.message_recieved_content)
    }

}