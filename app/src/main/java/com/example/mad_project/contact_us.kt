package com.example.mad_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mad_project.databinding.ActivityContactUsBinding
import com.example.mad_project.databinding.ActivityOtpVerificationBinding

class contact_us : AppCompatActivity() {

    private val binding: ActivityContactUsBinding by lazy {
        ActivityContactUsBinding.inflate(layoutInflater)
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


        binding.phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel: 03182118652")
            startActivity(intent)
        }

        binding.email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto: muzammilrazzaq009@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, main_page::class.java)
            startActivity(intent)
        }





    }
}