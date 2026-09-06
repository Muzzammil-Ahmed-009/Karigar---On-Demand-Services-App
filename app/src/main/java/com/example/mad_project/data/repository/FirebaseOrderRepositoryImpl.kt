package com.karigar.app.data.repository

import android.R.attr.order
import com.karigar.app.data.model.AppNotification
import com.karigar.app.data.model.Order
import com.karigar.app.data.model.WorkerBid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseOrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : OrderRepository {

    private val ordersCollection = firestore.collection("orders")
    private val notificationsCollection = firestore.collection("notifications")

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    override val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    override val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val categoriesCollection = firestore.collection("categories")
    private val _categories = MutableStateFlow<List<com.karigar.app.data.model.ServiceCategory>>(emptyList())
    override val categories: StateFlow<List<com.karigar.app.data.model.ServiceCategory>> = _categories.asStateFlow()

    init {
        listenToOrders()
        listenToNotifications()
        listenToCategories()
    }

    private fun listenToCategories() {
        categoriesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null) {
                val list = snapshot.toObjects(com.karigar.app.data.model.ServiceCategory::class.java)
                if (list.isEmpty()) {
                    populateDefaultCategories()
                } else {
                    _categories.value = list
                }
            }
        }
    }

    private fun populateDefaultCategories() {
        val defaults = listOf(
            com.karigar.app.data.model.ServiceCategory(name = "Cleaning", localIconRes = "cleaning"),
            com.karigar.app.data.model.ServiceCategory(name = "Repairing", localIconRes = "repairing"),
            com.karigar.app.data.model.ServiceCategory(name = "Carpenter", localIconRes = "carpenter"),
            com.karigar.app.data.model.ServiceCategory(name = "Electrician", localIconRes = "electrician"),
            com.karigar.app.data.model.ServiceCategory(name = "Plumbing", localIconRes = "plumber"),
            com.karigar.app.data.model.ServiceCategory(name = "Painting", localIconRes = "painter"),
            com.karigar.app.data.model.ServiceCategory(name = "Shifting", localIconRes = "shifting"),
            com.karigar.app.data.model.ServiceCategory(name = "AC Repair", localIconRes = "ac_repair"),
            com.karigar.app.data.model.ServiceCategory(name = "Security", localIconRes = "security"),
            com.karigar.app.data.model.ServiceCategory(name = "Gardening", localIconRes = "gardener"),
            com.karigar.app.data.model.ServiceCategory(name = "More", localIconRes = "more")
        )
        val batch = firestore.batch()
        defaults.forEach { cat ->
            val ref = categoriesCollection.document()
            batch.set(ref, cat)
        }
        batch.commit()
    }

    private fun listenToOrders() {
        ordersCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val ordersList = snapshot.toObjects(Order::class.java)
                    _orders.value = ordersList
                }
            }
    }

    private fun listenToNotifications() {
        val uid = auth.currentUser?.uid ?: return
        notificationsCollection
            .whereEqualTo("userId", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val notifList = snapshot.toObjects(AppNotification::class.java)
                    _notifications.value = notifList
                }
            }
    }

    override suspend fun placeOrder(order: Order) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = ordersCollection.document()
        val newOrder = order.copy(id = docRef.id, userId = uid)
        docRef.set(newOrder).await()
        
        // Notify
        val categoryText = order.categories.joinToString(", ")
        val notificationRef = notificationsCollection.document()
        val appNotif = AppNotification(
            id = notificationRef.id,
            title = "Order Placed! 🎉",
            message = "Your request for \"$categoryText\" is live. Waiting for workers to bid.",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            type = "placed",
            userId = uid
        )
        notificationRef.set(appNotif)
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        ordersCollection.document(orderId).update("status", newStatus).await()
    }

    override suspend fun acceptBid(orderId: String, bid: WorkerBid) {
        val docRef = ordersCollection.document(orderId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val order = snapshot.toObject(Order::class.java)
            if (order != null) {
                order.bids.forEach { it.isAccepted = (it.id == bid.id) }
                order.status = "Assigned"
                order.assignedWorkerId = bid.workerId
                order.assignedWorkerName = bid.workerName
                order.assignedWorkerRating = bid.workerRating
                order.offeredFare = bid.bidAmount
                order.assignedWorkerExperience = "${bid.jobsCompleted} jobs completed"
                transaction.set(docRef, order)
            }
        }.await()
        val notificationRef = notificationsCollection.document()
        val notif = AppNotification(
            id = notificationRef.id,
            title = "Worker Assigned! \uD83D\uDC68\u200D\uD83D\uDD27",
            message = "${bid.workerName} has been assigned to your order. ETA: ${bid.etaString}.",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            type = "assigned",
            userId = auth.currentUser?.uid ?: ""
        )
        notificationRef.set(notif)
    }

    override suspend fun submitBid(orderId: String, bid: WorkerBid) {
        val docRef = ordersCollection.document(orderId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val order = snapshot.toObject(Order::class.java)
            if (order != null && order.status == "Pending") {
                order.bids.add(bid)
                transaction.set(docRef, order)
            }
        }.await()
    }

    override suspend fun submitReview(orderId: String, rating: Int, reviewText: String) {
        val docRef = ordersCollection.document(orderId)
        val snapshot = docRef.get().await()
        val order = snapshot.toObject(Order::class.java) ?: return
        val uid = auth.currentUser?.uid ?: return

        // 1. Mark order as reviewed
        docRef.update("isReviewed", true).await()

        // 2. Save review to reviews collection
        val reviewsCollection = firestore.collection("reviews")
        val reviewRef = reviewsCollection.document()
        val review = com.karigar.app.data.model.Review(
            id = reviewRef.id,
            orderId = orderId,
            customerId = uid,
            workerId = order.assignedWorkerId,
            rating = rating,
            reviewText = reviewText
        )
        reviewRef.set(review).await()

        // (Optional for future) Update worker's average rating in their user profile using a Firebase Function or here
    }

    override suspend fun getOrderById(orderId: String): Order? {
        return ordersCollection.document(orderId).get().await().toObject(Order::class.java)
    }

    override fun getPendingOrders(): List<Order> {
        val uid = auth.currentUser?.uid
        return _orders.value.filter { 
            it.userId == uid && it.status in listOf("Pending", "Accepted", "Assigned", "Confirmed") 
        }
    }

    override fun getHistoryOrders(): List<Order> {
        val uid = auth.currentUser?.uid
        return _orders.value.filter { 
            it.userId == uid && it.status in listOf("Completed", "Cancelled") 
        }
    }

    override fun markAllNotificationsRead() {
        val unread = _notifications.value.filter { !it.isRead }
        val batch = firestore.batch()
        unread.forEach { notif ->
            val ref = notificationsCollection.document(notif.id)
            batch.update(ref, "isRead", true)
        }
        batch.commit()
    }
}
