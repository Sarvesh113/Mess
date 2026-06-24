package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MessMateViewModel
import com.example.ui.theme.MessMateOrange
import com.example.ui.theme.MessMateOrangeLight
import com.example.ui.theme.MessMateGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardScreen(
    viewModel: MessMateViewModel,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var foodType by remember { mutableStateOf("Veg") } // Veg, Non-Veg, Both
    var menuToday by remember { mutableStateOf("") }
    var pricePerMeal by remember { mutableStateOf("90") }
    var deliveryRadius by remember { mutableStateOf(5.0f) }
    var availabilityTiming by remember { mutableStateOf("11:30 AM - 3:00 PM, 7:00 PM - 10:00 PM") }

    var showError by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("onboard_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MessMateGreen)
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Outlined.Kitchen,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kitchen Onboarding",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Join Mess Mate & start serving healthy meals near you",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Onboarding Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Become a Mess Mate Vendor",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Kitchen Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Kitchen / Cook Name") },
                            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("onboard_name")
                        )

                        // Phone Number
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Contact Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("onboard_phone")
                        )

                        // Address
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Kitchen Address") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            singleLine = false,
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("onboard_address")
                        )

                        // Food Type Switcher
                        Text(
                            text = "Food Category Served",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Veg", "Non-Veg", "Both").forEach { type ->
                                val isSelected = foodType == type
                                Button(
                                    onClick = { foodType = type },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MessMateOrange else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = type,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Today's Menu
                        OutlinedTextField(
                            value = menuToday,
                            onValueChange = { menuToday = it },
                            label = { Text("Today's Menu description") },
                            placeholder = { Text("e.g. Kadhi Pakoda, Jeera Rice, 3 Chapati, Salad") },
                            leadingIcon = { Icon(Icons.Default.RestaurantMenu, contentDescription = null) },
                            singleLine = false,
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("onboard_menu")
                        )

                        // Price setup
                        OutlinedTextField(
                            value = pricePerMeal,
                            onValueChange = { pricePerMeal = it },
                            label = { Text("Price per Meal (₹)") },
                            leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("onboard_price")
                        )

                        // Availability timing
                        OutlinedTextField(
                            value = availabilityTiming,
                            onValueChange = { availabilityTiming = it },
                            label = { Text("Availability timings") },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )

                        // Delivery radius
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Delivery Radius", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "${deliveryRadius.toInt()} km", fontWeight = FontWeight.Bold, color = MessMateGreen)
                        }
                        Slider(
                            value = deliveryRadius,
                            onValueChange = { deliveryRadius = it },
                            valueRange = 1.0f..10.0f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                activeTrackColor = MessMateGreen,
                                thumbColor = MessMateGreen
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (showError) {
                            Text(
                                text = "Please fill in all details correctly before submitting.",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        Button(
                            onClick = {
                                val priceVal = pricePerMeal.toDoubleOrNull()
                                if (name.isBlank() || phone.isBlank() || address.isBlank() || menuToday.isBlank() || priceVal == null) {
                                    showError = true
                                } else {
                                    showError = false
                                    viewModel.registerVendor(
                                        name = name,
                                        phone = phone,
                                        address = address,
                                        foodType = foodType,
                                        menuToday = menuToday,
                                        pricePerMeal = priceVal,
                                        deliveryRadius = deliveryRadius.toDouble(),
                                        availabilityTiming = availabilityTiming
                                    )
                                    showSuccessModal = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MessMateGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("become_vendor_submit_button")
                        ) {
                            Text("Become a Mess Mate Vendor", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // Onboarding Success dialog
        if (showSuccessModal) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessModal = false
                    onSuccess()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MessMateGreen,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = { Text("Kitchen Registered!") },
                text = {
                    Text(
                        text = "Congratulations! Your home kitchen is now live. Nearby students can view your daily menu and place subscriptions immediately.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessModal = false
                            onSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MessMateGreen)
                    ) {
                        Text("View My Kitchen")
                    }
                }
            )
        }
    }
}
