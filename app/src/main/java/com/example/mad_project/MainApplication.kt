package com.karigar.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.FirebaseApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // App Check removed for MVP/debug - not needed when enforcement is off.
        // Will be re-added before Play Store release with proper Play Integrity setup.
    }
}
