package com.karigar.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.HeadsetMic
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.R
import com.karigar.app.data.model.User
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextPrimary
import com.karigar.app.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    user: User?,
    totalOrders: Int,
    completedOrders: Int,
    pendingOrders: Int,
    onEditNameClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSwitchRoleClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
    ) {
        // Header Profile Section (Full Width, Teal Background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = TealPrimary,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(top = 48.dp, bottom = 32.dp), // Padding for content
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image with a ring
                val profilePainter = painterResource(id = R.drawable.emp_profile)
                
                Image(
                    painter = profilePainter,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(110.dp)
                        .border(3.dp, Color.White.copy(alpha = 0.8f), CircleShape) // Nice ring
                        .padding(3.dp) // Space between ring and image
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user?.name?.ifEmpty { "Guest User" } ?: "Guest User",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = user?.phone?.ifEmpty { "No phone number" } ?: "No phone number",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Padded content container for the rest of the screen
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Stats Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), title = "Total", value = totalOrders.toString(), color = TealPrimary)
                StatCard(modifier = Modifier.weight(1f), title = "Completed", value = completedOrders.toString(), color = Color(0xFF2E7D32))
                StatCard(modifier = Modifier.weight(1f), title = "Pending", value = pendingOrders.toString(), color = Color(0xFFE65100))
            }

            Spacer(modifier = Modifier.height(32.dp))

        // Actions Section
        Text(
            text = "Settings & Preferences",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                ActionRow(
                    icon = Icons.Rounded.Person,
                    title = "Edit Profile",
                    onClick = onEditProfileClick
                )
                Divider(color = Color(0xFFEEEEEE))
                ActionRow(
                    icon = Icons.Rounded.SwapHoriz,
                    title = if (user?.currentRole == "worker") "Switch to Customer Mode" else "Switch to Worker Mode",
                    onClick = onSwitchRoleClick
                )
                Divider(color = Color(0xFFEEEEEE))
                ActionRow(
                    icon = Icons.Rounded.HeadsetMic,
                    title = "Contact Us",
                    onClick = onContactUsClick
                )
                Divider(color = Color(0xFFEEEEEE))
                ActionRow(
                    icon = Icons.Rounded.Logout,
                    title = "Log Out",
                    textColor = Color(0xFFC62828),
                    iconColor = Color(0xFFC62828),
                    onClick = onLogoutClick
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        } // End of padded column
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    com.karigar.app.ui.theme.KarigarTheme {
        ProfileScreen(
            user = com.karigar.app.data.model.User(
                id = "1",
                name = "Ali Ahmed",
                phone = "+92 300 1234567",
                email = "ali@example.com",
                city = "Lahore",
                gender = "Male"
            ),
            totalOrders = 15,
            completedOrders = 10,
            pendingOrders = 5,
            onEditNameClick = {},
            onEditProfileClick = {},
            onSwitchRoleClick = {},
            onContactUsClick = {},
            onLogoutClick = {}
        )
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ActionRow(
    icon: ImageVector,
    title: String,
    textColor: Color = TextPrimary,
    iconColor: Color = TextSecondary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
