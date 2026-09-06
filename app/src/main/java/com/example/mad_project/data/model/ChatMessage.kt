package com.karigar.app.data.model

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val isFromUser: Boolean = false,
    val time: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var isEdited: Boolean = false,
    var isUnsent: Boolean = false,
    var mediaUri: String? = null,
    var isVideo: Boolean = false,
    var audioUri: String? = null,
    var audioDuration: Int = 0 // Duration in seconds
)
