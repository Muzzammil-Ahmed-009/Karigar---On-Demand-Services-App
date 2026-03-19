package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mad_project.databinding.ActivityEnterphoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.FirebaseApp
import java.util.concurrent.TimeUnit

class enterphone : AppCompatActivity() {

    private val binding: ActivityEnterphoneBinding by lazy {
        ActivityEnterphoneBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase and Auth
        try {
            FirebaseApp.initializeApp(this)
            auth = FirebaseAuth.getInstance()
            
        } catch (e: Exception) {
            Log.e("enterphone", "Firebase init failed: ${e.message}", e)
            Toast.makeText(this, "Firebase initialization failed.", Toast.LENGTH_LONG).show()
            return
        }

        // Register phone edit text with the CCP widget
        binding.ccp.registerCarrierNumberEditText(binding.etPhone)

        binding.btnVerify.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()

            when {
                phone.isEmpty() -> {
                    binding.etPhone.error = "Please enter phone number"
                }
                !binding.ccp.isValidFullNumber -> {
                    binding.etPhone.error = "Invalid phone number"
                }
                else -> {
                    val phoneNumber = binding.ccp.fullNumberWithPlus
                    sendVerificationCode(phoneNumber)
                }
            }
        }

        binding.cross.setOnClickListener {
            startActivity(Intent(this, securityverification::class.java))
            finish()
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        binding.btnVerify.isEnabled = false
        binding.btnVerify.text = "Sending..."

        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneAuthCallbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            Log.e("enterphone", "verifyPhoneNumber failed: ${e.message}", e)
            binding.btnVerify.isEnabled = true
            binding.btnVerify.text = "Send Code  →"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Toast.makeText(this@enterphone, "Auto verification completed", Toast.LENGTH_SHORT).show()
            signInWithCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            binding.btnVerify.isEnabled = true
            binding.btnVerify.text = "Send Code  →"

            val message = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format."
                is FirebaseTooManyRequestsException -> "Too many requests. Please try again later."
                else -> "Verification failed: ${e.message}"
            }
            Log.e("enterphone", "onVerificationFailed: ${e.message}", e)
            Toast.makeText(this@enterphone, message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@enterphone.verificationId = verificationId
            binding.btnVerify.isEnabled = true
            binding.btnVerify.text = "Send Code  →"

            Toast.makeText(this@enterphone, "OTP sent to your phone", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@enterphone, otp_verification::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("phone", binding.ccp.fullNumberWithPlus)
            intent.putExtra("resendToken", token)
            startActivity(intent)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, verification_success::class.java))
                    finish()
                } else {
                    Log.e("enterphone", "signInWithCredential failed: ${task.exception?.message}")
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
