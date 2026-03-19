package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mad_project.databinding.ActivityOtpVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthOptions

class otp_verification : AppCompatActivity() {

    private val binding: ActivityOtpVerificationBinding by lazy {
        ActivityOtpVerificationBinding.inflate(layoutInflater)
    }
    
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var phoneNumber: String? = null
    
    // Timer handling
    private var isResendEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Get intent extras
        verificationId = intent.getStringExtra("verificationId")
        phoneNumber = intent.getStringExtra("phone")
        resendToken = intent.getParcelableExtra("resendToken")

        setupOtpInputs()
        startResendTimer()

        binding.btnVerifyOtp.setOnClickListener {
            val otp = "${binding.otpBox1.text}${binding.otpBox2.text}${binding.otpBox3.text}${binding.otpBox4.text}${binding.otpBox5.text}${binding.otpBox6.text}".trim()

            if (otp.length != 6) {
                Toast.makeText(this, "Please enter a 6-digit OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyCode(otp)
            }
        }


        binding.tvResend.setOnClickListener {
            if (isResendEnabled && phoneNumber != null) {
                resendVerificationCode()
            } else if (!isResendEnabled) {
                Toast.makeText(this, "Please wait for the timer to finish", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, enterphone::class.java))
            finish()
        }
    }
    
    private fun startResendTimer() {
        isResendEnabled = false
        binding.tvResend.setTextColor(android.graphics.Color.GRAY)
        
        object : android.os.CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvResend.text = "Resend Code (${millisUntilFinished / 1000}s)"
            }

            override fun onFinish() {
                isResendEnabled = true
                binding.tvResend.text = "Resend Code"
                binding.tvResend.setTextColor(android.graphics.Color.parseColor("#1D70F5"))
            }
        }.start()
    }
    
    private fun resendVerificationCode() {
        binding.btnVerifyOtp.isEnabled = false
        Toast.makeText(this, "Sending OTP...", Toast.LENGTH_SHORT).show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    binding.btnVerifyOtp.isEnabled = true
                    Toast.makeText(this@otp_verification, e.message, Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@otp_verification.verificationId = verificationId
                    this@otp_verification.resendToken = token
                    binding.btnVerifyOtp.isEnabled = true
                    Toast.makeText(this@otp_verification, "OTP resent to your phone", Toast.LENGTH_SHORT).show()
                    startResendTimer()
                }
            })

        if (resendToken != null) {
            options.setForceResendingToken(resendToken!!)
        }

        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }
    
    private fun verifyCode(code: String) {
        binding.btnVerifyOtp.isEnabled = false
        binding.btnVerifyOtp.text = "Verifying..."
        
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithCredential(credential)
        } else {
            binding.btnVerifyOtp.isEnabled = true
            binding.btnVerifyOtp.text = "Verify"
            Toast.makeText(this, "Verification ID not found. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                binding.btnVerifyOtp.isEnabled = true
                binding.btnVerifyOtp.text = "Verify"
                
                if (task.isSuccessful) {
                    // Verification successful
                    Toast.makeText(this, "Phone number verified successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, verification_success::class.java))
                    finish()
                } else {
                    // Verification failed
                    Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    
    private fun setupOtpInputs() {
        val otpBoxes = arrayOf(
            binding.otpBox1, binding.otpBox2, binding.otpBox3,
            binding.otpBox4, binding.otpBox5, binding.otpBox6
        )

        for (i in otpBoxes.indices) {
            otpBoxes[i].addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < otpBoxes.size - 1) {
                        otpBoxes[i + 1].requestFocus()
                    }
                }
                
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
            
            // Handle backspace properly
            otpBoxes[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && 
                    event.action == android.view.KeyEvent.ACTION_DOWN &&
                    otpBoxes[i].text.isEmpty() && i > 0) {
                    otpBoxes[i - 1].requestFocus()
                    otpBoxes[i - 1].text.clear()
                    true
                } else {
                    false
                }
            }
        }
    }
}
