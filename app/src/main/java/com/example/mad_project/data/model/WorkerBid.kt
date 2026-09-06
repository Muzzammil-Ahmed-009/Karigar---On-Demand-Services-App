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
data class WorkerBid(
    @DocumentId
    var id: String = "",
    var workerId: String = "",
    var workerName: String = "",
    var workerRating: Float = 0f,
    var jobsCompleted: Int = 0,
    var bidAmount: String = "",
    var etaString: String = "",
    var workerImageResId: Int = 0, // E.g., R.drawable.emp_profile
    var isAccepted: Boolean = false,
    var vehicleDetails: String = "" // Added for Module 5
) : Parcelable
