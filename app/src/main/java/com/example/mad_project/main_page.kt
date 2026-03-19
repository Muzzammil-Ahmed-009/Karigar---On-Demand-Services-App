package com.example.mad_project

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

class main_page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_page)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ── DRAWER LISTENERS ──────────────────────────────
        drawerLayout.let { drawer ->
            // Close drawer on cross icon
            findViewById<ImageView>(R.id.cross)?.setOnClickListener {
                drawer.closeDrawers()
            }

            findViewById<android.widget.TextView>(R.id.tvContactus)?.setOnClickListener {
                startActivity(Intent(this, contact_us::class.java))
                drawer.closeDrawers()
            }

            findViewById<android.widget.TextView>(R.id.worker)?.setOnClickListener {
                startActivity(Intent(this, become_worker::class.java))
                drawer.closeDrawers()
            }

            findViewById<android.widget.TextView>(R.id.tvRegisterCompany)?.setOnClickListener {
                startActivity(Intent(this, RegisterCompanyIntroActivity::class.java))
                drawer.closeDrawers()
            }

            findViewById<android.widget.TextView>(R.id.tvSettings)?.setOnClickListener {
                startActivity(Intent(this, SettingsActivity::class.java))
                drawer.closeDrawers()
            }

            findViewById<android.widget.TextView>(R.id.tvShare)?.setOnClickListener {
                shareApp()
                drawer.closeDrawers()
            }

            findViewById<android.widget.TextView>(R.id.nav_rateus)?.setOnClickListener {
                showRateUsDialog()
                drawer.closeDrawers()
            }

            findViewById<android.widget.TextView>(R.id.nav_logout)?.setOnClickListener {
                showLogoutDialog()
                drawer.closeDrawers()
            }
        }

        // ── INITIAL FRAGMENT ─────────────────────────────
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // ── TOP BAR: Hamburger menu ──────────────────────
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ── TOP BAR: Bell icon → Notifications ──────────
        findViewById<ImageView>(R.id.btnNotifications)?.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // ── BOTTOM NAV: Home | Orders | Wallet | Profile ─
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home    -> loadFragment(HomeFragment())
                R.id.nav_orders  -> loadFragment(OrdersFragment())
                R.id.nav_wallet  -> loadFragment(WalletFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showRateUsDialog() {
        val dialog = android.app.AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.dialog_rate_us, null)
        dialog.setView(view)
        view.findViewById<Button>(R.id.btnSubmit)?.setOnClickListener {
            Toast.makeText(this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.btnCancel)?.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showLogoutDialog() {
        val dialog = android.app.AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.logout_dialog, null)
        dialog.setView(view)
        view.findViewById<Button>(R.id.btnlogout)?.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }
        view.findViewById<Button>(R.id.btn_Cancel)?.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Karigar App")
            putExtra(Intent.EXTRA_TEXT, """
                Check out Karigar App! 🛠️
                
                Find skilled workers like painters, electricians, plumbers, carpenters, and more.
                ✅ Easy booking  ✅ Reliable service  ✅ Quality workers
            """.trimIndent())
        }
        startActivity(Intent.createChooser(shareIntent, "Share Karigar App via"))
    }
}