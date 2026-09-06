package com.karigar.app.ui.orders

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.karigar.app.data.model.Order
import com.karigar.app.data.repository.OrderRepository
import com.karigar.app.ui.chat.ChatActivity
import com.karigar.app.ui.services.PlaceOrderActivity
import com.karigar.app.utils.PdfGeneratorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailActivity : ComponentActivity() {

    @Inject
    lateinit var orderRepository: OrderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        @Suppress("DEPRECATION")
        val order = intent.getParcelableExtra<Order>("order") ?: run {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                val coroutineScope = rememberCoroutineScope()
                var currentOrder by remember { mutableStateOf(order) }
                var showCancelDialog by remember { mutableStateOf(false) }
                
                if (showCancelDialog) {
                    CancelOrderDialog(
                        onDismiss = { showCancelDialog = false },
                        onConfirm = { reason ->
                            coroutineScope.launch {
                                currentOrder.cancellationReason = reason
                                orderRepository.updateOrderStatus(currentOrder.id, "Cancelled")
                                currentOrder = currentOrder.copy(status = "Cancelled", cancellationReason = reason)
                                Toast.makeText(this@OrderDetailActivity, "Order cancelled.", Toast.LENGTH_SHORT).show()
                                showCancelDialog = false
                            }
                        }
                    )
                }

                OrderDetailScreen(
                    order = currentOrder,
                    onBack = { finish() },
                    onCancelClick = { showCancelDialog = true },
                    onConfirmWorker = {
                        coroutineScope.launch {
                            orderRepository.updateOrderStatus(currentOrder.id, "Confirmed")
                            currentOrder = currentOrder.copy(status = "Confirmed")
                            Toast.makeText(this@OrderDetailActivity, "Worker confirmed!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onHelp = { showHelpDialog() },
                    onShareInvoice = {
                        val uri = PdfGeneratorUtil.generateInvoiceAndGetUri(this, currentOrder)
                        if (uri != null) {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            startActivity(Intent.createChooser(shareIntent, "Share Invoice"))
                        } else {
                            Toast.makeText(this, "Failed to generate invoice", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onBookAgain = {
                        startActivity(Intent(this, PlaceOrderActivity::class.java))
                        finish()
                    },
                    onMessageWorker = {
                        val intent = Intent(this, ChatActivity::class.java).apply {
                            putExtra("worker_name", currentOrder.assignedWorkerName)
                            putExtra("worker_phone", currentOrder.assignedWorkerPhone)
                        }
                        startActivity(intent)
                    },
                    onCallWorker = {
                        if (currentOrder.assignedWorkerPhone.isNotEmpty()) {
                            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${currentOrder.assignedWorkerPhone}")))
                        } else {
                            Toast.makeText(this, "Phone not available", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onPayInvoice = {
                        val intent = Intent(this@OrderDetailActivity, PaymentActivity::class.java).apply {
                            putExtra("order_id", currentOrder.id)
                            putExtra("amount", currentOrder.offeredFare)
                        }
                        startActivity(intent)
                    },
                    onRateWorker = {
                        val intent = Intent(this@OrderDetailActivity, ReviewWorkerActivity::class.java).apply {
                            putExtra("order_id", currentOrder.id)
                            putExtra("worker_name", currentOrder.assignedWorkerName)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun showHelpDialog() {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+923000000000")))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    order: Order,
    onBack: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmWorker: () -> Unit,
    onHelp: () -> Unit,
    onShareInvoice: () -> Unit,
    onBookAgain: () -> Unit,
    onMessageWorker: () -> Unit,
    onCallWorker: () -> Unit,
    onPayInvoice: () -> Unit = {},
    onRateWorker: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        color = getStatusColor(order.status),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = order.status,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    IconButton(onClick = onHelp) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Help")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            OrderBottomBar(
                order = order,
                onCancelClick = onCancelClick,
                onConfirmWorker = onConfirmWorker,
                onHelp = onHelp,
                onBookAgain = onBookAgain,
                onPayInvoice = onPayInvoice,
                onRateWorker = onRateWorker
            )
        },
        containerColor = Color(0xFFF4F6F9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServiceCard(order)
            TimelineCard(order.status)
            
            if (order.status == "On the Way") {
                LiveTrackingCard()
            }
            
            if (order.status in listOf("Assigned", "Confirmed", "On the Way", "Completed")) {
                WorkerCard(order, onMessageWorker, onCallWorker)
            }
            
            if (order.status in listOf("Assigned", "Confirmed", "On the Way", "Completed")) {
                InvoiceCard(order, onShareInvoice)
            }
            
            if (order.status == "Cancelled" && order.cancellationReason.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4F4))) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Cancellation Reason", color = Color.Red, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(order.cancellationReason)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(order: Order) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF0F4FF),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Build, 
                        contentDescription = null,
                        tint = Color(0xFF0F2CBD),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(order.categories.joinToString(", "), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Order #ORD-${order.id.takeLast(6).uppercase()}", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(order.address, fontSize = 14.sp, color = Color.DarkGray)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(order.scheduledTime, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun TimelineCard(status: String) {
    val steps = listOf("Pending", "Accepted", "Assigned", "On the Way", "Completed")
    val currentIndex = steps.indexOf(status).takeIf { it >= 0 } ?: if (status == "Cancelled") 0 else steps.size

    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Order Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))
            
            steps.forEachIndexed { index, step ->
                val isCompleted = index <= currentIndex
                val isCurrent = index == currentIndex
                val color = when {
                    status == "Cancelled" && index == 0 -> Color(0xFFF44336)
                    isCurrent -> Color(0xFF0F2CBD)
                    isCompleted -> Color(0xFF4CAF50)
                    else -> Color(0xFFE0E0E0)
                }
                
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .fillMaxHeight()
                                    .background(if (isCompleted && index < currentIndex) Color(0xFF4CAF50) else Color(0xFFE0E0E0))
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = step,
                        color = if (isCompleted) Color.Black else Color.Gray,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LiveTrackingCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Live Tracking (Simulated)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp))) {
                val karachiLocation = LatLng(24.8607, 67.0011)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(karachiLocation, 14f)
                }
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = karachiLocation),
                        title = "Worker Location",
                        snippet = "On the way to your address"
                    )
                }
            }
        }
    }
}

@Composable
fun WorkerCard(order: Order, onMessage: () -> Unit, onCall: () -> Unit) {
    if (order.assignedWorkerName.isEmpty()) return
    
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Your Worker", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Color.LightGray, modifier = Modifier.size(52.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.assignedWorkerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(order.assignedWorkerExperience, fontSize = 12.sp, color = Color.Gray)
                    Text("${order.assignedWorkerRating} ⭐", fontSize = 12.sp, color = Color(0xFFF5A623))
                }
                IconButton(onClick = onMessage, modifier = Modifier.background(Color(0xFFF0F4FF), CircleShape)) {
                    Icon(Icons.Default.Message, contentDescription = "Message", tint = Color(0xFF0F2CBD))
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onCall, modifier = Modifier.background(Color(0xFFF0F4FF), CircleShape)) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0F2CBD))
                }
            }
        }
    }
}

@Composable
fun InvoiceCard(order: Order, onShare: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Payment Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (order.status == "Completed") {
                    TextButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Share Receipt")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            
            val baseFare = order.offeredFare.toIntOrNull() ?: 800
            val extraWork = if (order.status == "Completed") 200 else 0
            val discount = 50
            val total = baseFare + extraWork - discount

            InvoiceRow("Base Fare", "Rs $baseFare")
            if (extraWork > 0) {
                InvoiceRow("Extra Materials", "Rs $extraWork")
            }
            InvoiceRow("Discount", "-Rs $discount", valueColor = Color(0xFF4CAF50))
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Rs $total", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F2CBD))
            }
        }
    }
}

@Composable
fun InvoiceRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.DarkGray)
        Text(value, color = valueColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun OrderBottomBar(
    order: Order,
    onCancelClick: () -> Unit,
    onConfirmWorker: () -> Unit,
    onHelp: () -> Unit,
    onBookAgain: () -> Unit,
    onPayInvoice: () -> Unit,
    onRateWorker: () -> Unit
) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            when (order.status) {
                "Pending", "Accepted" -> {
                    Button(
                        onClick = onCancelClick, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF4F4), contentColor = Color(0xFFFF3B30))
                    ) {
                        Text("Cancel Order")
                    }
                }
                "Assigned" -> {
                    Button(onClick = onConfirmWorker, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))) {
                        Text("Confirm Worker")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onCancelClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF4F4), contentColor = Color(0xFFFF3B30))) {
                        Text("Cancel Order")
                    }
                }
                "Confirmed", "On the Way" -> {
                    Button(onClick = onHelp, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))) {
                        Text("Help & Support")
                    }
                }
                "Completed" -> {
                    if (!order.isPaid) {
                        Button(onClick = onPayInvoice, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))) {
                            Text("Pay Invoice")
                        }
                    } else if (!order.isReviewed) {
                        Button(onClick = onRateWorker, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))) {
                            Text("Rate Worker")
                        }
                    } else {
                        Button(onClick = onBookAgain, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))) {
                            Text("Book Again")
                        }
                    }
                }
                "Cancelled" -> {
                    Button(onClick = onBookAgain, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2CBD))) {
                        Text("Book Again")
                    }
                }
            }
        }
    }
}

@Composable
fun CancelOrderDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var selectedReason by remember { mutableStateOf("") }
    val reasons = listOf("Worker is taking too long", "Changed my mind", "Found another service", "Price is too high")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel Order") },
        text = {
            Column {
                Text("Please select a reason for cancellation:")
                Spacer(Modifier.height(8.dp))
                reasons.forEach { reason ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        RadioButton(selected = selectedReason == reason, onClick = { selectedReason = reason })
                        Text(reason, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (selectedReason.isNotEmpty()) onConfirm(selectedReason) }, enabled = selectedReason.isNotEmpty()) {
                Text("Confirm Cancel")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Order")
            }
        }
    )
}

fun getStatusColor(status: String): Color = when (status) {
    "Pending" -> Color(0xFFFF9800)
    "Accepted" -> Color(0xFF2196F3)
    "Assigned" -> Color(0xFF9C27B0)
    "On the Way" -> Color(0xFF03A9F4)
    "Confirmed" -> Color(0xFF0F2CBD)
    "Completed" -> Color(0xFF4CAF50)
    "Cancelled" -> Color(0xFFF44336)
    else -> Color.Gray
}

@Preview(showBackground = true)
@Composable
fun OrderDetailScreenPreview() {
    val sampleOrder = Order(
        id = "123456789",
        status = "On the Way",
        categories = listOf("Cleaning", "AC Repair"),
        address = "123 Main Street, Karachi",
        scheduledTime = "Today, 3:00 PM",
        assignedWorkerName = "Ali Raza",
        assignedWorkerPhone = "03001234567",
        assignedWorkerExperience = "3 yrs experience",
        assignedWorkerRating = 4.8f,
        offeredFare = "800",
        cancellationReason = ""
    )
    MaterialTheme {
        OrderDetailScreen(
            order = sampleOrder,
            onBack = {},
            onCancelClick = {},
            onConfirmWorker = {},
            onHelp = {},
            onShareInvoice = {},
            onBookAgain = {},
            onMessageWorker = {},
            onCallWorker = {}
        )
    }
}
