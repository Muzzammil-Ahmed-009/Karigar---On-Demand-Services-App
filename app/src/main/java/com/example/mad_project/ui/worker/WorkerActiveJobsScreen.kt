package com.karigar.app.ui.worker

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.data.model.Order

@Composable
fun WorkerActiveJobsScreen(
    activeJobs: List<Order>,
    onJobClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "My Active Jobs", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Black
                )
                Text(
                    text = "${activeJobs.size} ongoing tasks", 
                    fontSize = 14.sp, 
                    color = Color.Gray
                )
            }
        }

        if (activeJobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active jobs right now", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(activeJobs) { job ->
                    ActiveJobItemCard(job = job, onClick = { onJobClick(job.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveJobItemCard(job: Order, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = when (job.status) {
                        "Assigned" -> Color(0xFFE3F2FD) // Light Blue
                        "Confirmed", "On the Way" -> Color(0xFFFFF8E1) // Light Yellow
                        else -> Color(0xFFE8F5E9) // Light Green
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = job.status, 
                        color = when (job.status) {
                            "Assigned" -> Color(0xFF1976D2)
                            "Confirmed", "On the Way" -> Color(0xFFFFA000)
                            else -> Color(0xFF388E3C)
                        }, 
                        fontSize = 12.sp, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text("Order #ORD-${job.id.takeLast(6).uppercase()}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(job.categories.joinToString(", "), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Address: ${job.address}", fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Est. Fare: Rs ${job.offeredFare}", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun WorkerActiveJobsScreenPreview() {
    com.karigar.app.ui.theme.KarigarTheme {
        WorkerActiveJobsScreen(
            activeJobs = listOf(
                Order(id = "1", status = "Assigned", categories = listOf("Plumbing"), address = "Gulshan e Iqbal", offeredFare = "1500"),
                Order(id = "2", status = "On the Way", categories = listOf("Cleaning"), address = "DHA Phase 6", offeredFare = "2000")
            ),
            onJobClick = {}
        )
    }
}
