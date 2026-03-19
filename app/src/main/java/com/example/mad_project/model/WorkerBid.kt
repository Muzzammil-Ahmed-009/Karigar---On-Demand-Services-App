package com.example.mad_project.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkerBid(
    val id: String,
    val workerName: String,
    val workerRating: Float,
    val jobsCompleted: Int,
    val bidAmount: String,
    val etaString: String,
    val workerImageResId: Int, // E.g., R.drawable.emp_profile
    var isAccepted: Boolean = false
) : Parcelable
