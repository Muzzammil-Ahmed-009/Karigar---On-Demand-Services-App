package com.karigar.app.ui.services

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.R
import com.karigar.app.data.model.Order
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.main.MainActivity
import com.karigar.app.ui.orders.OrderViewModel
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextSecondary
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import java.util.UUID

@Parcelize
data class Category(
    val name: String,
    val iconRes: Int
) : Parcelable

data class OrderState(
    val categories: ArrayList<Category>,
    val photos: ArrayList<Uri>,
    val address: String?,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val details: String?,
    val currentCameraUri: Uri? = null
)

@AndroidEntryPoint
class PlaceOrderActivity : ComponentActivity() {

    private val orderViewModel: OrderViewModel by viewModels()

    companion object {
        private var savedOrderState: OrderState? = null
        fun hasSavedState(): Boolean = savedOrderState != null
        fun getSavedState(): OrderState? = savedOrderState
        fun clearSavedState() { savedOrderState = null }
        fun saveState(state: OrderState) { savedOrderState = state }
    }

    private val allPhotos = mutableStateListOf<Uri>()
    private val selectedCategories = mutableStateListOf<Category>()
    
    private var currentAddress by mutableStateOf<String?>(null)
    private var currentDetails by mutableStateOf<String?>(null)
    private var currentLat = 0.0
    private var currentLng = 0.0
    private var currentCameraUri by mutableStateOf<Uri?>(null)

    private val locationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val address = result.data?.getStringExtra("address")
            if (address != null) {
                currentAddress = address
                currentLat = result.data?.getDoubleExtra("lat", 0.0) ?: 0.0
                currentLng = result.data?.getDoubleExtra("lng", 0.0) ?: 0.0
            }
        }
    }

    private val detailsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val details = result.data?.getStringExtra("order_details")
            if (details != null) {
                currentDetails = details
            }
        }
    }

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            allPhotos.addAll(uris)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && currentCameraUri != null) {
            allPhotos.add(currentCameraUri!!)
            currentCameraUri = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (hasSavedState()) {
            val savedState = getSavedState()
            if (savedState != null) {
                selectedCategories.clear()
                selectedCategories.addAll(savedState.categories)
                allPhotos.clear()
                allPhotos.addAll(savedState.photos)
                currentAddress = savedState.address
                currentDetails = savedState.details
                currentLat = savedState.lat
                currentLng = savedState.lng
                currentCameraUri = savedState.currentCameraUri
            }
            clearSavedState()
        }

        val initialCategory = intent.getStringExtra("category_name")
        if (initialCategory != null && selectedCategories.none { it.name.equals(initialCategory, ignoreCase = true) }) {
            selectedCategories.add(Category(initialCategory, getCategoryIcon(initialCategory)))
        }

        setContent {
            KarigarTheme {
                PlaceOrderScreen(
                    categories = selectedCategories,
                    photos = allPhotos,
                    address = currentAddress,
                    details = currentDetails,
                    onBackClick = { finish() },
                    onAddCategory = {
                        saveCurrentState()
                        val resultIntent = Intent()
                        resultIntent.putExtra("request_new_category", true)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    },
                    onRemoveCategory = { selectedCategories.remove(it) },
                    onAddAddress = { locationLauncher.launch(Intent(this, SelectLocationActivity::class.java)) },
                    onAddDetails = { detailsLauncher.launch(Intent(this, DetailsActivity::class.java)) },
                    onAddPhotos = {
                        // Managed in Compose with bottom sheet
                    },
                    onLaunchGallery = { photoLauncher.launch("image/*") },
                    onLaunchCamera = {
                        val file = java.io.File(cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            this@PlaceOrderActivity,
                            "${applicationContext.packageName}.fileprovider",
                            file
                        )
                        currentCameraUri = uri
                        cameraLauncher.launch(uri)
                    },
                    onPlaceOrder = { offeredFare ->
                        val order = Order(
                            id = UUID.randomUUID().toString(),
                            categories = selectedCategories.map { it.name },
                            address = currentAddress ?: "",
                            latitude = currentLat,
                            longitude = currentLng,
                            details = currentDetails ?: "",
                            photoCount = allPhotos.size,
                            status = "Pending",
                            timestamp = System.currentTimeMillis(),
                            offeredFare = offeredFare
                        )
                        orderViewModel.placeOrder(order)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    private fun saveCurrentState() {
        saveState(
            OrderState(
                categories = ArrayList(selectedCategories),
                photos = ArrayList(allPhotos),
                address = currentAddress,
                lat = currentLat,
                lng = currentLng,
                details = currentDetails,
                currentCameraUri = currentCameraUri
            )
        )
    }

    private fun getCategoryIcon(categoryName: String): Int {
        return when (categoryName.lowercase()) {
            "cleaning" -> R.drawable.cleaning
            "repairing" -> R.drawable.repairing
            "carpenter", "carpanter" -> R.drawable.carpenter
            "electrician" -> R.drawable.electrician
            "plumbing", "plumber" -> R.drawable.plumber
            "painting", "painter" -> R.drawable.painter
            "shifting" -> R.drawable.shifting
            "ac repair", "ac_repair" -> R.drawable.ac_repair
            "security", "security guard" -> R.drawable.security
            "gardening" -> R.drawable.gardener
            else -> R.drawable.cleaning
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceOrderScreen(
    categories: List<Category>,
    photos: List<Uri>,
    address: String?,
    details: String?,
    onBackClick: () -> Unit,
    onAddCategory: () -> Unit,
    onRemoveCategory: (Category) -> Unit,
    onAddAddress: () -> Unit,
    onAddDetails: () -> Unit,
    onAddPhotos: () -> Unit,
    onLaunchGallery: () -> Unit,
    onLaunchCamera: () -> Unit,
    onPlaceOrder: (String) -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 3
    var offeredFare by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showPhotoSheet by remember { mutableStateOf(false) }

    val isStep1Valid = categories.isNotEmpty()
    val isStep2Valid = !address.isNullOrEmpty() && !details.isNullOrEmpty()
    val isStep3Valid = offeredFare.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Service", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    KarigarPrimaryButton(
                        text = if (currentStep == 3) "Confirm & Book" else "Next Step →",
                        onClick = {
                            if (currentStep < 3) currentStep++ else showConfirmDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = when (currentStep) {
                            1 -> isStep1Valid
                            2 -> isStep2Valid
                            else -> isStep3Valid
                        }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stepper indicator
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = TealPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = "Step $currentStep of $totalSteps",
                style = MaterialTheme.typography.labelMedium.copy(color = TealPrimary, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally { width -> if (targetState > initialState) width else -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> if (targetState > initialState) -width else width } + fadeOut()
                },
                modifier = Modifier.fillMaxSize()
            ) { step ->
                when (step) {
                    1 -> Step1Categories(categories, onAddCategory, onRemoveCategory)
                    2 -> Step2Details(address, details, photos, onAddAddress, onAddDetails, { showPhotoSheet = true })
                    3 -> Step3Pricing(offeredFare, { offeredFare = it })
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Booking") },
            text = { Text("Are you sure you want to place this order with an offered fare of Rs. $offeredFare?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onPlaceOrder(offeredFare)
                }) {
                    Text("Place Order", color = TealPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showPhotoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Photo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                ListItem(
                    headlineContent = { Text("Choose from Gallery") },
                    leadingContent = { Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, tint = TealPrimary) },
                    modifier = Modifier.clickable {
                        showPhotoSheet = false
                        onLaunchGallery()
                    }
                )
                
                ListItem(
                    headlineContent = { Text("Take Photo") },
                    leadingContent = { Icon(Icons.Rounded.CameraAlt, contentDescription = null, tint = TealPrimary) },
                    modifier = Modifier.clickable {
                        showPhotoSheet = false
                        onLaunchCamera()
                    }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun Step1Categories(
    categories: List<Category>,
    onAddCategory: () -> Unit,
    onRemoveCategory: (Category) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selected Services", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("What type of professional do you need?", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(24.dp))

        // Create a list combining selected categories and the "Add More" button indicator
        val items = categories + null // null represents the Add More button
        val rows = items.chunked(3)

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            rows.forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { category ->
                        if (category != null) {
                            CategoryChip(
                                category = category,
                                modifier = Modifier.weight(1f).aspectRatio(1f),
                                onRemove = { onRemoveCategory(category) }
                            )
                        } else {
                            AddCategoryChip(
                                modifier = Modifier.weight(1f).aspectRatio(1f),
                                onClick = onAddCategory
                            )
                        }
                    }
                    // Fill remaining empty slots in the row with spacers so sizing remains consistent
                    val emptySlots = 3 - rowItems.size
                    repeat(emptySlots) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: Category, modifier: Modifier = Modifier, onRemove: () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = category.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.name, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp, // Slightly smaller to ensure fit
                    textAlign = TextAlign.Center
                )
            }
            IconButton(
                onClick = onRemove, 
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .padding(4.dp)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun AddCategoryChip(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        // Soft Teal background to match the app theme, as requested by the user
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
        border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add", modifier = Modifier.size(32.dp), tint = TealPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Add More", fontWeight = FontWeight.Bold, color = TealPrimary, fontSize = 13.sp)
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun Step2Details(
    address: String?,
    details: String?,
    photos: List<Uri>,
    onAddAddress: () -> Unit,
    onAddDetails: () -> Unit,
    onAddPhotos: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Order Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Address
        ActionCard(
            title = "Service Location",
            value = address ?: "Tap to set your address",
            icon = Icons.Rounded.LocationOn,
            onClick = onAddAddress,
            isSet = address != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Details
        ActionCard(
            title = "Task Description",
            value = details ?: "Describe what needs to be done...",
            icon = Icons.Rounded.Description,
            onClick = onAddDetails,
            isSet = details != null
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Photos (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            photos.forEach { uri ->
                coil.compose.AsyncImage(
                    model = uri,
                    contentDescription = "Photo",
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onAddPhotos() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AddAPhoto, contentDescription = "Add Photo", tint = TealPrimary)
            }
        }
    }
}

@Composable
fun GlideImage(
    model: Uri,
    contentDescription: String,
    modifier: Modifier,
    contentScale: ContentScale
) {
    TODO("Not yet implemented")
}

@Composable
fun ActionCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, isSet: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(TealPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TealPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSet) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSet) MaterialTheme.colorScheme.onSurface else TextSecondary
                    ),
                    maxLines = 2
                )
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Edit", tint = TextSecondary)
        }
    }
}

@Composable
fun Step3Pricing(offeredFare: String, onFareChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Set Your Price", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Offer a fair price to attract the best professionals.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = offeredFare,
            onValueChange = onFareChange,
            label = { Text("Offered Fare (Rs.)") },
            leadingIcon = { Text("Rs.", modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TealPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Rounded.Info, contentDescription = "Info", tint = TealPrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("This is an initial offer. Professionals may counter-offer depending on the exact requirements.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PlaceOrderPreview() {
    KarigarTheme {
        PlaceOrderScreen(
            categories = listOf(Category("Plumber", R.drawable.plumber)),
            photos = emptyList(),
            address = null,
            details = null,
            onBackClick = {},
            onAddCategory = {},
            onRemoveCategory = {},
            onAddAddress = {},
            onAddDetails = {},
            onAddPhotos = {},
            onLaunchGallery = {},
            onLaunchCamera = {},
            onPlaceOrder = {}
        )
    }
}
