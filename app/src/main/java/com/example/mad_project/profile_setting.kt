package com.example.mad_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class profile_setting : AppCompatActivity() {

    private var selectedPhotoUri: Uri? = null

    // Gallery picker launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            val imgProfile = findViewById<ImageView>(R.id.imgProfile)
            imgProfile.setImageURI(uri)

            // Save URI persistently
            UserProfileManager.savePhotoUri(this, uri.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etName    = findViewById<EditText>(R.id.et_name)
        val etEmail   = findViewById<EditText>(R.id.et_email)
        val etContact = findViewById<EditText>(R.id.et_contact)
        val etCity    = findViewById<EditText>(R.id.etName)       // city field
        val rgGender  = findViewById<RadioGroup>(R.id.rg_gender)
        val rbMale    = findViewById<RadioButton>(R.id.rb_male)
        val rbFemale  = findViewById<RadioButton>(R.id.rb_female)
        val rbOther   = findViewById<RadioButton>(R.id.rb_other)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val btnEdit   = findViewById<ImageView>(R.id.btnEdit)
        val btnBack   = findViewById<ImageView>(R.id.btnBack)
        val btnSave   = findViewById<Button>(R.id.btnSave)

        // ── Load existing saved data ───────────────────────
        etName.setText(UserProfileManager.getName(this))
        etEmail.setText(UserProfileManager.getEmail(this))
        etContact.setText(UserProfileManager.getContact(this))
        etCity.setText(UserProfileManager.getCity(this))

        val savedGender = UserProfileManager.getGender(this)
        when (savedGender) {
            "Female" -> rbFemale.isChecked = true
            "Other"  -> rbOther.isChecked  = true
            else     -> rbMale.isChecked   = true
        }

        // Load profile photo if previously saved
        val savedPhotoUri = UserProfileManager.getPhotoUri(this)
        if (!savedPhotoUri.isNullOrEmpty()) {
            try {
                imgProfile.setImageURI(Uri.parse(savedPhotoUri))
            } catch (e: Exception) {
                imgProfile.setImageResource(R.drawable.emp_profile)
            }
        }

        // ── Listeners ───────────────────────────────────────
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Tap profile photo or pencil icon → open gallery
        imgProfile.setOnClickListener { galleryLauncher.launch("image/*") }
        btnEdit.setOnClickListener   { galleryLauncher.launch("image/*") }

        // Save Changes
        btnSave.setOnClickListener {
            val name    = etName.text.toString().trim()
            val email   = etEmail.text.toString().trim()
            val contact = etContact.text.toString().trim()
            val city    = etCity.text.toString().trim()

            // Basic validation
            if (name.isEmpty()) {
                etName.error = "Name cannot be empty"
                etName.requestFocus()
                return@setOnClickListener
            }

            // Determine selected gender
            val gender = when (rgGender.checkedRadioButtonId) {
                R.id.rb_female -> "Female"
                R.id.rb_other  -> "Other"
                else           -> "Male"
            }

            // Save everything
            UserProfileManager.saveAll(this, name, email, contact, city, gender)

            Toast.makeText(this, "✅ Profile updated successfully!", Toast.LENGTH_SHORT).show()

            // Go back – ProfileFragment's onResume will refresh the display
            finish()
        }
    }
}
