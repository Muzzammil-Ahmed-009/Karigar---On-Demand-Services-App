package com.karigar.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration // <-- 1. NEW IMPORT
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.R
import com.karigar.app.ui.theme.TealPrimary

@Composable
fun KarigarDrawer(
    onCloseClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var isWorkerMode by remember { mutableStateOf(false) }

    // <-- 2. Screen ki width calculate kar rahe hain
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // <-- 3. ModalDrawerSheet use kar ke max-width ki limit override kar rahe hain
    ModalDrawerSheet(
        modifier = Modifier.width(screenWidth * 0.91f),
        drawerContainerColor = Color.White,
        drawerShape = DrawerDefaults.shape // Agar right side se golai nahi chahiye to RectangleShape use kar lena
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth() // <-- Yahan se 0.91f hata diya hai, ab yeh parent (ModalDrawerSheet) ki poori width lega
                .background(Color.White)
        ) {
            // Drawer Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(TealPrimary)
            ) {
                // Close Button
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 32.dp, end = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_karigar),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 18.dp, bottom = 13.dp)
                        .height(110.dp)
                        .fillMaxWidth(0.4f),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Menu Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                DrawerMenuItem(
                    icon = Icons.Rounded.Person,
                    title = "Become a Worker",
                    onClick = { onNavigate("BecomeWorker") }
                )
                DrawerMenuItem(
                    icon = Icons.Rounded.Business,
                    title = "Register a Company",
                    onClick = { onNavigate("RegisterCompany") }
                )
                DrawerMenuItem(
                    icon = Icons.Rounded.HeadsetMic,
                    title = "Contact Us",
                    onClick = { onNavigate("ContactUs") }
                )
                DrawerMenuItem(
                    icon = Icons.Rounded.Settings,
                    title = "Settings",
                    onClick = { onNavigate("Settings") }
                )
                DrawerMenuItem(
                    icon = Icons.Rounded.Share,
                    title = "Share",
                    onClick = { onNavigate("Share") }
                )
                DrawerMenuItem(
                    icon = Icons.Rounded.StarRate,
                    title = "Rate Us",
                    onClick = { onNavigate("RateUs") }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // Footer Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                // Flat Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isWorkerMode) "Worker Mode" else "Customer Mode",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    Switch(
                        checked = isWorkerMode,
                        onCheckedChange = { isWorkerMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TealPrimary,
                            checkedTrackColor = TealPrimary.copy(alpha = 0.5f)
                        )
                    )
                }

                DrawerMenuItem(
                    icon = Icons.Rounded.Logout,
                    title = "Log Out",
                    onClick = { onNavigate("LogOut") },
                    isDestructive = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "v1.0.0",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val color = if (isDestructive) Color(0xFFD32F2F) else Color.DarkGray
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KarigarDrawerPreview() {
    MaterialTheme {
        KarigarDrawer(onCloseClick = {}, onNavigate = {})
    }
}