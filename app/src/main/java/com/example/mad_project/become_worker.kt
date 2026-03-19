package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class become_worker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_become_worker)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.become_worker)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerButton = findViewById<Button>(R.id.btnRegister)
        registerButton.setOnClickListener {
            val intent = Intent(this, worker_form::class.java)
            startActivity(intent)

        }

        val crossbtn = findViewById<ImageButton>(R.id.cross)
        crossbtn.setOnClickListener {
            val intent = Intent(this, main_page::class.java)
            startActivity(intent)

        }



    }
}


