package com.karigar.app.ui.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun WorkerFormScreen(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cnic by remember { mutableStateOf("") } // NEW FIELD
    var city by remember { mutableStateOf("") } // NEW FIELD
    var experience by remember { mutableStateOf("") }
    
    var expanded by remember { mutableStateOf(false) }
    var selectedWorkType by remember { mutableStateOf("None Selected") }
    val workTypes = listOf("Electrician", "Plumber", "Carpenter", "Painter", "Cleaner", "AC Repair")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Become a worker", fontWeight = FontWeight.Bold, color = TextPrimary) 
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
                label = "Full Name",
                value = fullName,
                onValueChange = { fullName = it },
                hint = "Enter full name"
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
                label = "CNIC Number",
                value = cnic,
                onValueChange = { cnic = it },
                hint = "Enter 13-digit CNIC",
                keyboardType = KeyboardType.Number
            )

            FormTextField(
                label = "City / Area",
                value = city,
                onValueChange = { city = it },
                hint = "Enter your city"
            )

            // Work Type Dropdown
            Text(
                text = "Work type",
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedWorkType,
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
                    workTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedWorkType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            FormTextField(
                label = "Years of experience",
                value = experience,
                onValueChange = { experience = it },
                hint = "Enter years of experience",
                keyboardType = KeyboardType.Number
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

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(
        text = label,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Preview
@Composable
fun WorkerFormScreenPreview() {
    KarigarTheme {
        WorkerFormScreen(onBackClick = {}, onSubmitClick = {})
    }
}
