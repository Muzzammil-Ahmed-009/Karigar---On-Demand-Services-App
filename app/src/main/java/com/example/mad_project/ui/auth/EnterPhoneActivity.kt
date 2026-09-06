package com.karigar.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.hbb20.CountryCodePicker
import com.karigar.app.R
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextSecondary
import java.util.concurrent.TimeUnit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.karigar.app.data.repository.AuthRepository
import com.karigar.app.data.model.User
import com.karigar.app.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EnterPhoneActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            FirebaseApp.initializeApp(this)
            auth = FirebaseAuth.getInstance()
            auth.setLanguageCode("en")
            auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
        } catch (e: Exception) {
            Log.e("EnterPhoneActivity", "Firebase init failed: ${e.message}", e)
            Toast.makeText(this, "Firebase initialization failed.", Toast.LENGTH_LONG).show()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account?.idToken
                    if (idToken != null) {
                        lifecycleScope.launch {
                            val signInResult = authRepository.signInWithGoogle(idToken)
                            if (signInResult.isSuccess) {
                                val uid = signInResult.getOrNull() ?: return@launch
                                val existingUser = authRepository.getUserProfile(uid).first().getOrNull()
                                if (existingUser == null) {
                                    // New user, save profile
                                    val newUser = User(
                                        id = uid,
                                        name = account.displayName ?: "New User",
                                        email = account.email ?: "",
                                        profileImage = account.photoUrl?.toString() ?: ""
                                    )
                                    authRepository.saveUserProfile(newUser)
                                }
                                startActivity(Intent(this@EnterPhoneActivity, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@EnterPhoneActivity, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: ApiException) {
                    Toast.makeText(this, "Google Sign-In error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setContent {
            KarigarTheme {
                EnterPhoneScreen(
                    onSendCodeClick = { phoneWithCode ->
                        sendVerificationCode(phoneWithCode)
                    },
                    onGoogleSignInClick = {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    },
                    onBackClick = {
                        startActivity(Intent(this, SecurityVerificationActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    private var isSending by mutableStateOf(false)
    private var bindingPhone = ""

    @Composable
    fun EnterPhoneScreen(
        onSendCodeClick: (String) -> Unit,
        onGoogleSignInClick: () -> Unit,
        onBackClick: () -> Unit
    ) {
        var phoneNumber by remember { mutableStateOf("") }
        var phoneError by remember { mutableStateOf<String?>(null) }
        var countryCode by remember { mutableStateOf("+92") }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp), // Moved SECURITY up
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SECURITY header
                Text(
                    text = "SECURITY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TextSecondary,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 48.dp) // Pushed logo slightly down relative to SECURITY
                )

                // Custom Illustration (Teal)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(TealPrimary.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(TealPrimary.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(TealPrimary, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = "Security Lock",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    // Small floating chat bubble icon
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 10.dp, y = (-10).dp)
                            .size(24.dp)
                            .background(Color.White, androidx.compose.foundation.shape.CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = TealPrimary)
                    }
                    // Small floating shield icon
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-10).dp, y = 10.dp)
                            .size(24.dp)
                            .background(Color.White, androidx.compose.foundation.shape.CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Security, contentDescription = null, modifier = Modifier.size(14.dp), tint = TealPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(56.dp)) // Moved Verify Your Phone down

                // Heading
                Text(
                    text = "Verify Your Phone",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle
                Text(
                    text = "We'll send a 6-digit verification code to this number to secure your account.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary,
                        lineHeight = 24.sp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Input Field Label
                Text(
                    text = "PHONE NUMBER",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                // Phone Input Row with separate Country Code Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Wrapper for the XML Country Code Picker
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(end = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (LocalInspectionMode.current) {
                            Text(
                                text = "🇵🇰 +92",
                                modifier = Modifier.padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            AndroidView(
                                factory = { context ->
                                    CountryCodePicker(context).apply {
                                        setDefaultCountryUsingNameCode("PK")
                                        showNameCode(false)
                                        showFlag(true)
                                        setTextSize(40) // pixels
                                        setOnCountryChangeListener {
                                            countryCode = this.selectedCountryCodeWithPlus
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            phoneError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("(555) 000-0000", color = TextSecondary.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        isError = phoneError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                        ),
                        singleLine = true
                    )
                }

                if (phoneError != null) {
                    Text(
                        text = phoneError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                var termsAccepted by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
                    )
                    Text(
                        text = "I agree to the Terms & Conditions and Privacy Policy.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Send Code Button
                Button(
                    onClick = {
                        if (phoneNumber.length < 9) {
                            phoneError = "Invalid phone number"
                        } else {
                            isSending = true
                            val fullPhone = "$countryCode$phoneNumber"
                            bindingPhone = fullPhone
                            onSendCodeClick(fullPhone)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isSending && termsAccepted,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isSending) "Sending..." else "Send Code \u2192", // Unicode right arrow
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Divider: --- OR ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = TextSecondary.copy(alpha = 0.2f))
                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Divider(modifier = Modifier.weight(1f), color = TextSecondary.copy(alpha = 0.2f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Google Sign In Button
                Button(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary.copy(alpha = 0.2f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneAuthCallbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            isSending = false
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Toast.makeText(this@EnterPhoneActivity, "Auto verification completed", Toast.LENGTH_SHORT).show()
            signInWithCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            isSending = false
            val message = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format."
                is FirebaseTooManyRequestsException -> "Too many requests. Please try again later."
                else -> "Verification failed: ${e.message}"
            }
            Toast.makeText(this@EnterPhoneActivity, message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            isSending = false
            this@EnterPhoneActivity.verificationId = verificationId
            Toast.makeText(this@EnterPhoneActivity, "OTP sent to your phone", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@EnterPhoneActivity, OtpVerificationActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("phone", bindingPhone)
            intent.putExtra("resendToken", token)
            startActivity(intent)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, VerificationSuccessActivity::class.java))
                    finish()
                } else {
                    isSending = false
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun EnterPhoneScreenPreview() {
    KarigarTheme {
        EnterPhoneActivity().EnterPhoneScreen(
            onSendCodeClick = {},
            onGoogleSignInClick = {},
            onBackClick = {}
        )
    }
}
