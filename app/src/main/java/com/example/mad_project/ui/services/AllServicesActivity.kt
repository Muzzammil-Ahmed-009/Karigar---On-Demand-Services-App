package com.karigar.app.ui.services

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.R
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextSecondary

data class ServiceItem(
    val name: String,
    val description: String,
    val iconRes: Int,
    val iconColor: String,
    val categoryKey: String
)

class AllServicesActivity : ComponentActivity() {

    private val allServices = listOf(
        ServiceItem("Cleaning", "Deep clean your home, office or kitchen", R.drawable.cleaning, "#4CAF50", "cleaning"),
        ServiceItem("Electrician", "Wiring, sockets, fans, panels & more", R.drawable.electrician, "#FF9800", "electrician"),
        ServiceItem("Plumber", "Pipe repairs, leaks, taps & bathroom fitting", R.drawable.plumber, "#2196F3", "plumber"),
        ServiceItem("Carpenter", "Furniture repair, doors, windows & woodwork", R.drawable.carpenter, "#795548", "carpenter"),
        ServiceItem("Painter", "Interior & exterior wall painting", R.drawable.painter, "#E91E63", "painter"),
        ServiceItem("AC Repair", "AC service, installation & gas refilling", R.drawable.ac_repair, "#00BCD4", "ac_repair"),
        ServiceItem("Shifting", "Home & office moving & relocation service", R.drawable.shifting, "#9C27B0", "shifting"),
        ServiceItem("Gardening", "Lawn care, tree trimming & garden design", R.drawable.gardener, "#8BC34A", "gardener"),
        ServiceItem("Security Guard", "Trained guards for homes & businesses", R.drawable.security, "#607D8B", "security"),
        ServiceItem("Mechanic", "Car, bike & generator repair at home", R.drawable.repairing, "#FF5722", "mechanic"),
        ServiceItem("Pest Control", "Insects, termites & cockroach treatment", R.drawable.ic_pest_control, "#FFC107", "pest_control"),
        ServiceItem("Laundry", "Wash, dry-clean & press your clothes", R.drawable.ic_laundry, "#03A9F4", "laundry"),
        ServiceItem("Driver", "Dedicated driver for daily commute & trips", R.drawable.ic_driver, "#3F51B5", "driver"),
        ServiceItem("Cook / Chef", "Home-cooked meals & event catering", R.drawable.ic_cook, "#FF7043", "cook"),
        ServiceItem("Baby Sitter", "Trusted childcare at your home", R.drawable.ic_baby_sitter, "#F48FB1", "baby_sitter"),
        ServiceItem("Elder Care", "Nursing & assistance for elderly family members", R.drawable.ic_elder_care, "#78909C", "elder_care"),
        ServiceItem("Interior Design", "Modern home decor & renovation consultation", R.drawable.ic_interior, "#7E57C2", "interior"),
        ServiceItem("Solar Installation", "Solar panel setup & net metering guidance", R.drawable.ic_solar, "#FDD835", "solar"),
        ServiceItem("CCTV / Camera", "Security camera setup & monitoring", R.drawable.ic_cctv, "#546E7A", "cctv"),
        ServiceItem("Water Tanker", "Fresh water delivery to your location", R.drawable.ic_water_tanker, "#29B6F6", "water_tanker"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KarigarTheme {
                AllServicesScreen(
                    services = allServices,
                    onBackClick = { finish() },
                    onServiceClick = { service ->
                        val intent = Intent(this, PlaceOrderActivity::class.java)
                        intent.putExtra("category_name", service.name)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllServicesScreen(
    services: List<ServiceItem>,
    onBackClick: () -> Unit,
    onServiceClick: (ServiceItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredServices = if (searchQuery.isEmpty()) {
        services
    } else {
        services.filter { it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Services", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search services...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search", tint = TealPrimary) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Services List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredServices) { service ->
                    ServiceCard(service = service, onClick = { onServiceClick(service) })
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: ServiceItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconColor = try {
                Color(android.graphics.Color.parseColor(service.iconColor))
            } catch (e: Exception) {
                TealPrimary
            }

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = service.iconRes),
                    contentDescription = service.name,
                    modifier = Modifier.size(32.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconColor)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun AllServicesPreview() {
    KarigarTheme {
        val dummyServices = listOf(
            ServiceItem("Cleaning", "Deep clean your home", R.drawable.cleaning, "#4CAF50", "cleaning"),
            ServiceItem("Plumber", "Pipe repairs & leaks", R.drawable.plumber, "#2196F3", "plumber")
        )
        AllServicesScreen(
            services = dummyServices,
            onBackClick = {},
            onServiceClick = {}
        )
    }
}
