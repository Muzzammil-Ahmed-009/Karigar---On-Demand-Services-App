package com.example.mad_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad_project.databinding.ActivityChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val messages = ArrayList<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val workerName = intent.getStringExtra("worker_name") ?: "Karigar Worker"
        val workerPhone = intent.getStringExtra("worker_phone") ?: ""

        binding.tvWorkerName.text = workerName

        binding.btnBack.setOnClickListener { finish() }

        binding.btnCall.setOnClickListener {
            if (workerPhone.isNotEmpty()) {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$workerPhone"))
                startActivity(dialIntent)
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
            }
        }

        adapter = ChatAdapter(messages)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.rvChat.layoutManager = layoutManager
        binding.rvChat.adapter = adapter

        // Load dummy initial messages
        loadInitialMessages(workerName)

        // Handle sending
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
            }
        }
    }

    private fun loadInitialMessages(workerName: String) {
        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(System.currentTimeMillis() - 120000)) // 2 mins ago
        val timeNow2 = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(System.currentTimeMillis() - 60000)) // 1 min ago
        messages.add(ChatMessage("Hello! I am assigned to your task.", false, timeNow))
        messages.add(ChatMessage("I will be there in 30 minutes. Is the location correct?", false, timeNow2))
        adapter.notifyDataSetChanged()
        binding.rvChat.scrollToPosition(messages.size - 1)
    }

    private fun sendMessage(text: String) {
        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        messages.add(ChatMessage(text, true, timeNow))
        adapter.notifyItemInserted(messages.size - 1)
        binding.rvChat.scrollToPosition(messages.size - 1)
        binding.etMessage.text.clear()

        // Simulate reply after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val replyTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            messages.add(ChatMessage("Okay, noted. Thanks!", false, replyTime))
            adapter.notifyItemInserted(messages.size - 1)
            binding.rvChat.scrollToPosition(messages.size - 1)
        }, 2000)
    }
}
