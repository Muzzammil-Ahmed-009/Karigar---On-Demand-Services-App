package com.karigar.app.ui.services

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.R
import com.karigar.app.data.model.Order
import com.karigar.app.data.model.WorkerBid
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.orders.OrderViewModel
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextSecondary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiddingDetailsActivity : ComponentActivity() {

    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val order = intent.getParcelableExtra<Order>("order")
        if (order == null) {
            finish()
            return
        }

        setContent {
            KarigarTheme {
                BiddingDetailsScreen(
                    order = order,
                    onBackClick = { finish() },
                    onAcceptBid = { bid ->
                        acceptBid(order, bid)
                    }
                )
            }
        }
    }

    private fun acceptBid(order: Order, bid: WorkerBid) {
        bid.isAccepted = true
        orderViewModel.acceptBid(order.id, bid)
        Toast.makeText(this, "Bid Accepted! ${bid.workerName} is on the way.", Toast.LENGTH_LONG).show()
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiddingDetailsScreen(
    order: Order,
    onBackClick: () -> Unit,
    onAcceptBid: (WorkerBid) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Offers", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Request", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(order.categories.joinToString(" · "), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(order.address, color = Color.White, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Your Offer:", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rs. ${order.offeredFare}", color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "${order.bids.size} Offers Received",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(order.bids) { bid ->
                    BidCard(bid = bid, onAcceptClick = { onAcceptBid(bid) })
                }
            }
        }
    }
}

@Composable
fun BidCard(bid: WorkerBid, onAcceptClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = if (bid.workerImageResId != 0) bid.workerImageResId else R.drawable.emp_profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(bid.workerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${bid.workerRating}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Rounded.Work, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${bid.jobsCompleted} jobs", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    if (bid.vehicleDetails.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Work, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp)) // Using work icon as placeholder for equipment/vehicle
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(bid.vehicleDetails, color = TealPrimary, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Text(
                    text = "Rs. ${bid.bidAmount}",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.Timer, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ETA: ${bid.etaString}", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
                KarigarPrimaryButton(
                    text = "Accept",
                    onClick = onAcceptClick,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun BiddingDetailsPreview() {
    KarigarTheme {
        BiddingDetailsScreen(
            order = Order(
                categories = listOf("Plumbing", "Repair"),
                address = "F-7 Markaz, Islamabad",
                offeredFare = "1500",
                bids = mutableListOf(
                    WorkerBid(
                        workerName = "Ahmed Khan",
                        workerRating = 4.8f,
                        jobsCompleted = 124,
                        bidAmount = "1600",
                        etaString = "15 mins",
                        vehicleDetails = "Honda CD 70, Black"
                    ),
                    WorkerBid(
                        workerName = "Usman Ali",
                        workerRating = 4.5f,
                        jobsCompleted = 45,
                        bidAmount = "1500",
                        etaString = "30 mins",
                        vehicleDetails = "Professional Toolkit"
                    )
                )
            ),
            onBackClick = {},
            onAcceptBid = {}
        )
    }
}
