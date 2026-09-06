package com.karigar.app.ui.orders

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.main.MainActivity
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewWorkerActivity : ComponentActivity() {

    private val viewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val orderId = intent.getStringExtra("order_id") ?: "Unknown"
        // Ideally fetch worker details from order ID or pass them in Intent
        val workerName = intent.getStringExtra("worker_name") ?: "Your Worker"

        setContent {
            KarigarTheme {
                ReviewWorkerScreen(
                    workerName = workerName,
                    onClose = { navigateToHome() },
                    onSubmit = { rating, review ->
                        viewModel.submitReview(orderId, rating, review)
                        Toast.makeText(this, "Review Submitted! Thank you.", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                )
            }
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWorkerScreen(
    workerName: String,
    onClose: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rate & Review") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                    KarigarPrimaryButton(
                        text = "Submit Review",
                        onClick = { onSubmit(rating, reviewText) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = rating > 0
                    )
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Worker Avatar
            Surface(
                shape = CircleShape,
                color = Color.LightGray.copy(alpha = 0.5f),
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    Icons.Default.Person, 
                    contentDescription = null, 
                    tint = Color.Gray,
                    modifier = Modifier.padding(20.dp).fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "How was your experience with",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Text(
                text = workerName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Star Rating Row
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star $i",
                        tint = if (i <= rating) Color(0xFFFFC107) else Color.LightGray,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { rating = i }
                            .padding(4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Review Text Field
            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = { Text("Write your feedback here (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color.LightGray
                ),
                maxLines = 4
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewWorkerScreenPreview() {
    KarigarTheme {
        ReviewWorkerScreen(
            workerName = "Ali Raza",
            onClose = {},
            onSubmit = { _, _ -> }
        )
    }
}
