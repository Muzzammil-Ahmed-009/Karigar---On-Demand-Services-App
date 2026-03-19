package com.example.mad_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons(view)
    }

    override fun onResume() {
        super.onResume()
        // Called every time user comes back from profile_setting — refresh data
        view?.let { loadProfileData(it) }
    }

    private fun loadProfileData(view: View) {
        val ctx = requireContext()

        // Name & phone from saved data
        val name    = UserProfileManager.getName(ctx)
        val contact = UserProfileManager.getContact(ctx)

        view.findViewById<TextView>(R.id.tvProfileName)?.text =
            if (name.isNotEmpty()) name else "Karigar User"
        view.findViewById<TextView>(R.id.tvProfilePhone)?.text =
            if (contact.isNotEmpty()) contact else "Add phone number"

        // Profile photo
        val photoUri = UserProfileManager.getPhotoUri(ctx)
        val ivPhoto  = view.findViewById<ImageView>(R.id.ivProfilePhoto)
        if (!photoUri.isNullOrEmpty()) {
            try {
                ivPhoto?.setImageURI(Uri.parse(photoUri))
            } catch (e: Exception) {
                ivPhoto?.setImageResource(R.drawable.emp_profile)
            }
        } else {
            ivPhoto?.setImageResource(R.drawable.emp_profile)
        }

        // Order stats
        view.findViewById<TextView>(R.id.tvTotalOrders)?.text     = AppRepository.orders.size.toString()
        view.findViewById<TextView>(R.id.tvCompletedOrders)?.text =
            AppRepository.getHistoryOrders().count { it.status == "Completed" }.toString()
        view.findViewById<TextView>(R.id.tvPendingOrders)?.text   =
            AppRepository.getPendingOrders().size.toString()
    }

    private fun setupButtons(view: View) {
        view.findViewById<LinearLayout>(R.id.btnEditProfile)?.setOnClickListener {
            startActivity(Intent(requireContext(), profile_setting::class.java))
        }

        view.findViewById<LinearLayout>(R.id.btnContactUs)?.setOnClickListener {
            startActivity(Intent(requireContext(), contact_us::class.java))
        }

        view.findViewById<LinearLayout>(R.id.btnFavoriteWorkers)?.setOnClickListener {
            startActivity(Intent(requireContext(), FavoriteWorkersActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.btnLogoutProfile)?.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        val dialog = android.app.AlertDialog.Builder(requireContext()).create()
        val dialogView = layoutInflater.inflate(R.layout.logout_dialog, null)
        dialog.setView(dialogView)
        dialogView.findViewById<android.widget.Button>(R.id.btnlogout)?.setOnClickListener {
            startActivity(Intent(requireContext(), OnboardingActivity::class.java))
            requireActivity().finish()
        }
        dialogView.findViewById<android.widget.Button>(R.id.btn_Cancel)?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
