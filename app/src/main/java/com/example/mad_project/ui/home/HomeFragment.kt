package com.karigar.app.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.karigar.app.R
import com.karigar.app.data.model.User
import com.karigar.app.ui.services.AllServicesActivity
import com.karigar.app.ui.services.PlaceOrderActivity
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextPrimary
import com.karigar.app.ui.theme.TextSecondary
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var waitingForNewCategory = false

    private val placeOrderLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val requestNewCategory = result.data?.getBooleanExtra("request_new_category", false) ?: false
            if (requestNewCategory) {
                waitingForNewCategory = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                KarigarTheme {
                    val popularWorkers by viewModel.popularWorkers.collectAsStateWithLifecycle(initialValue = emptyList())
                    val categories by viewModel.categories.collectAsStateWithLifecycle(initialValue = emptyList())
                    
                    HomeScreen(
                        popularWorkers = popularWorkers,
                        categories = categories,
                        onCategoryClick = { category ->
                            if (category == "More") {
                                startActivity(Intent(requireContext(), AllServicesActivity::class.java))
                            } else {
                                navigateToPlaceOrder(category)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun navigateToPlaceOrder(categoryName: String) {
        val intent = Intent(requireContext(), PlaceOrderActivity::class.java).apply {
            putExtra("category_name", categoryName)
            if (waitingForNewCategory) {
                putExtra("adding_category", true)
                waitingForNewCategory = false
            }
        }
        placeOrderLauncher.launch(intent)
    }
}

@Composable
fun HomeScreen(
    popularWorkers: List<User>, 
    categories: List<com.karigar.app.data.model.ServiceCategory>, 
    onCategoryClick: (String) -> Unit
) {
    // Categories are now dynamically fetched from Firestore
    // If the list is empty (loading), we can optionally show a shimmer or empty state, but it will populate quickly.

    

    // The workers list is passed from the viewmodel via popularWorkers parameter


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp) // padding for floating bottom nav
    ) {
        // Search Bar
        item {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val borderColor by animateColorAsState(
                targetValue = if (isPressed) TealPrimary else Color.Transparent,
                animationSpec = tween(durationMillis = 300)
            )
            
            val borderWidth by animateDpAsState(
                targetValue = if (isPressed) 2.dp else 0.dp,
                animationSpec = tween(durationMillis = 300)
            )

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 8.dp else 4.dp),
                border = BorderStroke(borderWidth, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = { onCategoryClick("More") }
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isPressed) TealPrimary else TextSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "I want to hire a...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Services Header
        item {
            Text(
                text = "Our Services",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Grid for Categories (4 columns)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                val chunked = categories.chunked(4)
                chunked.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowItems.forEach { category ->
                            CategoryItem(
                                category = category,
                                modifier = Modifier.weight(1f),
                                onClick = { onCategoryClick(category.name) }
                            )
                        }
                        // Fill empty spaces in the last row if any
                        repeat(4 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Popular Workers Header
        item {
            Text(
                text = "Popular Workers",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Worker List
        items(popularWorkers.size) { index ->
            WorkerCard(worker = popularWorkers[index])
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun CategoryItem(category: com.karigar.app.data.model.ServiceCategory, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = if (category.name == "More") Color(0xFFF3F3F3) else Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.size(72.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (category.iconUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(category.iconUrl),
                        contentDescription = category.name,
                        modifier = Modifier
                            .size(44.dp)
                            .padding(if (category.name == "More") 6.dp else 0.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    val iconRes = when (category.localIconRes) {
                        "cleaning" -> R.drawable.cleaning
                        "repairing" -> R.drawable.repairing
                        "carpenter" -> R.drawable.carpenter
                        "electrician" -> R.drawable.electrician
                        "plumber" -> R.drawable.plumber
                        "painter" -> R.drawable.painter
                        "shifting" -> R.drawable.shifting
                        "ac_repair" -> R.drawable.ac_repair
                        "security" -> R.drawable.security
                        "gardener" -> R.drawable.gardener
                        "more" -> R.drawable.more
                        else -> R.drawable.more
                    }
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = category.name,
                        modifier = Modifier
                            .size(44.dp)
                            .padding(if (category.name == "More") 6.dp else 0.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary),
            maxLines = 1
        )
    }
}

@Composable
fun WorkerCard(worker: User) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val imagePainter = if (worker.profileImage.isNotEmpty()) {
                rememberAsyncImagePainter(worker.profileImage)
            } else {
                painterResource(id = R.drawable.emp_profile)
            }
            Image(
                painter = imagePainter,
                contentDescription = worker.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = worker.name.ifEmpty { "Karigar Worker" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Professional Worker",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFF5A623),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.5", // TODO: Add rating to User model
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFF5A623))
                    )
                }
            }
            Box(
                modifier = Modifier
                    .background(TealPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Rs. 500/hr", // TODO: Add rate to User model if needed
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TealPrimary)
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    KarigarTheme {
        HomeScreen(popularWorkers = emptyList(), categories = emptyList(), onCategoryClick = {})
    }
}

