package com.karigar.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextSecondary

enum class NavRole {
    CUSTOMER, WORKER
}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val id: Int // Used to map to old XML IDs
)

@Composable
fun FloatingBottomNav(
    currentRole: NavRole,
    selectedId: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = if (currentRole == NavRole.CUSTOMER) {
        listOf(
            NavItem("Home", Icons.Rounded.Home, 1),
            NavItem("Orders", Icons.Rounded.List, 2),
            NavItem("Wallet", Icons.Rounded.AccountBalanceWallet, 3),
            NavItem("Profile", Icons.Rounded.Person, 4)
        )
    } else {
        listOf(
            NavItem("Feed", Icons.Rounded.DynamicFeed, 5),
            NavItem("Jobs", Icons.Rounded.Work, 6),
            NavItem("Wallet", Icons.Rounded.AccountBalanceWallet, 3),
            NavItem("Profile", Icons.Rounded.Person, 4)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = TealPrimary.copy(alpha = 0.2f))
                .background(Color.White, RoundedCornerShape(32.dp))
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = selectedId == item.id
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isSelected) TealPrimary.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onItemSelected(item.id) }
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) TealPrimary else TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                        
                        AnimatedVisibility(visible = isSelected) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary
                                ),
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun FloatingBottomNavPreview() {
    com.karigar.app.ui.theme.KarigarTheme {
        FloatingBottomNav(
            currentRole = NavRole.CUSTOMER,
            selectedId = 1,
            onItemSelected = {}
        )
    }
}
