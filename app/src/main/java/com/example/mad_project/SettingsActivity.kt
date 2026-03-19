package com.example.mad_project

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchNightMode = findViewById<SwitchCompat>(R.id.switchNightMode)
        val switchPushNotif = findViewById<SwitchCompat>(R.id.switchPushNotif)
        val btnDeleteAccount = findViewById<LinearLayout>(R.id.btnDeleteAccount)
        val btnBack = findViewById<android.widget.ImageView>(R.id.btnSettingsBack)

        // Set current dark mode state
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        switchNightMode.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES

        // Night mode toggle
        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Push notifications toggle
        switchPushNotif.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Push notifications enabled" else "Push notifications disabled"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Delete Account
        btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    Toast.makeText(this, "Account deletion request submitted.", Toast.LENGTH_LONG).show()
                    // Navigate to onboarding and clear stack
                    val intent = android.content.Intent(this, OnboardingActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnBack.setOnClickListener { finish() }
    }
}
