package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class worker_form : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_worker_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.worker_form)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val etWorkType = findViewById<TextInputEditText>(R.id.etWorkType)

        etWorkType.setOnClickListener {
            showWorkTypeBottomSheet()
        }

        val btnback = findViewById<ImageView>(R.id.btnBack)

        btnback.setOnClickListener {
            startActivity(Intent(this, become_worker::class.java))

        }




        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etName = findViewById<EditText>(R.id.etName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etExperience = findViewById<EditText>(R.id.etExperience)


        btnSubmit.setOnClickListener {
                val name = etName.text.toString()
                val phone = etPhone.text.toString()
                val work = etWorkType.text.toString()
                val email = etEmail.text.toString()
                val experience = etExperience.text.toString()


                when {
                    name.isEmpty() -> {
                        etName.error = "Name is required"
                        etName.requestFocus()
                    }
                    phone.isEmpty() -> {
                        etPhone.error = "Phone number is required"
                        etPhone.requestFocus()
                    }
                    work.isEmpty() || work == "None Selected" -> {
                        etWorkType.error = "Please select work type"
                        Toast.makeText(this, "Please select work type from bottom sheet", Toast.LENGTH_SHORT).show()
                    }
                    email.isEmpty() -> {
                        etEmail.error = "Email is required"
                        etEmail.requestFocus()
                    }
                    experience.isEmpty() -> {
                        etExperience.error = "Experience is required"
                        etExperience.requestFocus()
                    }
                    else -> {
                        showRegistrationSuccessDialog()
                    }
                }

        }



    }

    private fun showWorkTypeBottomSheet() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_worktype, null)
        dialog.setContentView(view)

        // Checkboxes ko find karein
        val cbPlumber = view.findViewById<CheckBox>(R.id.cbPlumber)
        val cbCarpenter = view.findViewById<CheckBox>(R.id.carpanter)
        val cbCleaning = view.findViewById<CheckBox>(R.id.cleaning)
        val cbElectrician = view.findViewById<CheckBox>(R.id.Electician)
        val btnOk = view.findViewById<Button>(R.id.btnSheetOk)

        btnOk.setOnClickListener {
            val selectedWorkTypes = mutableListOf<String>()

            if (cbPlumber.isChecked) selectedWorkTypes.add(cbPlumber.text.toString().trim())
            if (cbCarpenter.isChecked) selectedWorkTypes.add(cbCarpenter.text.toString().trim())
            if (cbCleaning.isChecked) selectedWorkTypes.add(cbCleaning.text.toString().trim())
            if (cbElectrician.isChecked) selectedWorkTypes.add(cbElectrician.text.toString().trim())

            // Error Handling
            if (selectedWorkTypes.isNotEmpty()) {
                // Tamam selected items ko comma (,) se join karein
                val resultText = selectedWorkTypes.joinToString(", ")

                // Apne EditText mein set karein
                findViewById<EditText>(R.id.etWorkType).setText(resultText)

                dialog.dismiss()
            } else {

                Toast.makeText(this, "Please select at least one option", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }



    private fun showRegistrationSuccessDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.registration_success_dialog, null)
        dialog.setContentView(view)
        dialog.show()

        val btnOk = view.findViewById<Button>(R.id.btnOk)
        btnOk.setOnClickListener {
            startActivity(Intent(this, main_page::class.java))
        }

    }



}