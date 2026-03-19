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

class CompanyRegistrationFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_company_registration_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.company_registration_form)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etWorkerType = findViewById<TextInputEditText>(R.id.etWorkerType)

        etWorkerType.setOnClickListener {
            showWorkerTypeBottomSheet()
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            startActivity(Intent(this, RegisterCompanyIntroActivity::class.java))
        }

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etCompanyName = findViewById<EditText>(R.id.etCompanyName)
        val etSupervisorName = findViewById<EditText>(R.id.etSupervisorName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etAddress = findViewById<EditText>(R.id.etAddress)

        btnSubmit.setOnClickListener {
            val companyName = etCompanyName.text.toString()
            val supervisorName = etSupervisorName.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val workerType = etWorkerType.text.toString()
            val address = etAddress.text.toString()

            when {
                companyName.isEmpty() -> {
                    etCompanyName.error = "Company name is required"
                    etCompanyName.requestFocus()
                }
                supervisorName.isEmpty() -> {
                    etSupervisorName.error = "Supervisor name is required"
                    etSupervisorName.requestFocus()
                }
                phone.isEmpty() -> {
                    etPhone.error = "Phone number is required"
                    etPhone.requestFocus()
                }
                email.isEmpty() -> {
                    etEmail.error = "Email is required"
                    etEmail.requestFocus()
                }
                workerType.isEmpty() || workerType == "None Selected" -> {
                    etWorkerType.error = "Please select worker type"
                    Toast.makeText(this, "Please select worker type from bottom sheet", Toast.LENGTH_SHORT).show()
                }
                address.isEmpty() -> {
                    etAddress.error = "Address is required"
                    etAddress.requestFocus()
                }
                else -> {
                    showRegistrationSuccessDialog()
                }
            }
        }
    }

    private fun showWorkerTypeBottomSheet() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_worktype, null)
        dialog.setContentView(view)

        // Find checkboxes
        val cbPlumber = view.findViewById<CheckBox>(R.id.cbPlumber)
        val cbCarpenter = view.findViewById<CheckBox>(R.id.carpanter)
        val cbCleaning = view.findViewById<CheckBox>(R.id.cleaning)
        val cbElectrician = view.findViewById<CheckBox>(R.id.Electician)
        val btnOk = view.findViewById<Button>(R.id.btnSheetOk)

        btnOk.setOnClickListener {
            val selectedWorkerTypes = mutableListOf<String>()

            if (cbPlumber.isChecked) selectedWorkerTypes.add(cbPlumber.text.toString().trim())
            if (cbCarpenter.isChecked) selectedWorkerTypes.add(cbCarpenter.text.toString().trim())
            if (cbCleaning.isChecked) selectedWorkerTypes.add(cbCleaning.text.toString().trim())
            if (cbElectrician.isChecked) selectedWorkerTypes.add(cbElectrician.text.toString().trim())

            if (selectedWorkerTypes.isNotEmpty()) {
                // Join selected items with comma
                val resultText = selectedWorkerTypes.joinToString(", ")

                // Set in EditText
                findViewById<EditText>(R.id.etWorkerType).setText(resultText)

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
