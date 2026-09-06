package com.karigar.app.ui.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karigar.app.ui.theme.KarigarTheme
import com.karigar.app.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyRegistrationFormScreen(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    var companyName by remember { mutableStateOf("") }
    var supervisorName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ntn by remember { mutableStateOf("") } // NEW FIELD
    var address by remember { mutableStateOf("") }
    
    var expanded by remember { mutableStateOf(false) }
    var selectedWorkerType by remember { mutableStateOf("None Selected") }
    val workerTypes = listOf("Electricians", "Plumbers", "Carpenters", "Painters", "Cleaners", "Multiple Services")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Register a company", fontWeight = FontWeight.Bold, color = TextPrimary) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            FormTextField(
                label = "Company Name",
                value = companyName,
                onValueChange = { companyName = it },
                hint = "Enter company name"
            )

            FormTextField(
                label = "Supervisor Name",
                value = supervisorName,
                onValueChange = { supervisorName = it },
                hint = "Enter supervisor name"
            )

            FormTextField(
                label = "Phone Number",
                value = phone,
                onValueChange = { phone = it },
                hint = "Enter phone number",
                keyboardType = KeyboardType.Phone
            )

            FormTextField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                hint = "Enter email address",
                keyboardType = KeyboardType.Email
            )

            FormTextField(
                label = "Company NTN / Registration No.",
                value = ntn,
                onValueChange = { ntn = it },
                hint = "Enter NTN (e.g. 1234567-8)",
                keyboardType = KeyboardType.Number
            )

            // Worker Type Dropdown
            Text(
                text = "Type of workers",
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedWorkerType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    workerTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedWorkerType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            FormTextField(
                label = "Address",
                value = address,
                onValueChange = { address = it },
                hint = "Enter your address"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSubmitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Submit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun CompanyRegistrationFormScreenPreview() {
    KarigarTheme {
        CompanyRegistrationFormScreen(onBackClick = {}, onSubmitClick = {})
    }
}
