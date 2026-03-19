package com.example.mad_project.model

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false,
    val type: String = "placed"
    // Types: "placed","accepted","confirm","assigned","completed","cancelled","announcement"
)
