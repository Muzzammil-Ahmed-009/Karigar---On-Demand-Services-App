package com.karigar.app.ui.services

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
import com.karigar.app.databinding.ActivitySelectLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

import android.view.inputmethod.InputMethodManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.karigar.app.BuildConfig

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivitySelectLocationBinding
    private var googleMap: GoogleMap? = null
    
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteAdapter: PlacesAutocompleteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }
        placesClient = Places.createClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupSearchRecyclerView()
        setupSearchListeners()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val address = binding.tvCurrentAddress.text.toString().trim()
            if (address.isNotEmpty() && address != "Fetching your current location..." && currentLat != 0.0) {
                val resultIntent = Intent()
                resultIntent.putExtra("address", address)
                resultIntent.putExtra("lat", currentLat)
                resultIntent.putExtra("lng", currentLng)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearchRecyclerView() {
        autocompleteAdapter = PlacesAutocompleteAdapter { prediction ->
            hideKeyboard()
            hideSearchResults()
            isProgrammaticTextChange = true
            binding.etSearchLocation.setText(prediction.getPrimaryText(null).toString())
            isProgrammaticTextChange = false
            binding.etSearchLocation.clearFocus()
            
            val placeId = prediction.placeId
            val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                place.latLng?.let { latLng ->
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to get location details", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = autocompleteAdapter
    }

    private var isProgrammaticTextChange = false

    private fun setupSearchListeners() {
        binding.etSearchLocation.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showSearchResults()
                val query = binding.etSearchLocation.text.toString()
                if (query.isNotEmpty() && !isProgrammaticTextChange) {
                    performSearch(query)
                }
            }
        }

        binding.etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isProgrammaticTextChange) return
                
                val query = s.toString()
                if (query.isNotEmpty()) {
                    binding.btnClearSearch.visibility = View.VISIBLE
                    showSearchResults()
                    performSearch(query)
                } else {
                    binding.btnClearSearch.visibility = View.GONE
                    autocompleteAdapter.setPredictions(emptyList())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearchLocation.text.clear()
            binding.btnClearSearch.visibility = View.GONE
            autocompleteAdapter.setPredictions(emptyList())
        }

        binding.dimOverlay.setOnClickListener {
            hideKeyboard()
            hideSearchResults()
            binding.etSearchLocation.clearFocus()
        }
    }

    private fun performSearch(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setCountry("PK") // Restrict to Pakistan
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            autocompleteAdapter.setPredictions(response.autocompletePredictions)
        }.addOnFailureListener {
            autocompleteAdapter.setPredictions(emptyList())
        }
    }

    private fun showSearchResults() {
        binding.dimOverlay.visibility = View.VISIBLE
        binding.rvSearchResults.visibility = View.VISIBLE
        binding.bottomCard.visibility = View.GONE
    }

    private fun hideSearchResults() {
        binding.dimOverlay.visibility = View.GONE
        binding.rvSearchResults.visibility = View.GONE
        binding.bottomCard.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Add padding: left, top (for search bar), right, bottom (for bottom sheet)
        googleMap?.setPadding(0, 350, 0, 450)
        
        googleMap?.setOnCameraIdleListener {
            val center = googleMap?.cameraPosition?.target
            if (center != null) {
                currentLat = center.latitude
                currentLng = center.longitude
                getAddressFromCoordinates(center.latitude, center.longitude)
            }
        }

        checkLocationPermissionAndGetLocation()
    }

    private fun checkLocationPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        googleMap?.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } else {
                // Default to Islamabad
                val defaultLocation = LatLng(33.7294, 73.0931)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationPermissionAndGetLocation()
        } else {
            Toast.makeText(this, "Location permission is required to fetch current location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddressFromCoordinates(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            isProgrammaticTextChange = true
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.etSearchLocation.setText(address)
                binding.tvCurrentAddress.text = address
            } else {
                binding.etSearchLocation.setText("Unknown Location")
                binding.tvCurrentAddress.text = "Unknown Location"
            }
            isProgrammaticTextChange = false
        } catch (e: IOException) {
            e.printStackTrace()
            isProgrammaticTextChange = true
            binding.etSearchLocation.setText("Current Location ($lat, $lng)")
            isProgrammaticTextChange = false
            binding.tvCurrentAddress.text = "Current Location ($lat, $lng)"
        }
    }


}
