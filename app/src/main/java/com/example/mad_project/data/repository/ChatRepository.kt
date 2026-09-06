package com.karigar.app.data.repository

import com.karigar.app.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(orderId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(orderId: String, message: ChatMessage): Result<Unit>
    suspend fun deleteMessage(orderId: String, messageId: String): Result<Unit>
    suspend fun editMessage(orderId: String, messageId: String, newText: String): Result<Unit>
}
