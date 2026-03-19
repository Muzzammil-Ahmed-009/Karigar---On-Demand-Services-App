package com.example.mad_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts

class HomeFragment : Fragment() {
    
    private var waitingForNewCategory = false
    
    // Activity Result Launcher for Place Order
    private val placeOrderLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val requestNewCategory = result.data?.getBooleanExtra("request_new_category", false) ?: false
            if (requestNewCategory) {
                // User wants to add another category, set flag
                waitingForNewCategory = true
            }
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)





        // Set up click listeners for all service categories
        view.findViewById<ImageView>(R.id.cleaning)?.setOnClickListener {
            navigateToPlaceOrder("Cleaning")
        }

        view.findViewById<ImageView>(R.id.repairing1)?.setOnClickListener {
            navigateToPlaceOrder("Repairing")
        }

        view.findViewById<ImageView>(R.id.carpenter1)?.setOnClickListener {
            navigateToPlaceOrder("Carpenter")
        }

        view.findViewById<ImageView>(R.id.electrician1)?.setOnClickListener {
            navigateToPlaceOrder("Electrician")
        }

        view.findViewById<ImageView>(R.id.plumbing1)?.setOnClickListener {
            navigateToPlaceOrder("Plumbing")
        }

        view.findViewById<ImageView>(R.id.painting1)?.setOnClickListener {
            navigateToPlaceOrder("Painting")
        }

        view.findViewById<ImageView>(R.id.shifting1)?.setOnClickListener {
            navigateToPlaceOrder("Shifting")
        }

        view.findViewById<ImageView>(R.id.ac_repair1)?.setOnClickListener {
            navigateToPlaceOrder("AC Repair")
        }

        view.findViewById<ImageView>(R.id.security1)?.setOnClickListener {
            navigateToPlaceOrder("Security")
        }

        view.findViewById<ImageView>(R.id.gardening1)?.setOnClickListener {
            navigateToPlaceOrder("Gardening")
        }

        view.findViewById<androidx.cardview.widget.CardView>(R.id.cardMore)?.setOnClickListener {
            val intent = android.content.Intent(requireContext(), AllServicesActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun navigateToPlaceOrder(categoryName: String) {
        val intent = Intent(requireContext(), place_order::class.java)
        intent.putExtra("category_name", categoryName)
        
        // If we're adding a new category to existing order, set flag
        if (waitingForNewCategory) {
            intent.putExtra("adding_category", true)
            waitingForNewCategory = false
        }
        
        placeOrderLauncher.launch(intent)
    }
}

