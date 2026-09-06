package com.karigar.app.data.repository

import com.karigar.app.data.model.AppNotification
import com.karigar.app.data.model.Order
import kotlinx.coroutines.flow.StateFlow

interface OrderRepository {
    val orders: StateFlow<List<Order>>
    val notifications: StateFlow<List<AppNotification>>
    
    suspend fun placeOrder(order: Order)
    suspend fun updateOrderStatus(orderId: String, newStatus: String)
    suspend fun getOrderById(orderId: String): Order?
    suspend fun acceptBid(orderId: String, bid: com.karigar.app.data.model.WorkerBid)
    suspend fun submitBid(orderId: String, bid: com.karigar.app.data.model.WorkerBid)
    suspend fun submitReview(orderId: String, rating: Int, reviewText: String)
    
    fun getPendingOrders(): List<Order>
    fun getHistoryOrders(): List<Order>
    fun markAllNotificationsRead()
    
    val categories: StateFlow<List<com.karigar.app.data.model.ServiceCategory>>
}
