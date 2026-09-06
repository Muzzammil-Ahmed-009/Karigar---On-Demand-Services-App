package com.karigar.app.ui.orders

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val orderId = intent.getStringExtra("order_id") ?: "Unknown"
        val amount = intent.getStringExtra("amount") ?: "0"
        
        setContent {
            KarigarTheme {
                PaymentScreen(
                    orderId = orderId,
                    amount = amount,
                    onBack = { finish() },
                    onPaySuccess = {
                        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ReviewWorkerActivity::class.java).apply {
                            putExtra("order_id", orderId)
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    orderId: String,
    amount: String,
    onBack: () -> Unit,
    onPaySuccess: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("Credit Card") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total to Pay", color = Color.Gray, fontSize = 14.sp)
                        Text("Rs $amount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TealPrimary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    KarigarPrimaryButton(
                        text = "Pay Rs $amount",
                        onClick = onPaySuccess,
                        modifier = Modifier.fillMaxWidth()
                    )
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
            // Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Order ID", color = Color.Gray)
                        Text("#ORD-${orderId.takeLast(6).uppercase()}", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Service Amount", color = Color.Gray)
                        Text("Rs $amount", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodCard(
                title = "Credit / Debit Card",
                icon = Icons.Default.CreditCard,
                isSelected = selectedMethod == "Credit Card",
                onClick = { selectedMethod = "Credit Card" }
            )
            Spacer(modifier = Modifier.height(12.dp))
            PaymentMethodCard(
                title = "Karigar Wallet",
                icon = Icons.Default.AccountBalanceWallet,
                isSelected = selectedMethod == "Wallet",
                onClick = { selectedMethod = "Wallet" }
            )
            Spacer(modifier = Modifier.height(12.dp))
            PaymentMethodCard(
                title = "Cash on Delivery",
                icon = Icons.Default.Money,
                isSelected = selectedMethod == "COD",
                onClick = { selectedMethod = "COD" }
            )
        }
    }
}

@Composable
fun PaymentMethodCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) TealPrimary else Color.Transparent
    val backgroundColor = if (isSelected) TealPrimary.copy(alpha = 0.05f) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp, modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = TealPrimary)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    KarigarTheme {
        PaymentScreen(
            orderId = "123456789",
            amount = "1500",
            onBack = {},
            onPaySuccess = {}
        )
    }
}
