package com.karigar.app.ui.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.activity.viewModels

@AndroidEntryPoint
class ChatActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val workerName = intent.getStringExtra("worker_name") ?: "Karigar Worker"
        val workerPhone = intent.getStringExtra("worker_phone") ?: ""
        val orderId = intent.getStringExtra("order_id") ?: ""

        setContent {
            MaterialTheme {
                ChatScreen(
                    workerName = workerName,
                    orderId = orderId,
                    onBack = { finish() },
                    onCall = {
                        if (workerPhone.isNotEmpty()) {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$workerPhone"))
                            startActivity(dialIntent)
                        } else {
                            Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
                        }
                    },
                    viewModel = chatViewModel
                )
            }
        }
    }
}
