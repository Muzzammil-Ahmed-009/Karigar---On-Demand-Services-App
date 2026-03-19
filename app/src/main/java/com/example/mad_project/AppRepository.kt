package com.example.mad_project

import com.example.mad_project.model.AppNotification
import com.example.mad_project.model.Order
import com.example.mad_project.model.WorkerBid
import android.os.Handler
import android.os.Looper
import java.util.UUID

data class FavoriteWorker(
    val id: String,
    val name: String,
    val profession: String,
    val rating: Float,
    val phone: String
)

object AppRepository {

    val orders = mutableListOf<Order>()
    val notifications = mutableListOf<AppNotification>()
    val favoriteWorkers = mutableListOf<FavoriteWorker>()
    
    // Callback to notify UI when bids arrive
    var onBidsUpdatedListener: ((String) -> Unit)? = null

    init {
        loadSampleData()
    }

    private fun loadSampleData() {
        // Sample orders covering all 6 statuses for testing
        val baseTime = System.currentTimeMillis()
        orders.addAll(listOf(
            Order(id = UUID.randomUUID().toString(), categories = listOf("Plumber"), address = "House 12, Block B, DHA Karachi",
                details = "Plumber needed for kitchen pipe leakage and bathroom fittings.", photoCount = 2,
                status = "Pending", timestamp = baseTime - 60_000, serviceCategoryKey = "plumber",
                scheduledTime = "Today, 4:00 PM"),
            Order(id = UUID.randomUUID().toString(), categories = listOf("Electrician"), address = "Flat 5, Gulshan-e-Iqbal, Karachi",
                details = "Fix wiring in the living room and install new ceiling fan.", photoCount = 3,
                status = "Accepted", timestamp = baseTime - 3_600_000, serviceCategoryKey = "electrician",
                scheduledTime = "Tomorrow, 10:00 AM"),
            Order(id = UUID.randomUUID().toString(), categories = listOf("Cleaning"), address = "Villa 7, Bahria Town Phase 4",
                details = "Deep cleaning of 4 rooms, kitchen, and 2 bathrooms.", photoCount = 1,
                status = "Assigned", timestamp = baseTime - 7_200_000, serviceCategoryKey = "cleaning",
                assignedWorkerName = "Muhammad Asif", assignedWorkerPhone = "+923001234567",
                assignedWorkerRating = 4.8f, assignedWorkerExperience = "5 years experience",
                scheduledTime = "Today, 2:00 PM"),
            Order(id = UUID.randomUUID().toString(), categories = listOf("Carpenter"), address = "Apartment 3B, Clifton Block 2",
                details = "Repair wardrobe doors and install 2 new shelves in bedroom.", photoCount = 4,
                status = "Confirmed", timestamp = baseTime - 14_400_000, serviceCategoryKey = "carpenter",
                assignedWorkerName = "Ali Raza", assignedWorkerPhone = "+923009876543",
                assignedWorkerRating = 4.6f, assignedWorkerExperience = "8 years experience",
                scheduledTime = "Today, 12:00 PM"),
            Order(id = UUID.randomUUID().toString(), categories = listOf("Painter"), address = "House 45, Model Town Lahore",
                details = "Paint 3 rooms with premium emulsion paint, including ceiling.", photoCount = 5,
                status = "Completed", timestamp = baseTime - 86_400_000, serviceCategoryKey = "painter",
                assignedWorkerName = "Tariq Mehmood", assignedWorkerPhone = "+923331234567",
                assignedWorkerRating = 4.9f, assignedWorkerExperience = "10 years experience",
                completionNote = "Work completed to highest standard. All rooms fully painted.",
                scheduledTime = "Yesterday, 9:00 AM"),
            Order(id = UUID.randomUUID().toString(), categories = listOf("Gardener"), address = "Bungalow 22, F-6/3, Islamabad",
                details = "Monthly garden maintenance — trimming, weeding, and fertilizing.", photoCount = 0,
                status = "Cancelled", timestamp = baseTime - 172_800_000, serviceCategoryKey = "gardener",
                cancellationReason = "Worker was unavailable at the scheduled time.",
                scheduledTime = "2 days ago, 11:00 AM")
        ))

        // Sample notifications
        notifications.addAll(listOf(
            AppNotification(id = UUID.randomUUID().toString(), title = "Order Accepted",
                message = "We have accepted your order. Click to view details.", timestamp = baseTime - 7_200_000,
                isRead = false, type = "accepted"),
            AppNotification(id = UUID.randomUUID().toString(), title = "Confirm Order",
                message = "We have added items in your order. Please check and confirm.", timestamp = baseTime - 7_200_000,
                isRead = false, type = "confirm"),
            AppNotification(id = UUID.randomUUID().toString(), title = "Order Assigned",
                message = "We have assigned your order to a worker. Click to view details.", timestamp = baseTime - 7_200_000,
                isRead = false, type = "assigned"),
            AppNotification(id = UUID.randomUUID().toString(), title = "Order Completed",
                message = "Your order has been completed. Please check the work done.", timestamp = baseTime - 7_200_000,
                isRead = true, type = "completed"),
            AppNotification(id = UUID.randomUUID().toString(), title = "Order Cancelled",
                message = "Your order has been cancelled. Click to view details.", timestamp = baseTime - 7_200_000,
                isRead = true, type = "cancelled"),
            AppNotification(id = UUID.randomUUID().toString(), title = "Announcement",
                message = "Our service will be down tomorrow for planned maintenance.", timestamp = baseTime - 7_200_000,
                isRead = true, type = "announcement")
        ))
    }

    fun placeOrder(order: Order) {
        orders.add(0, order)
        val categoryText = order.categories.joinToString(", ")
        notifications.add(0, AppNotification(
            id = UUID.randomUUID().toString(),
            title = "Order Placed! 🎉",
            message = "Your request for \"$categoryText\" is live. Waiting for workers to bid.",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            type = "placed"
        ))
        
        // Simulate bids arriving after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val targetOrder = orders.find { it.id == order.id }
            if (targetOrder != null && targetOrder.status == "Pending") {
                // Generate 3 simulated bids based on the offered fare
                val baseFare = targetOrder.offeredFare.toIntOrNull() ?: 500
                
                targetOrder.bids.add(WorkerBid(
                    id = UUID.randomUUID().toString(),
                    workerName = "Ali Raza",
                    workerRating = 4.8f,
                    jobsCompleted = 124,
                    bidAmount = baseFare.toString(), // Exact match
                    etaString = "10 mins away",
                    workerImageResId = R.drawable.emp_profile
                ))
                
                targetOrder.bids.add(WorkerBid(
                    id = UUID.randomUUID().toString(),
                    workerName = "Kamran Khan",
                    workerRating = 4.5f,
                    jobsCompleted = 45,
                    bidAmount = (baseFare - 50).toString(), // Cheaper
                    etaString = "25 mins away",
                    workerImageResId = R.drawable.emp_profile
                ))
                
                targetOrder.bids.add(WorkerBid(
                    id = UUID.randomUUID().toString(),
                    workerName = "Bilal Ahmed",
                    workerRating = 5.0f,
                    jobsCompleted = 312,
                    bidAmount = (baseFare + 100).toString(), // More expensive but top rated
                    etaString = "5 mins away",
                    workerImageResId = R.drawable.emp_profile
                ))
                
                // Add a notification that bids have arrived
                notifications.add(0, AppNotification(
                    id = UUID.randomUUID().toString(),
                    title = "New Bids Received! 🔔",
                    message = "3 workers have placed bids on your $categoryText order.",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    type = "bids"
                ))
                
                // Trigger UI update
                onBidsUpdatedListener?.invoke(targetOrder.id)
            }
        }, 3000)
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        orders.find { it.id == orderId }?.status = newStatus
    }

    fun getOrderById(orderId: String): Order? = orders.find { it.id == orderId }

    fun getPendingOrders(): List<Order> =
        orders.filter { it.status in listOf("Pending", "Accepted", "Assigned", "Confirmed") }

    fun getHistoryOrders(): List<Order> =
        orders.filter { it.status in listOf("Completed", "Cancelled") }

    fun unreadNotificationCount(): Int = notifications.count { !it.isRead }

    fun markAllNotificationsRead() {
        notifications.forEach { it.isRead = true }
    }

    fun toggleFavoriteWorker(worker: FavoriteWorker) {
        val existing = favoriteWorkers.find { it.phone == worker.phone }
        if (existing != null) {
            favoriteWorkers.remove(existing)
        } else {
            favoriteWorkers.add(worker)
        }
    }

    fun isWorkerFavorite(phone: String): Boolean {
        return favoriteWorkers.any { it.phone == phone }
    }
}
