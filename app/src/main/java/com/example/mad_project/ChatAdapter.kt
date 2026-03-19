package com.example.mad_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ChatMessage(
    val message: String,
    val isFromUser: Boolean,
    val time: String
)

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutReceived: LinearLayout = view.findViewById(R.id.layoutReceived)
        val tvMessageReceived: TextView = view.findViewById(R.id.tvMessageReceived)
        val tvTimeReceived: TextView = view.findViewById(R.id.tvTimeReceived)

        val layoutSent: LinearLayout = view.findViewById(R.id.layoutSent)
        val tvMessageSent: TextView = view.findViewById(R.id.tvMessageSent)
        val tvTimeSent: TextView = view.findViewById(R.id.tvTimeSent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val msg = messages[position]
        if (msg.isFromUser) {
            holder.layoutReceived.visibility = View.GONE
            holder.layoutSent.visibility = View.VISIBLE
            holder.tvMessageSent.text = msg.message
            holder.tvTimeSent.text = msg.time
        } else {
            holder.layoutSent.visibility = View.GONE
            holder.layoutReceived.visibility = View.VISIBLE
            holder.tvMessageReceived.text = msg.message
            holder.tvTimeReceived.text = msg.time
        }
    }

    override fun getItemCount() = messages.size
}
