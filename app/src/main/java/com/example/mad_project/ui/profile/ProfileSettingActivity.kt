package com.karigar.app.ui.profile

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.karigar.app.R
import com.karigar.app.data.model.User
import com.karigar.app.data.repository.AuthRepository
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextPrimary
import com.karigar.app.ui.theme.TextSecondary
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileSettingActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private var selectedPhotoUriState = mutableStateOf<Uri?>(null)
    
    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            selectedPhotoUriState.value = result.uriContent
        } else {
            result.error?.let {
                Toast.makeText(this, "Image cropping failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KarigarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    ProfileSettingScreenWrapper()
                }
            }
        }
    }

    @Composable
    fun ProfileSettingScreenWrapper() {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("Male") }
        var phone by remember { mutableStateOf("") }
        var profileImageUrl by remember { mutableStateOf("") }
        
        var isLoading by remember { mutableStateOf(true) }
        var currentUser by remember { mutableStateOf<User?>(null) }
        val selectedPhotoUri by selectedPhotoUriState

        LaunchedEffect(Unit) {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                Toast.makeText(this@ProfileSettingActivity, "User not logged in", Toast.LENGTH_SHORT).show()
                finish()
                return@LaunchedEffect
            }
            phone = authRepository.getCurrentUserPhone() ?: ""
            
            authRepository.getUserProfile(userId).collect { result ->
                result.onSuccess { user ->
                    if (user != null) {
                        currentUser = user
                        name = user.name
                        email = user.email
                        city = user.city
                        gender = user.gender.ifEmpty { "Male" }
                        profileImageUrl = user.profileImage
                    }
                    isLoading = false
                }.onFailure {
                    Toast.makeText(this@ProfileSettingActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
        }

        val customCropOptions = CropImageOptions(
            activityTitle = "Adjust Profile Picture",
            allowFlipping = true,
            allowRotation = true,
            guidelines = CropImageView.Guidelines.ON,
            aspectRatioX = 1,
            aspectRatioY = 1,
            fixAspectRatio = true,
            cropShape = CropImageView.CropShape.OVAL,
            toolbarColor = android.graphics.Color.parseColor("#009688"),
            toolbarTitleColor = android.graphics.Color.WHITE,
            toolbarBackButtonColor = android.graphics.Color.WHITE,
            activityMenuIconColor = android.graphics.Color.WHITE
        )

        ProfileSettingScreenContent(
            name = name,
            onNameChange = { name = it },
            email = email,
            onEmailChange = { email = it },
            phone = phone,
            city = city,
            onCityChange = { city = it },
            gender = gender,
            onGenderChange = { gender = it },
            profileImageUrl = profileImageUrl,
            selectedPhotoUri = selectedPhotoUri,
            onBackClick = { finish() },
            onGallerySelected = {
                cropImageLauncher.launch(
                    CropImageContractOptions(
                        uri = null,
                        cropImageOptions = customCropOptions.copy(
                            imageSourceIncludeGallery = true,
                            imageSourceIncludeCamera = false
                        )
                    )
                )
            },
            onCameraSelected = {
                cropImageLauncher.launch(
                    CropImageContractOptions(
                        uri = null,
                        cropImageOptions = customCropOptions.copy(
                            imageSourceIncludeGallery = false,
                            imageSourceIncludeCamera = true
                        )
                    )
                )
            },
            onSaveClick = {
                if (name.trim().isEmpty()) {
                    Toast.makeText(this@ProfileSettingActivity, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@ProfileSettingScreenContent
                }
                
                val userToSave = currentUser?.copy(
                    name = name.trim(),
                    email = email.trim(),
                    city = city.trim(),
                    gender = gender
                ) ?: User(
                    id = authRepository.getCurrentUserId() ?: "",
                    phone = phone,
                    name = name.trim(),
                    email = email.trim(),
                    city = city.trim(),
                    gender = gender,
                    profileImage = profileImageUrl
                )

                val progressDialog = ProgressDialog(this@ProfileSettingActivity).apply {
                    setMessage("Saving profile...")
                    setCancelable(false)
                    show()
                }

                lifecycleScope.launch {
                    val result = authRepository.updateUserProfileWithImage(userToSave, selectedPhotoUri)
                    progressDialog.dismiss()
                    
                    result.onSuccess {
                        Toast.makeText(this@ProfileSettingActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }.onFailure { e ->
                        Toast.makeText(this@ProfileSettingActivity, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingScreenContent(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    city: String,
    onCityChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    profileImageUrl: String,
    selectedPhotoUri: Uri?,
    onBackClick: () -> Unit,
    onGallerySelected: () -> Unit,
    onCameraSelected: () -> Unit,
    onSaveClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Add Photo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 16.dp)
                )
                
                // Gallery Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                            onGallerySelected()
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Choose from Gallery", fontSize = 16.sp, color = Color.Black)
                }
                
                // Camera Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                            onCameraSelected()
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CameraAlt,
                        contentDescription = "Camera",
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Take Photo", fontSize = 16.sp, color = Color.Black)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Profile Settings",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 40.dp), // To center align with the back button offset
                textAlign = TextAlign.Center
            )
        }
        
        Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Picture Area
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            val placeholderModifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE))
                .border(2.dp, Color.White, CircleShape)

            if (selectedPhotoUri != null) {
                AsyncImage(
                    model = selectedPhotoUri,
                    contentDescription = "Profile Photo",
                    modifier = placeholderModifier,
                    contentScale = ContentScale.Crop
                )
            } else if (profileImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Photo",
                    modifier = placeholderModifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.emp_profile),
                    contentDescription = "Profile Photo",
                    modifier = placeholderModifier,
                    contentScale = ContentScale.Crop
                )
            }

            // Edit Button Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(36.dp)
                    .background(TealPrimary, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { showBottomSheet = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = "Edit Photo",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name.ifEmpty { "Karigar User" },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Update your public profile",
            fontSize = 15.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Inputs
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = TealPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = TealPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { },
                label = { Text("Contact Number (Verified)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledTextColor = TextPrimary,
                    disabledLabelColor = TextSecondary
                ),
                enabled = false, // Read only
                singleLine = true
            )

            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = TealPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Gender Selection
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Gender",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val genders = listOf("Male", "Female", "Other")
                genders.forEach { g ->
                    val isSelected = gender == g
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) TealPrimary.copy(alpha = 0.1f) else Color(0xFFF8F9FA))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) TealPrimary else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onGenderChange(g) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = g,
                            color = if (isSelected) TealPrimary else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        KarigarPrimaryButton(
            text = "Save Changes",
            onClick = onSaveClick,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Last updated on Oct 24, 2023", // Can be dynamic later
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSettingScreenPreview() {
    KarigarTheme {
        ProfileSettingScreenContent(
            name = "Ali Ahmed",
            onNameChange = {},
            email = "ali@example.com",
            onEmailChange = {},
            phone = "+92 300 1234567",
            city = "Lahore",
            onCityChange = {},
            gender = "Male",
            onGenderChange = {},
            profileImageUrl = "",
            selectedPhotoUri = null,
            onBackClick = {},
            onGallerySelected = {},
            onCameraSelected = {},
            onSaveClick = {}
        )
    }
}
