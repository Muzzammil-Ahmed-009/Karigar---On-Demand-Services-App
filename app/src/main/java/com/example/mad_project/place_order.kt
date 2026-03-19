package com.example.mad_project

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.mad_project.databinding.ActivityPlaceOrderBinding
import com.example.mad_project.model.Order
import java.util.UUID
import kotlinx.parcelize.Parcelize

// Data class to represent a service category
@Parcelize
data class Category(
    val name: String,
    val iconRes: Int
) : Parcelable

// Data class to store order state temporarily
data class OrderState(
    val categories: ArrayList<Category>,
    val photos: ArrayList<Uri>,
    val address: String?,
    val details: String?
)

class place_order : AppCompatActivity() {

    companion object {
        private var savedOrderState: OrderState? = null
        fun hasSavedState(): Boolean = savedOrderState != null
        fun getSavedState(): OrderState? = savedOrderState
        fun clearSavedState() { savedOrderState = null }
        fun saveState(state: OrderState) { savedOrderState = state }
    }

    private lateinit var binding: ActivityPlaceOrderBinding

    private val allPhotos = ArrayList<Uri>()
    private val selectedCategories = ArrayList<Category>()
    
    // --- STEPPER STATE ---
    private var currentStep = 1
    private val totalSteps = 3

    // Activity Result Launchers
    private val locationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val address = result.data?.getStringExtra("address")
            if (address != null) {
                binding.btnAddAddress.text = address
                validateCurrentStep()
            }
        }
    }

    private val detailsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val details = result.data?.getStringExtra("order_details")
            if (details != null) {
                binding.btnAddDetails.text = details
                validateCurrentStep()
            }
        }
    }

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photoList = result.data?.getParcelableArrayListExtra<Uri>("all_photos")
            if (photoList != null && photoList.isNotEmpty()) {
                allPhotos.clear()
                allPhotos.addAll(photoList)
                updatePhotoDisplay()
                validateCurrentStep()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- RESTORE PREVIOUS STATE IF EXISTS ---
        if (hasSavedState()) {
            val savedState = getSavedState()
            if (savedState != null) {
                selectedCategories.clear()
                selectedCategories.addAll(savedState.categories)
                updateCategoryDisplay()
                
                allPhotos.clear()
                allPhotos.addAll(savedState.photos)
                if (allPhotos.isNotEmpty()) {
                    updatePhotoDisplay()
                }
                
                if (savedState.address != null) {
                    binding.btnAddAddress.text = savedState.address
                }
                
                if (savedState.details != null) {
                    binding.btnAddDetails.text = savedState.details
                }
                
                updateStepUI()
            }
            clearSavedState()
        }
        
        // --- CLICK LISTENERS ---

        val initialCategory = intent.getStringExtra("category_name")
        if (initialCategory != null) {
            addCategory(initialCategory)
        }
        
        binding.btnBack.setOnClickListener {
            handleBackPress()
        }

        binding.btnAddAddress.setOnClickListener {
            val intent = Intent(this, Select_location::class.java)
            locationLauncher.launch(intent)
        }

        binding.btnAddDetails.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            detailsLauncher.launch(intent)
        }

        binding.btnAddPhotos.setOnClickListener {
            val intent = Intent(this, TakephotoActivity::class.java)
            intent.putParcelableArrayListExtra("existing_photos", allPhotos)
            photoLauncher.launch(intent)
        }
        
        binding.btnAddCategory.setOnClickListener {
            val currentAddress = binding.btnAddAddress.text.toString()
            val currentDetails = binding.btnAddDetails.text.toString()
            
            val orderState = OrderState(
                categories = ArrayList(selectedCategories),
                photos = ArrayList(allPhotos),
                address = if (currentAddress != "Tap to set your address") currentAddress else null,
                details = if (currentDetails != "Describe what needs to be done...") currentDetails else null
            )
            saveState(orderState)
            
            val resultIntent = Intent()
            resultIntent.putExtra("request_new_category", true)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        
        binding.btnAddMorePhotos.setOnClickListener {
            val intent = Intent(this, TakephotoActivity::class.java)
            intent.putParcelableArrayListExtra("existing_photos", allPhotos)
            photoLauncher.launch(intent)
        }

        binding.btnNextStep.setOnClickListener {
            if (currentStep < totalSteps) {
                currentStep++
                updateStepUI()
            } else {
                showConfirmationDialog()
            }
        }

        updateStepUI()
    }

    override fun onBackPressed() {
        handleBackPress()
    }

    private fun handleBackPress() {
        if (currentStep > 1) {
            currentStep--
            updateStepUI()
        } else {
            super.onBackPressed()
        }
    }

    // --- STEPPER UI LOGIC ---
    private fun updateStepUI() {
        // Update texts and progress bar
        binding.tvStepIndicator.text = "Step $currentStep of $totalSteps"
        binding.progressBar.progress = (currentStep * 100) / totalSteps

        // Toggle visibility of step layouts
        binding.layoutStep1.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        binding.layoutStep2.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        binding.layoutStep3.visibility = if (currentStep == 3) View.VISIBLE else View.GONE

        // Smooth scroll to top
        binding.scrollContent.smoothScrollTo(0, 0)

        // Validate the newly visible step
        validateCurrentStep()
    }

    private fun validateCurrentStep() {
        var isValid = false
        var buttonText = "Next Step →"

        when (currentStep) {
            1 -> {
                isValid = selectedCategories.isNotEmpty()
                buttonText = if (isValid) "Next Step →" else "Select a category"
            }
            2 -> {
                val currentAddress = binding.btnAddAddress.text.toString()
                val currentDetails = binding.btnAddDetails.text.toString()
                val isAddressValid = currentAddress != "Tap to set your address" && currentAddress.isNotEmpty()
                val isDetailsValid = currentDetails != "Describe what needs to be done..." && currentDetails.isNotEmpty()
                
                isValid = isAddressValid && isDetailsValid
                buttonText = if (isValid) "Next Step →" else "Complete details to continue"
            }
            3 -> {
                isValid = true
                buttonText = "Confirm Order"
            }
        }

        enableNextButton(isValid, buttonText)
    }

    private fun enableNextButton(isEnabled: Boolean, text: String) {
        binding.btnNextStep.isEnabled = isEnabled
        binding.btnNextStep.text = text
        if (isEnabled) {
            binding.btnNextStep.setTextColor(Color.parseColor("#FFFFFF"))
            binding.btnNextStep.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#0F2CBD"))
        } else {
            binding.btnNextStep.setTextColor(Color.parseColor("#999999"))
            binding.btnNextStep.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
        }
    }

    // --- HELPER METHODS ---

    private fun updatePhotoDisplay() {
        val plusButton = binding.btnAddMorePhotos
        binding.layoutPhotoList.removeAllViews()

        val sizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88f, resources.displayMetrics).toInt()
        val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
        val cornerPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, resources.displayMetrics)

        for (uri in allPhotos) {
            val cardView = androidx.cardview.widget.CardView(this)
            val cardParams = LinearLayout.LayoutParams(sizePx, sizePx)
            cardParams.setMargins(0, 0, marginPx, 0)
            cardView.layoutParams = cardParams
            cardView.radius = cornerPx
            cardView.cardElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
            cardView.preventCornerOverlap = true
            cardView.useCompatPadding = false

            val imageView = ImageView(this)
            imageView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageURI(uri)
            cardView.addView(imageView)

            binding.layoutPhotoList.addView(cardView)
        }

        binding.layoutPhotoList.addView(plusButton)
        binding.btnAddPhotos.text = "${allPhotos.size} Photo${if (allPhotos.size == 1) "" else "s"} Added ✓"
    }

    private fun getCategoryIcon(categoryName: String): Int {
        return when (categoryName.lowercase()) {
            "cleaning" -> R.drawable.cleaning
            "repairing" -> R.drawable.repairing
            "carpenter", "carpanter" -> R.drawable.carpenter
            "electrician" -> R.drawable.electrician
            "plumbing" -> R.drawable.plumber
            "painting" -> R.drawable.painter
            "shifting" -> R.drawable.shifting
            "ac repair", "ac_repair" -> R.drawable.ac_repair
            "security" -> R.drawable.security
            "gardening" -> R.drawable.gardener
            else -> R.drawable.cleaning
        }
    }

    private fun addCategory(categoryName: String) {
        if (selectedCategories.any { it.name.equals(categoryName, ignoreCase = true) }) {
            Toast.makeText(this, "$categoryName already added", Toast.LENGTH_SHORT).show()
            return
        }
        val iconRes = getCategoryIcon(categoryName)
        val category = Category(categoryName, iconRes)
        selectedCategories.add(category)
        updateCategoryDisplay()
        validateCurrentStep()
    }

    private fun updateCategoryDisplay() {
        binding.layoutCategoryCards.removeAllViews()
        for (category in selectedCategories) {
            val categoryCard = createCategoryCard(category)
            binding.layoutCategoryCards.addView(categoryCard)
        }
        val plusCard = createPlusCard()
        binding.layoutCategoryCards.addView(plusCard)
    }

    private fun createCategoryCard(category: Category): CardView {
        val cardView = CardView(this)
        val params = LinearLayout.LayoutParams(200, 200)
        params.setMargins(0, 0, 30, 0)
        cardView.layoutParams = params
        cardView.radius = 30f
        cardView.cardElevation = 4f
        
        val frameLayout = FrameLayout(this)
        frameLayout.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        
        val iconView = ImageView(this)
        val iconParams = FrameLayout.LayoutParams(100, 100)
        iconParams.gravity = android.view.Gravity.CENTER
        iconView.layoutParams = iconParams
        iconView.setImageResource(category.iconRes)
        frameLayout.addView(iconView)
        
        val removeBtn = ImageView(this)
        val removeParams = FrameLayout.LayoutParams(40, 35)
        removeParams.gravity = android.view.Gravity.TOP or android.view.Gravity.END
        removeParams.setMargins(10, 10, 10, 10)
        removeBtn.layoutParams = removeParams
        removeBtn.setImageResource(R.drawable.cross)
        removeBtn.setOnClickListener {
            selectedCategories.remove(category)
            updateCategoryDisplay()
            validateCurrentStep()
            Toast.makeText(this, "${category.name} removed", Toast.LENGTH_SHORT).show()
        }
        frameLayout.addView(removeBtn)
        cardView.addView(frameLayout)
        return cardView
    }

    private fun createPlusCard(): CardView {
        val cardView = CardView(this)
        val params = LinearLayout.LayoutParams(200, 200)
        params.setMargins(0, 0, 30, 0)
        cardView.layoutParams = params
        cardView.radius = 30f
        cardView.setCardBackgroundColor(Color.WHITE)
        cardView.cardElevation = 0f
        
        val plusIcon = ImageView(this)
        val iconParams = FrameLayout.LayoutParams(75, 75)
        iconParams.gravity = android.view.Gravity.CENTER
        plusIcon.layoutParams = iconParams
        plusIcon.setImageResource(R.drawable.plus)
        plusIcon.alpha = 0.3f
        
        cardView.addView(plusIcon)
        cardView.setOnClickListener {
            val currentAddress = binding.btnAddAddress.text.toString()
            val currentDetails = binding.btnAddDetails.text.toString()
            val orderState = OrderState(
                categories = ArrayList(selectedCategories),
                photos = ArrayList(allPhotos),
                address = if (currentAddress != "Tap to set your address") currentAddress else null,
                details = if (currentDetails != "Describe what needs to be done...") currentDetails else null
            )
            saveState(orderState)
            val resultIntent = Intent()
            resultIntent.putExtra("request_new_category", true)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        return cardView
    }

    private fun showConfirmationDialog() {
        val offeredFareStr = binding.etOfferedFare.text.toString()
        if (offeredFareStr.isEmpty()) {
            Toast.makeText(this, "Please enter an Offered Fare", Toast.LENGTH_SHORT).show()
            binding.etOfferedFare.requestFocus()
            return
        }
        
        val dialog = android.app.AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.dialog_confirm_order, null)
        dialog.setView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnPlaceOrder = view.findViewById<Button>(R.id.btnFinalPlaceOrder)

        btnPlaceOrder.setOnClickListener {
            dialog.dismiss()

            val categoryNames = selectedCategories.map { it.name }
            val address = binding.btnAddAddress.text.toString()
            val details = binding.btnAddDetails.text.toString()

            val order = Order(
                id = UUID.randomUUID().toString(),
                categories = categoryNames,
                address = address,
                details = details,
                photoCount = allPhotos.size,
                status = "Pending",
                timestamp = System.currentTimeMillis(),
                offeredFare = offeredFareStr
            )

            AppRepository.placeOrder(order)

            val intent = Intent(this, main_page::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        dialog.show()
    }
}