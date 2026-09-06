package com.karigar.app.ui.main

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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private var currentRole: String = "customer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_page)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val composeBottomNav = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.composeBottomNav)
        val composeTopBar = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.composeTopBar)
        val composeDrawer = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.composeDrawer)

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var currentNavRole by androidx.compose.runtime.mutableStateOf(com.karigar.app.ui.components.NavRole.CUSTOMER)
        var selectedNavId by androidx.compose.runtime.mutableIntStateOf(1)

        // Fetch User Role
        val uid = authRepository.getCurrentUserId()
        if (uid != null) {
            lifecycleScope.launch {
                authRepository.getUserProfile(uid).collect { result ->
                    result.onSuccess { user ->
                        if (user != null && user.currentRole != currentRole) {
                            currentRole = user.currentRole
                            currentNavRole = if (currentRole == "worker") com.karigar.app.ui.components.NavRole.WORKER else com.karigar.app.ui.components.NavRole.CUSTOMER
                            selectedNavId = if (currentRole == "worker") 5 else 1
                            if (currentRole == "worker") {
                                loadFragment(com.karigar.app.ui.worker.WorkerFeedFragment())
                            } else {
                                loadFragment(HomeFragment())
                            }
                        }
                    }
                }
            }
        }

        val showRateDialog = androidx.compose.runtime.mutableStateOf(false)
        val showLogoutDialog = androidx.compose.runtime.mutableStateOf(false)

        // ── DRAWER (Compose) ──────────────────────────────
        composeDrawer.setContent {
            com.karigar.app.ui.theme.KarigarTheme {
                com.karigar.app.ui.components.KarigarDrawer(
                    onCloseClick = { drawerLayout.closeDrawer(GravityCompat.START) },
                    onNavigate = { route ->
                        drawerLayout.closeDrawer(GravityCompat.START)
                        when (route) {
                            "BecomeWorker" -> startActivity(Intent(this@MainActivity, BecomeWorkerActivity::class.java))
                            "RegisterCompany" -> startActivity(Intent(this@MainActivity, RegisterCompanyIntroActivity::class.java))
                            "ContactUs" -> startActivity(Intent(this@MainActivity, ContactUsActivity::class.java))
                            "Settings" -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                            "Share" -> shareApp()
                            "RateUs" -> showRateDialog.value = true
                            "LogOut" -> showLogoutDialog.value = true
                        }
                    }
                )
            }
        }

        // ── INITIAL FRAGMENT ─────────────────────────────
        if (savedInstanceState == null) {
            currentNavRole = if (currentRole == "worker") com.karigar.app.ui.components.NavRole.WORKER else com.karigar.app.ui.components.NavRole.CUSTOMER
            selectedNavId = if (currentRole == "worker") 5 else 1
            if (currentRole == "worker") {
                loadFragment(com.karigar.app.ui.worker.WorkerFeedFragment())
            } else {
                loadFragment(HomeFragment())
            }
        }

        // ── TOP BAR (Compose) ──────────────────────────────
        composeTopBar.setContent {
            com.karigar.app.ui.theme.KarigarTheme {
                com.karigar.app.ui.components.KarigarTopBar(
                    onMenuClick = { drawerLayout.openDrawer(GravityCompat.START) },
                    onNotificationsClick = { startActivity(Intent(this@MainActivity, NotificationsActivity::class.java)) }
                )
            }
        }

        // Compose Bottom Nav Setup
        composeBottomNav.setContent {
            com.karigar.app.ui.theme.KarigarTheme {

                if (showRateDialog.value) {
                    com.karigar.app.ui.components.RateUsDialog(
                        onDismiss = { showRateDialog.value = false },
                        onSubmit = { rating ->
                            showRateDialog.value = false
                            Toast.makeText(this@MainActivity, "Thanks for rating us $rating stars!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (showLogoutDialog.value) {
                    com.karigar.app.ui.components.LogoutDialog(
                        onDismiss = { showLogoutDialog.value = false },
                        onConfirm = {
                            showLogoutDialog.value = false
                            lifecycleScope.launch {
                                authRepository.logout()
                                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                                finish()
                            }
                        }
                    )
                }

                com.karigar.app.ui.components.FloatingBottomNav(
                    currentRole = currentNavRole,
                    selectedId = selectedNavId,
                    onItemSelected = { id ->
                        selectedNavId = id
                        
                        // Handle Top Bar visibility
                        val topBar = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.composeTopBar)
                        if (id == 4) { // Profile ID
                            topBar?.visibility = android.view.View.GONE
                        } else {
                            topBar?.visibility = android.view.View.VISIBLE
                        }

                        // Load respective fragment
                        when (id) {
                            1 -> loadFragment(HomeFragment())
                            2 -> loadFragment(OrdersFragment())
                            3 -> loadFragment(WalletFragment())
                            4 -> loadFragment(ProfileFragment())
                            5 -> loadFragment(com.karigar.app.ui.worker.WorkerFeedFragment())
                            6 -> loadFragment(com.karigar.app.ui.worker.WorkerActiveJobsFragment())
                        }
                    }
                )
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitAllowingStateLoss()
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Karigar App")
            putExtra(Intent.EXTRA_TEXT, """
                Need a reliable worker? 🛠️
                
                Download Karigar App and find trusted plumbers, electricians, carpenters, and more right at your doorstep!
                
                ✅ Easy booking
                ✅ Reliable & verified service
                ✅ Quality workers you can trust
                
                Get the app now: https://play.google.com/store/apps/details?id=com.karigar.app
            """.trimIndent())
        }
        startActivity(Intent.createChooser(shareIntent, "Share Karigar App via"))
    }
}
