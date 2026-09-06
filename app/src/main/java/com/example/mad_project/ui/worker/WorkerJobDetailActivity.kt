package com.karigar.app.ui.worker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karigar.app.data.model.Order
import com.karigar.app.data.repository.OrderRepository
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.orders.OrderViewModel
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkerJobDetailActivity : ComponentActivity() {

    @Inject
    lateinit var orderRepository: OrderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val orderId = intent.getStringExtra("order_id") ?: return finish()

        setContent {
            KarigarTheme {
                val orders = orderRepository.orders.collectAsStateWithLifecycle().value
                val job = orders.find { it.id == orderId }
                
                val coroutineScope = rememberCoroutineScope()

                if (job != null) {
                    WorkerJobDetailScreen(
                        job = job,
                        onBack = { finish() },
                        onUpdateStatus = { newStatus ->
                            coroutineScope.launch {
                                orderRepository.updateOrderStatus(job.id, newStatus)
                                Toast.makeText(this@WorkerJobDetailActivity, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                                if (newStatus == "Completed") {
                                    finish() // Close after completion
                                }
                            }
                        },
                        onNavigateToCustomer = { lat, lng, address ->
                            val gmmIntentUri = if (lat != 0.0 && lng != 0.0) {
                                Uri.parse("google.navigation:q=$lat,$lng")
                            } else {
                                Uri.parse("google.navigation:q=${Uri.encode(address)}")
                            }
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            if (mapIntent.resolveActivity(packageManager) != null) {
                                startActivity(mapIntent)
                            } else {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(address)}")))
                            }
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Job not found or loading...")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerJobDetailScreen(
    job: Order,
    onBack: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onNavigateToCustomer: (Double, Double, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (job.status) {
                        "Assigned", "Confirmed" -> {
                            KarigarPrimaryButton(
                                text = "Start Journey",
                                onClick = { onUpdateStatus("On the Way") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "On the Way" -> {
                            KarigarPrimaryButton(
                                text = "Mark as Completed",
                                onClick = { onUpdateStatus("Completed") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "Completed" -> {
                            Button(
                                onClick = { }, 
                                enabled = false, 
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFE8F5E9), disabledContentColor = Color(0xFF388E3C))
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Job Completed")
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF4F6F9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(job.categories.joinToString(", "), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Surface(
                            color = TealPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(job.status, color = TealPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Order ID: #ORD-${job.id.takeLast(6).uppercase()}", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Date: ${job.scheduledTime}", fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fare: Rs ${job.offeredFare}", fontSize = 16.sp, color = TealPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Service Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(job.address, fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { onNavigateToCustomer(job.latitude, job.longitude, job.address) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Navigate")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description Card
            if (job.details.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Task Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(job.details, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun WorkerJobDetailScreenPreview() {
    KarigarTheme {
        WorkerJobDetailScreen(
            job = Order(id = "1", status = "Assigned", categories = listOf("Electrician"), address = "Clifton, Karachi", offeredFare = "800", details = "Fix the fan"),
            onBack = {},
            onUpdateStatus = {},
            onNavigateToCustomer = { _, _, _ -> }
        )
    }
}
