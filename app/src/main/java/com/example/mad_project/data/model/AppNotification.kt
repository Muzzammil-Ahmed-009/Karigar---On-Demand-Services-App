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


import com.google.firebase.firestore.DocumentId

data class AppNotification(
    @DocumentId
    var id: String = "",
    var title: String = "",
    var message: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false,
    var type: String = "placed",
    var userId: String = ""
    // Types: "placed","accepted","confirm","assigned","completed","cancelled","announcement"
)
