package com.karigar.app.ui.worker

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.R
import com.karigar.app.data.model.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerFeedScreen(
    orders: List<Order>,
    currentWorkerId: String,
    onSubmitBid: (Order, String, String) -> Unit
) {
    var isOnline by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All") }
    
    val pendingJobs = orders.filter { it.status == "Pending" && (selectedCategory == "All" || it.categories.contains(selectedCategory)) }
    val activeJobs = orders.filter { it.status in listOf("Assigned", "Confirmed", "On the Way") && it.assignedWorkerId == currentWorkerId }

    var selectedJobForBid by remember { mutableStateOf<Order?>(null) }
    
    if (selectedJobForBid != null) {
        BidBottomSheet(
            job = selectedJobForBid!!,
            onDismiss = { selectedJobForBid = null },
            onSubmit = { amount, cover ->
                onSubmitBid(selectedJobForBid!!, amount, cover)
                selectedJobForBid = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        // Earnings & Status Header
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Today's Earnings", fontSize = 14.sp, color = Color.Gray)
                        Text("Rs 2,500", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2CBD))
                        Text("3 Jobs Completed", fontSize = 12.sp, color = Color(0xFF4CAF50))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(if (isOnline) "Online" else "Offline", fontWeight = FontWeight.Bold, color = if (isOnline) Color(0xFF4CAF50) else Color.Gray)
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { isOnline = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF4CAF50), checkedTrackColor = Color(0xFFC8E6C9))
                        )
                    }
                }
            }
        }

        if (!isOnline) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PowerOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("You are offline", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Go online to start receiving new jobs", color = Color.DarkGray)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Active Tasks Section
                if (activeJobs.isNotEmpty()) {
                    item {
                        Text("Active Tasks", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    items(activeJobs) { job ->
                        ActiveTaskCard(job = job)
                    }
                }

                // Smart Filters
                item {
                    Text("Available Jobs", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        val categories = listOf("All", "Cleaning", "Plumbing", "Electrician", "AC Repair")
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0F2CBD),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                if (pendingJobs.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Text("No jobs available right now", color = Color.Gray)
                        }
                    }
                } else {
                    items(pendingJobs) { job ->
                        JobCard(job = job, onBidClick = { selectedJobForBid = job })
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveTaskCard(job: Order) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                    Text(job.status, color = Color(0xFF4CAF50), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Text("Order #ORD-${job.id.takeLast(6).uppercase()}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(job.categories.joinToString(", "), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(job.address, fontSize = 14.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val gmmIntentUri = if (job.latitude != 0.0 && job.longitude != 0.0) {
                        Uri.parse("google.navigation:q=${job.latitude},${job.longitude}")
                    } else {
                        Uri.parse("google.navigation:q=${Uri.encode(job.address)}")
                    }
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        val fallbackUri = Uri.parse("https://maps.google.com/?q=${Uri.encode(job.address)}")
                        context.startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Navigate to Customer")
            }
        }
    }
}

@Composable
fun JobCard(job: Order, onBidClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(job.categories.joinToString(", "), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(job.scheduledTime, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(job.address, fontSize = 14.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Est. Fare", fontSize = 12.sp, color = Color.Gray)
                    Text("Rs ${job.offeredFare.ifEmpty { "800" }}", fontWeight = FontWeight.Bold, color = Color(0xFF0F2CBD))
                }
                Column {
                    Text("Distance", fontSize = 12.sp, color = Color.Gray)
                    Text("3.5 km", fontWeight = FontWeight.Bold) // Dummy distance
                }
                Button(
                    onClick = onBidClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))
                ) {
                    Text("Place Bid")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidBottomSheet(
    job: Order,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var bidAmount by remember { mutableStateOf(job.offeredFare.ifEmpty { "800" }) }
    var coverLetter by remember { mutableStateOf("") }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text("Place your Bid", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Job: ${job.categories.joinToString(", ")}", color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = bidAmount,
                onValueChange = { bidAmount = it },
                label = { Text("Your Bid Amount (Rs)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = coverLetter,
                onValueChange = { coverLetter = it },
                label = { Text("Cover Letter (Optional)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { if (bidAmount.isNotEmpty()) onSubmit(bidAmount, coverLetter) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))
            ) {
                Text("Submit Bid")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkerFeedScreenPreview() {
    val sampleJobs = listOf(
        Order(id = "1", status = "Pending", categories = listOf("Cleaning"), address = "Gulshan-e-Iqbal, Karachi", offeredFare = "1200", scheduledTime = "Today, 4:00 PM"),
        Order(id = "2", status = "Pending", categories = listOf("Plumbing"), address = "DHA Phase 6, Karachi", offeredFare = "800", scheduledTime = "Tomorrow, 10:00 AM"),
        Order(id = "3", status = "On the Way", categories = listOf("Electrician"), address = "Clifton, Karachi", offeredFare = "1500", scheduledTime = "Today, 1:00 PM")
    )
    
    MaterialTheme {
        WorkerFeedScreen(
            orders = sampleJobs,
            currentWorkerId = "worker123",
            onSubmitBid = { _, _, _ -> }
        )
    }
}
