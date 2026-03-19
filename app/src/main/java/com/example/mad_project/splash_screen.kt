package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mad_project.databinding.ActivityMainBinding
import com.example.mad_project.databinding.ActivitySplashScreenBinding

class splash_screen : AppCompatActivity() {

    private val binding: ActivitySplashScreenBinding by lazy {
        ActivitySplashScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)



    }
}