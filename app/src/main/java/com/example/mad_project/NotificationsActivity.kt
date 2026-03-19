package com.example.mad_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import com.example.mad_project.adapter.NotificationAdapter

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
    }

    override fun onResume() {
        super.onResume()
        loadNotifications()
    }

    private fun loadNotifications() {
        val rvNotifications  = findViewById<RecyclerView>(R.id.rvNotifications)
        val layoutEmpty      = findViewById<LinearLayout>(R.id.layoutEmptyNotifications)
        
        layoutEmpty?.findViewById<android.widget.ImageView>(R.id.ivEmptyIcon)?.setImageResource(R.drawable.bell)
        layoutEmpty?.findViewById<android.widget.TextView>(R.id.tvEmptyTitle)?.text = "No notifications yet"
        layoutEmpty?.findViewById<android.widget.TextView>(R.id.tvEmptyDesc)?.text = "We'll notify you when something updates"
        
        val tvMarkAll        = findViewById<TextView>(R.id.tvMarkAllRead)
        val btnBack          = findViewById<android.widget.ImageView>(R.id.btnNotifBack)

        btnBack?.setOnClickListener { finish() }

        rvNotifications?.layoutManager = LinearLayoutManager(this)

        tvMarkAll?.setOnClickListener {
            AppRepository.markAllNotificationsRead()
            loadNotifications()
        }

        val notifs = AppRepository.notifications
        if (notifs.isEmpty()) {
            rvNotifications?.visibility = View.GONE
            layoutEmpty?.visibility = View.VISIBLE
        } else {
            layoutEmpty?.visibility = View.GONE
            rvNotifications?.visibility = View.VISIBLE
            rvNotifications?.adapter = NotificationAdapter(notifs)
        }
    }
}
