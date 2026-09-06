package com.karigar.app.data.model

import com.google.firebase.firestore.DocumentId

data class WalletTransaction(
    @DocumentId
    var id: String = "",
    var userId: String = "",
    var amount: Double = 0.0,
    var type: String = "credit", // "credit" or "debit"
    var title: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
