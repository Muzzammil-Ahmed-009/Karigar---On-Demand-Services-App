package com.karigar.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.karigar.app.data.model.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun getMessages(orderId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listenerRegistration = firestore.collection("chats")
            .document(orderId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val messages = snapshot?.documents?.mapNotNull { doc -> 
                    try {
                        doc.toObject(ChatMessage::class.java)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(messages)
            }
            
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun sendMessage(orderId: String, message: ChatMessage): Result<Unit> {
        return try {
            val messageRef = if (message.id.isEmpty()) {
                firestore.collection("chats").document(orderId).collection("messages").document()
            } else {
                firestore.collection("chats").document(orderId).collection("messages").document(message.id)
            }
            
            val messageToSave = message.copy(id = messageRef.id)
            messageRef.set(messageToSave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(orderId: String, messageId: String): Result<Unit> {
        return try {
            firestore.collection("chats")
                .document(orderId)
                .collection("messages")
                .document(messageId)
                .update(
                    mapOf(
                        "unsent" to true,
                        "text" to "",
                        "mediaUri" to null,
                        "audioUri" to null
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun editMessage(orderId: String, messageId: String, newText: String): Result<Unit> {
        return try {
            firestore.collection("chats")
                .document(orderId)
                .collection("messages")
                .document(messageId)
                .update(
                    mapOf(
                        "text" to newText,
                        "edited" to true
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
