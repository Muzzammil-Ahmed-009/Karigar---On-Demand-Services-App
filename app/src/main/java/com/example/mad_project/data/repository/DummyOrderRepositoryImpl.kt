package com.karigar.app.data.repository

import com.karigar.app.R
import com.karigar.app.data.model.AppNotification
import com.karigar.app.data.model.Order
import com.karigar.app.data.model.WorkerBid
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyOrderRepositoryImpl @Inject constructor() : OrderRepository {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    override val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    override val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _categories = MutableStateFlow<List<com.karigar.app.data.model.ServiceCategory>>(emptyList())
    override val categories: StateFlow<List<com.karigar.app.data.model.ServiceCategory>> = _categories.asStateFlow()

    init {
        loadSampleData()
    }

    private fun loadSampleData() {
        val baseTime = System.currentTimeMillis()
        _orders.value = listOf(
            Order(id = UUID.randomUUID().toString(), categories = listOf("Plumber"), address = "House 12, Block B, DHA Karachi",
                details = "Plumber needed for kitchen pipe leakage and bathroom fittings.", photoCount = 2,
                status = "Pending", timestamp = baseTime - 60_000, serviceCategoryKey = "plumber",
                scheduledTime = "Today, 4:00 PM")
        )
    }

    override suspend fun placeOrder(order: Order) {
        // Add order to state
        _orders.value = listOf(order) + _orders.value
        
        // Notify
        val categoryText = order.categories.joinToString(", ")
        _notifications.value = listOf(AppNotification(
            id = UUID.randomUUID().toString(),
            title = "Order Placed! 🎉",
            message = "Your request for \"$categoryText\" is live. Waiting for workers to bid.",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            type = "placed"
        )) + _notifications.value

        // Simulate bids arriving after 3 seconds asynchronously
        delay(3000)
        
        val currentOrders = _orders.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == order.id }
        if (index != -1 && currentOrders[index].status == "Pending") {
            val targetOrder = currentOrders[index].copy()
            val baseFare = targetOrder.offeredFare.toIntOrNull() ?: 500
            
            val bids = mutableListOf<WorkerBid>()
            bids.add(WorkerBid(
                id = UUID.randomUUID().toString(),
                workerName = "Ali Raza",
                workerRating = 4.8f,
                jobsCompleted = 124,
                bidAmount = baseFare.toString(),
                etaString = "10 mins away",
                workerImageResId = R.drawable.emp_profile
            ))
            bids.add(WorkerBid(
                id = UUID.randomUUID().toString(),
                workerName = "Kamran Khan",
                workerRating = 4.5f,
                jobsCompleted = 45,
                bidAmount = (baseFare - 50).toString(),
                etaString = "25 mins away",
                workerImageResId = R.drawable.emp_profile
            ))
            
            targetOrder.bids.addAll(bids)
            currentOrders[index] = targetOrder
            _orders.value = currentOrders
            
            _notifications.value = listOf(AppNotification(
                id = UUID.randomUUID().toString(),
                title = "New Bids Received! 🔔",
                message = "New workers have placed bids on your $categoryText order.",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                type = "bids"
            )) + _notifications.value
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        val currentOrders = _orders.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val order = currentOrders[index].copy(status = newStatus)
            currentOrders[index] = order
            _orders.value = currentOrders
        }
    }

    override suspend fun acceptBid(orderId: String, bid: WorkerBid) {
        val currentOrders = _orders.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val order = currentOrders[index].copy(
                status = "Assigned",
                assignedWorkerName = bid.workerName,
                assignedWorkerRating = bid.workerRating,
                assignedWorkerExperience = "${bid.jobsCompleted} jobs completed"
            )
            currentOrders[index] = order
            _orders.value = currentOrders
            
            _notifications.value = listOf(AppNotification(
                id = UUID.randomUUID().toString(),
                title = "Worker Assigned! 👨‍🔧",
                message = "${bid.workerName} has been assigned to your order. ETA: ${bid.etaString}.",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                type = "assigned"
            )) + _notifications.value
        }
    }

    override suspend fun getOrderById(orderId: String): Order? {
        return _orders.value.find { it.id == orderId }
    }

    override fun getPendingOrders(): List<Order> {
        return _orders.value.filter { it.status in listOf("Pending", "Accepted", "Assigned", "Confirmed") }
    }

    override fun getHistoryOrders(): List<Order> {
        return _orders.value.filter { it.status in listOf("Completed", "Cancelled") }
    }

    override suspend fun submitBid(orderId: String, bid: WorkerBid) {
        // Dummy implementation - not used in production
    }

    override suspend fun submitReview(orderId: String, rating: Int, reviewText: String) {
        // Dummy implementation - not used in production
    }

    override fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}
