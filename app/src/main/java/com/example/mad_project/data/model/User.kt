package com.karigar.app.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    var id: String = "",
    var phone: String = "",
    var name: String = "",
    var currentRole: String = "customer", // "customer" or "worker"
    var isRegisteredWorker: Boolean = false,
    var profileImage: String = "",
    var email: String = "",
    var city: String = "",
    var gender: String = "Male",
    var createdAt: Long = System.currentTimeMillis(),
    var walletBalance: Double = 0.0
)
