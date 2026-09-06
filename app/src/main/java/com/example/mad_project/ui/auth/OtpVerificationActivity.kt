package com.karigar.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.ui.main.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

class OtpVerificationActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        verificationId = intent.getStringExtra("verificationId")
        phoneNumber = intent.getStringExtra("phone")
        resendToken = intent.getParcelableExtra("resendToken")

        setContent {
            KarigarTheme {
                OtpScreen(
                    phone = phoneNumber ?: "",
                    onVerifyClick = { otp -> verifyCode(otp) },
                    onResendClick = { resendVerificationCode() },
                    onBackClick = {
                        startActivity(Intent(this, EnterPhoneActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    private var isVerifying by mutableStateOf(false)

    @Composable
    fun OtpScreen(
        phone: String,
        onVerifyClick: (String) -> Unit,
        onResendClick: () -> Unit,
        onBackClick: () -> Unit
    ) {
        var otpValue by remember { mutableStateOf("") }
        var timer by remember { mutableIntStateOf(60) }

        LaunchedEffect(timer) {
            if (timer > 0) {
                delay(1000)
                timer -= 1
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Verification",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 40.dp), // offset to center title
                        textAlign = TextAlign.Center
                    )
                }
                
                Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

                Spacer(modifier = Modifier.height(32.dp))

                // Top Icon (Teal Box with Shield and Lock)
                Box(
                    modifier = Modifier
                        .size(80.dp) // Reduced size as requested
                        .background(TealPrimary, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Outer shield
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = "Security Shield",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        // Inner lock to create a cutout effect
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(56.dp))

                Text(
                    text = "Enter the Code to Continue",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "We've sent a 6-digit verification code to\nyour registered device.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // OTP Input implementation
                BasicTextField(
                    value = otpValue,
                    onValueChange = {
                        if (it.length <= 6) {
                            otpValue = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            repeat(6) { index ->
                                val char = when {
                                    index >= otpValue.length -> ""
                                    else -> otpValue[index].toString()
                                }
                                val isFocused = otpValue.length == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = if (isFocused) TealPrimary else TextSecondary.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                KarigarPrimaryButton(
                    text = if (isVerifying) "Verifying..." else "Verify",
                    onClick = {
                        if (otpValue.length == 6) {
                            isVerifying = true
                            onVerifyClick(otpValue)
                        } else {
                            Toast.makeText(this@OtpVerificationActivity, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isVerifying && otpValue.length == 6,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Didn't receive the code?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (timer > 0) {
                    Text(
                        text = "Resend Code (${timer}s)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else {
                    Text(
                        text = "Resend Code",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable {
                            timer = 60
                            onResendClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 64.dp) // Pushed up from the bottom
                        .background(
                            color = Color(0xFFF2F6F9), // Light grayish-blue matching screenshot
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.karigar.app.R.drawable.otp2),
                        contentDescription = "OTP Illustration",
                        modifier = Modifier.height(80.dp), // Using height instead of size to preserve aspect ratio
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }
        }
    }

    private fun resendVerificationCode() {
        if (phoneNumber == null) return
        Toast.makeText(this, "Sending OTP...", Toast.LENGTH_SHORT).show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OtpVerificationActivity, e.message, Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@OtpVerificationActivity.verificationId = verificationId
                    this@OtpVerificationActivity.resendToken = token
                    Toast.makeText(this@OtpVerificationActivity, "OTP resent to your phone", Toast.LENGTH_SHORT).show()
                }
            })

        resendToken?.let { options.setForceResendingToken(it) }
        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    private fun verifyCode(code: String) {
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithCredential(credential)
        } else {
            isVerifying = false
            Toast.makeText(this, "Verification ID not found. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                isVerifying = false
                if (task.isSuccessful) {
                    Toast.makeText(this, "Phone number verified successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun OtpScreenPreview() {
    KarigarTheme {
        OtpVerificationActivity().OtpScreen(
            phone = "+92 300 1234567",
            onVerifyClick = {},
            onResendClick = {},
            onBackClick = {}
        )
    }
}
