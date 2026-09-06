package com.karigar.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karigar.app.data.model.ChatMessage
import com.karigar.app.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun loadMessages(orderId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(orderId).collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(orderId: String, message: ChatMessage) {
        viewModelScope.launch {
            chatRepository.sendMessage(orderId, message)
        }
    }

    fun deleteMessage(orderId: String, messageId: String) {
        viewModelScope.launch {
            chatRepository.deleteMessage(orderId, messageId)
        }
    }

    fun editMessage(orderId: String, messageId: String, newText: String) {
        viewModelScope.launch {
            chatRepository.editMessage(orderId, messageId, newText)
        }
    }
}
