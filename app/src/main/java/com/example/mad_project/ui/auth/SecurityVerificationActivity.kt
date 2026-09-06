package com.karigar.app.ui.auth

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


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

class SecurityVerificationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KarigarTheme {
                SecurityVerificationScreen(
                    onStart = {
                        startActivity(Intent(this, EnterPhoneActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SecurityVerificationScreen(onStart: () -> Unit) {
    val scaleAnim = remember { Animatable(0.8f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scaleAnim.animateTo(1f, animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
        ))
        alphaAnim.animateTo(1f, animationSpec = tween(600))
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = Icons.Rounded.Security,
                contentDescription = "Security",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(TealPrimary)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Secure Your Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We use bank-level security to ensure your data and transactions are safe. Please verify your phone number to continue.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .alpha(alphaAnim.value)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            KarigarPrimaryButton(
                text = "Get Started",
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alphaAnim.value)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SecurityVerificationPreview() {
    KarigarTheme {
        SecurityVerificationScreen(onStart = {})
    }
}
