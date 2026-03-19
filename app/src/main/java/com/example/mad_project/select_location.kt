package com.example.mad_project

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mad_project.databinding.ActivitySelectLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class Select_location : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivitySelectLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Try getting real location on launch
        checkLocationPermissionAndGetLocation()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val address = binding.etSearchLocation.text.toString().trim()
            if (address.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("address", address)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                getAddressFromCoordinates(location.latitude, location.longitude)
            } else {
                // Mock default if GPS unavailable
                binding.etSearchLocation.setText("Sector F-6/3, Islamabad")
                binding.tvCurrentAddress.text = "Sector F-6/3, Islamabad"
            }
        }
    }

    private fun getAddressFromCoordinates(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.etSearchLocation.setText(address)
                binding.tvCurrentAddress.text = address
            }
        } catch (e: IOException) {
            e.printStackTrace()
            binding.etSearchLocation.setText("Current Location ($lat, $lng)")
            binding.tvCurrentAddress.text = "Current Location ($lat, $lng)"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationPermissionAndGetLocation()
        } else {
            Toast.makeText(this, "Location permission denied. Please enter address manually.", Toast.LENGTH_SHORT).show()
        }
    }
}