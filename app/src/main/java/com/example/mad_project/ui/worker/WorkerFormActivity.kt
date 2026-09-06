package com.karigar.app.ui.worker

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

import androidx.activity.compose.setContent
import com.karigar.app.ui.theme.KarigarTheme


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkerFormActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KarigarTheme {
                WorkerFormScreen(
                    onBackClick = {
                        startActivity(Intent(this, BecomeWorkerActivity::class.java))
                        finish()
                    },
                    onSubmitClick = {
                        registerWorker()
                    }
                )
            }
        }
    }

    private fun registerWorker() {
        val uid = authRepository.getCurrentUserId() ?: return
        lifecycleScope.launch {
            // Fetch current user and update flags
            val result = authRepository.updateUserRole("worker")
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("users").document(uid).update("isRegisteredWorker", true).addOnSuccessListener {
                showRegistrationSuccessDialog()
            }
        }
    }



    private fun showRegistrationSuccessDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.registration_success_dialog, null)
        dialog.setContentView(view)
        dialog.show()

        val btnOk = view.findViewById<Button>(R.id.btnOk)
        btnOk.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }



}
