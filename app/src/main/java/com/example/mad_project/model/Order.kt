package com.example.mad_project.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String,
    val categories: List<String>,
    val address: String,
    val details: String,
    val photoCount: Int,
    var status: String = "Pending",
    // Valid: "Pending","Accepted","Assigned","Confirmed","Completed","Cancelled"
    val timestamp: Long = System.currentTimeMillis(),
    // Worker info (visible for Assigned, Confirmed, Completed)
    val assignedWorkerName: String = "",
    val assignedWorkerPhone: String = "",
    val assignedWorkerRating: Float = 0f,
    val assignedWorkerExperience: String = "",
    // Service icon resource name for mapping
    val serviceCategoryKey: String = "", // e.g. "cleaning","carpenter","electrician"
    // Extra
    var cancellationReason: String = "",
    val completionNote: String = "",
    val scheduledTime: String = "",
    // Bidding Model 2 fields
    val offeredFare: String = "",
    val bids: MutableList<WorkerBid> = mutableListOf()
) : Parcelable
