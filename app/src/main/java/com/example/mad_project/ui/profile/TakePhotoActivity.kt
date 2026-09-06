package com.karigar.app.ui.profile

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


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.karigar.app.databinding.ActivityTakePhotoBinding

class TakePhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTakePhotoBinding
    private var isImageSelected = false
    private val imageList = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Receive existing photos from the intent
        val existingPhotos = intent.getParcelableArrayListExtra<Uri>("existing_photos")
        if (existingPhotos != null && existingPhotos.isNotEmpty()) {
            imageList.addAll(existingPhotos)
            // Show the first existing photo in preview
            binding.imgPreview.setImageURI(existingPhotos[0])
            isImageSelected = true
            binding.layoutNoPhoto.visibility = android.view.View.GONE
        }

        binding.btnBack.setOnClickListener { finish() }

        // Gallery se multiple images select karne ka launcher
        val getGalleryImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                if (imageList.size < 5) { // Maximum 5 photos limit
                    imageList.add(it)
                    binding.imgPreview.setImageURI(it)
                    isImageSelected = true
                    binding.layoutNoPhoto.visibility = android.view.View.GONE
                    Toast.makeText(this, "${imageList.size} photo(s) selected", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Maximum 5 photos allowed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Camera se photo lene ka launcher
        val getCameraImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as Bitmap
                binding.imgPreview.setImageBitmap(bitmap)
                isImageSelected = true
                binding.layoutNoPhoto.visibility = android.view.View.GONE
            }
        }

        binding.btnGallery.setOnClickListener { getGalleryImage.launch("image/*") }

        binding.btnCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getCameraImage.launch(cameraIntent)
        }

        // Save Button - pass images back to PlaceOrderActivity
        binding.btnSavePhoto.setOnClickListener {
            if (isImageSelected && imageList.isNotEmpty()) {
                // Use setResult to pass data back
                val resultIntent = Intent()
                resultIntent.putParcelableArrayListExtra("all_photos", imageList)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please select at least one photo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
