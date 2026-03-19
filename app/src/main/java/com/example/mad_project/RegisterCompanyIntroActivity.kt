package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterCompanyIntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_company_intro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_company_intro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerButton = findViewById<Button>(R.id.btnRegister)
        registerButton.setOnClickListener {
            val intent = Intent(this, CompanyRegistrationFormActivity::class.java)
            startActivity(intent)
        }

        val crossBtn = findViewById<ImageButton>(R.id.cross)
        crossBtn.setOnClickListener {
            val intent = Intent(this, main_page::class.java)
            startActivity(intent)
        }
    }
}
