package com.karigar.app.data.model

import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    var id: String = "",
    var orderId: String = "",
    var customerId: String = "",
    var workerId: String = "",
    var rating: Int = 0,
    var reviewText: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
