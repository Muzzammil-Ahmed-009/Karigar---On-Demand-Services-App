package com.karigar.app.data.model

import com.google.firebase.firestore.DocumentId

data class ServiceCategory(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var iconUrl: String = "",
    var localIconRes: String = "" // "cleaning", "repairing" to map to R.drawable if url is empty
)
