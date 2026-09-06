package com.karigar.app.data.model

import com.karigar.app.R


import com.karigar.app.ui.auth.*
import com.karigar.app.ui.splash.*
import com.karigar.app.ui.onboarding.*
import com.karigar.app.ui.main.*
import com.karigar.app.ui.home.*
import com.karigar.app.ui.orders.*
import com.karigar.app.ui.services.*
import com.karigar.app.ui.worker.*

import com.karigar.app.ui.chat.*
import com.karigar.app.ui.notifications.*
import com.karigar.app.ui.settings.*
import com.karigar.app.ui.profile.*
import com.karigar.app.ui.wallet.*
import com.karigar.app.ui.PromotionsFragment.*
import com.karigar.app.data.manager.*
import com.karigar.app.data.repository.*
import com.karigar.app.data.model.*
import com.karigar.app.adapter.*


import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    @DocumentId
    var id: String = "",
    var userId: String = "",
    var categories: List<String> = emptyList(),
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var details: String = "",
    var photoCount: Int = 0,
    var status: String = "Pending",
    // Valid: "Pending","Accepted","Assigned","Confirmed","Completed","Cancelled"
    var timestamp: Long = System.currentTimeMillis(),
    // Worker info (visible for Assigned, Confirmed, Completed)
    var assignedWorkerId: String = "",
    var assignedWorkerName: String = "",
    var assignedWorkerPhone: String = "",
    var assignedWorkerRating: Float = 0f,
    var assignedWorkerExperience: String = "",
    // Service icon resource name for mapping
    var serviceCategoryKey: String = "", // e.g. "cleaning","carpenter","electrician"
    // Extra
    var cancellationReason: String = "",
    var completionNote: String = "",
    var scheduledTime: String = "",
    // Bidding Model 2 fields
    var offeredFare: String = "",
    var bids: MutableList<WorkerBid> = mutableListOf(),
    // Added for Payment and Review flow
    var isPaid: Boolean = false,
    var isReviewed: Boolean = false
) : Parcelable
