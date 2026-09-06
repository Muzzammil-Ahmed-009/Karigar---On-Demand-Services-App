package com.karigar.app.ui.services

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.ui.components.KarigarPrimaryButton
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TealPrimary
import com.karigar.app.ui.theme.TextSecondary

class DetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KarigarTheme {
                DetailsScreen(
                    onBackClick = { finish() },
                    onSaveClick = { details ->
                        if (details.isNotBlank()) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("order_details", details.trim())
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        } else {
                            Toast.makeText(this, "Please enter some details", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String) -> Unit
) {
    var detailsText by remember { mutableStateOf("") }
    val maxChars = 500

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
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
                .padding(24.dp)
        ) {
            Text(
                text = "Describe your requirements",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Be as specific as possible so our professionals know exactly what you need.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = detailsText,
                onValueChange = {
                    if (it.length <= maxChars) {
                        detailsText = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = { Text("e.g. I need my 1.5 ton AC serviced and the gas refilled. It's on the 2nd floor.") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${detailsText.length}/$maxChars characters",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (detailsText.length == maxChars) MaterialTheme.colorScheme.error else TextSecondary
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Pro Tip Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Tip",
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = TealPrimary)) {
                                append("Pro Tip: ")
                            }
                            append("The more details you add, the better your service will be. Consider adding technical specs, deadlines, or specific preferences.")
                        },
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            KarigarPrimaryButton(
                text = "Save Details",
                onClick = { onSaveClick(detailsText) },
                modifier = Modifier.fillMaxWidth(),
                enabled = detailsText.isNotBlank()
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun DetailsPreview() {
    KarigarTheme {
        DetailsScreen(
            onBackClick = {},
            onSaveClick = {}
        )
    }
}
