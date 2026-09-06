package com.karigar.app.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.onboarding.OnboardingActivity
import android.content.Intent
import androidx.activity.enableEdgeToEdge

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            KarigarTheme {
                SettingsScreen(
                    onBackClick = {
                        finish()
                    },
                    onDeleteAccountClick = {
                        showDeleteAccountDialog()
                    },
                    onNotificationsToggled = { enabled ->
                        val msg = if (enabled) "Push notifications enabled" else "Push notifications disabled"
                        Toast.makeText(this@SettingsActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                Toast.makeText(this, "Account deletion request submitted.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
