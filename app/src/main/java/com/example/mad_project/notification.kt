package com.example.mad_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_project.adapter.NotificationAdapter

class notification : Fragment() {

    private lateinit var rvNotifications: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var tvMarkAllRead: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNotifications = view.findViewById(R.id.rvNotifications)
        layoutEmpty = view.findViewById(R.id.layoutEmptyNotifications)
        tvMarkAllRead = view.findViewById(R.id.tvMarkAllRead)

        rvNotifications.layoutManager = LinearLayoutManager(requireContext())

        tvMarkAllRead.setOnClickListener {
            AppRepository.markAllNotificationsRead()
            loadNotifications()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotifications()
    }

    private fun loadNotifications() {
        val notifs = AppRepository.notifications

        if (notifs.isEmpty()) {
            rvNotifications.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            layoutEmpty.visibility = View.GONE
            rvNotifications.visibility = View.VISIBLE
            rvNotifications.adapter = NotificationAdapter(notifs)
        }
    }
}