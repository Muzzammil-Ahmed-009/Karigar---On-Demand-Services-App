package com.karigar.app.ui.notifications

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.karigar.app.data.repository.OrderRepository
import com.karigar.app.ui.theme.KarigarTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsActivity : AppCompatActivity() {

    @Inject
    lateinit var orderRepository: OrderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            KarigarTheme {
                val notifications by orderRepository.notifications.collectAsState(initial = emptyList())
                
                NotificationsScreen(
                    notifications = notifications,
                    onBackClick = { finish() },
                    onMarkAllReadClick = { orderRepository.markAllNotificationsRead() }
                )
            }
        }
    }
}
