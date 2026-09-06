package com.karigar.app.ui.settings

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
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.karigar.app.databinding.ActivityContactUsBinding
import com.karigar.app.databinding.ActivityOtpVerificationBinding

import androidx.activity.compose.setContent
import com.karigar.app.ui.theme.KarigarTheme

class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            KarigarTheme {
                ContactUsScreen(
                    onBackClick = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onCallClick = {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel: 03182118652")
                        startActivity(intent)
                    },
                    onEmailClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto: muzammilrazzaq009@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}
