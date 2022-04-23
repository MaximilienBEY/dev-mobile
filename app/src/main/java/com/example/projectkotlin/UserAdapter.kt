package com.example.projectkotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter(private val context: Context, private val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.username.text = user.username
        holder.description.text = user.last_message
        if (user.last_date != null) holder.date.text = getDateString(user.last_date!!)
        else holder.date.text = ""

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MessageActivity::class.java)

            intent.putExtra("name", user.username)
            intent.putExtra("uid", user.uid)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView.findViewById<TextView>(R.id.user_name)
        val description = itemView.findViewById<TextView>(R.id.user_description)
        val date = itemView.findViewById<TextView>(R.id.user_date)
    }

    private fun getDateString(date: Date) : String {
        val diff: Long = Date().time - date.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24


        return when {
            days >= 1 -> SimpleDateFormat("dd/MM/yyyy").format(date)
            hours >= 1 -> return "$hours h"
            minutes >= 1 -> return "$minutes min"
            else -> "Now"
        }
    }

}