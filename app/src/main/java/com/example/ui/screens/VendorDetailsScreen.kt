package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VendorEntity
import com.example.ui.MessMateViewModel
import com.example.ui.theme.MessMateOrange
import com.example.ui.theme.MessMateOrangeLight
import com.example.ui.theme.MessMateGreen
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDetailsScreen(
    viewModel: MessMateViewModel,
    onBack: () -> Unit,
    onNavigateToTracking: (Int) -> Unit
) {
    val vendor by viewModel.selectedVendor.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()

    var selectedDayIndex by remember { mutableStateOf(0) }
    var selectedPlanTab by remember { mutableStateOf(0) } // 0 = Daily, 1 = Weekly, 2 = Monthly
    var showCallDialog by remember { mutableStateOf(false) }

    // List of weekdays
    val daysList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    if (vendor == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MessMateOrange)
        }
        return
    }

    val currentVendor = vendor!!

    // When an order gets placed, navigate to tracking
    LaunchedEffect(activeOrder) {
        if (activeOrder != null && activeOrder!!.vendorId == currentVendor.id && activeOrder!!.status != "Delivered") {
            onNavigateToTracking(activeOrder!!.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentVendor.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCallDialog = true }) {
                        Icon(Icons.Default.Call, contentDescription = "Call Vendor", tint = MessMateOrange)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Vendor Profile Banner Card
            item {
                VendorProfileHeaderCard(vendor = currentVendor)
            }

            // Today's Menu Section
            item {
                TodayMenuCard(
                    menu = currentVendor.menuToday,
                    onOrderClick = {
                        viewModel.placeOrder(currentVendor, "Single Meal")
                    }
                )
            }

            // Weekly Menu Calendar Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Weekly Menu Calendar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tap on a day to view scheduled meals",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Weekdays Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(daysList.size) { index ->
                            val isSelected = selectedDayIndex == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MessMateOrange else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedDayIndex = index }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = daysList[index].take(3),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Parse JSON weekly menu
                    val parsedMenu = remember(currentVendor.weeklyMenuJson, selectedDayIndex) {
                        try {
                            val jsonArray = JSONArray(currentVendor.weeklyMenuJson)
                            val selectedDayName = daysList[selectedDayIndex]
                            var foundLunch = "Not scheduled"
                            var foundDinner = "Not scheduled"
                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                if (obj.getString("day").equals(selectedDayName, ignoreCase = true)) {
                                    foundLunch = obj.optString("lunch", "Home Cooked Tiffin Lunch")
                                    foundDinner = obj.optString("dinner", "Home Cooked Tiffin Dinner")
                                    break
                                }
                            }
                            Pair(foundLunch, foundDinner)
                        } catch (e: Exception) {
                            Pair("Balanced Tiffin Lunch", "Balanced Tiffin Dinner")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MealTimeCard(
                            time = "LUNCH",
                            menu = parsedMenu.first,
                            icon = Icons.Default.LightMode,
                            modifier = Modifier.weight(1f)
                        )
                        MealTimeCard(
                            time = "DINNER",
                            menu = parsedMenu.second,
                            icon = Icons.Default.DarkMode,
                            tint = Color(0xFF3F51B5),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Subscription Pricing Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Subscription Plans",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Unlock heavy discounts with structured plans",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedPlanTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedPlanTab]),
                                color = MessMateOrange
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedPlanTab == 0,
                            onClick = { selectedPlanTab = 0 },
                            text = { Text("Daily", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedPlanTab == 1,
                            onClick = { selectedPlanTab = 1 },
                            text = { Text("Weekly", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedPlanTab == 2,
                            onClick = { selectedPlanTab = 2 },
                            text = { Text("Monthly", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedContent(targetState = selectedPlanTab, label = "") { tabIndex ->
                        when (tabIndex) {
                            0 -> PricingCard(
                                title = "🟢 Daily Plan",
                                priceText = "₹85 - ₹110 / Day",
                                features = listOf("Perfect for trying out", "Breakfast + Lunch + Dinner options", "Free delivery up to 3km", "No long-term commitment"),
                                onSubscribe = {
                                    viewModel.placeOrder(currentVendor, "Daily Plan", currentVendor.pricePerMeal)
                                }
                            )
                            1 -> PricingCard(
                                title = "🟡 Weekly Plan",
                                priceText = "₹550 - ₹750 / Week",
                                features = listOf("Saves ₹100 compared to daily", "Fixed rotating menu schedule", "Priority hot food delivery", "Pause / resume anytime"),
                                onSubscribe = {
                                    viewModel.placeOrder(currentVendor, "Weekly Plan", currentVendor.pricePerMeal * 6.5)
                                }
                            )
                            2 -> PricingCard(
                                title = "🔵 Monthly Plan",
                                priceText = "₹2200 - ₹2800 / Month",
                                features = listOf("Saves ₹500+ every month", "Custom diet requests honored", "Zero delivery charges forever", "Includes complimentary desserts on Sundays"),
                                onSubscribe = {
                                    viewModel.placeOrder(currentVendor, "Monthly Plan", currentVendor.pricePerMeal * 25.0)
                                }
                            )
                        }
                    }
                }
            }

            // Reviews & Ratings Summary
            item {
                ReviewsSection(rating = currentVendor.rating, reviewsCount = currentVendor.reviewsCount)
            }

            // Simulated Location Map
            item {
                SimulatedLocationMap(vendor = currentVendor)
            }
        }
    }

    // Call Vendor Dialog
    if (showCallDialog) {
        AlertDialog(
            onDismissRequest = { showCallDialog = false },
            title = { Text("Call ${currentVendor.name}") },
            text = {
                Column {
                    Text("Contact Number:")
                    Text(
                        text = currentVendor.phone,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MessMateOrange,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Availability: ${currentVendor.availabilityTiming}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCallDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MessMateOrange)
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCallDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun VendorProfileHeaderCard(vendor: VendorEntity) {
    Card(
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(MessMateOrangeLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = "Kitchen Icon",
                        tint = MessMateOrange,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isVeg = vendor.foodType == "Veg"
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isVeg) MessMateGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = vendor.foodType,
                                color = if (isVeg) MessMateGreen else Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "Stars", tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Text(text = " ${vendor.rating}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = " (${vendor.reviewsCount} reviews)", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Text(
                        text = vendor.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn(icon = Icons.Outlined.DirectionsRun, label = "Distance", value = "${vendor.distanceKm} km")
                InfoColumn(icon = Icons.Outlined.DeliveryDining, label = "Delivery Radius", value = "Up to ${vendor.deliveryRadiusKm.toInt()} km")
                InfoColumn(icon = Icons.Outlined.Timer, label = "Est. Time", value = "${vendor.deliveryTimeMinutes} mins")
            }

            Divider(modifier = Modifier.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = vendor.address,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Timings: ${vendor.availabilityTiming}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun InfoColumn(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = MessMateOrange, modifier = Modifier.size(18.dp))
        Text(text = label, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun TodayMenuCard(menu: String, onOrderClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MessMateOrangeLight.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        border = BorderStroke(1.dp, MessMateOrange.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Today, contentDescription = null, tint = MessMateOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Today's Special Menu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MessMateOrange
                    )
                }
                Box(
                    modifier = Modifier
                        .background(MessMateOrange, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("TODAY", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = menu,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOrderClick,
                colors = ButtonDefaults.buttonColors(containerColor = MessMateOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("details_order_now_button")
            ) {
                Text("Order Today's Meal Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MealTimeCard(
    time: String,
    menu: String,
    icon: ImageVector,
    tint: Color = MessMateOrange,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.heightIn(min = 120.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = time,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = tint
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = menu,
                fontSize = 11.sp,
                color = Color.DarkGray,
                lineHeight = 15.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PricingCard(
    title: String,
    priceText: String,
    features: List<String>,
    onSubscribe: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = priceText, fontWeight = FontWeight.Bold, color = MessMateOrange, fontSize = 16.sp)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Check",
                        tint = MessMateGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feature, fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSubscribe,
                colors = ButtonDefaults.buttonColors(containerColor = MessMateGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("Subscribe to Plan", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReviewsSection(rating: Float, reviewsCount: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ratings & Reviews",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = rating.toString(), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = MessMateOrange)
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating.toInt()) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(text = "$reviewsCount Ratings", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(modifier = Modifier.weight(1f)) {
                    RatingBarItem(stars = 5, progress = 0.8f)
                    RatingBarItem(stars = 4, progress = 0.15f)
                    RatingBarItem(stars = 3, progress = 0.05f)
                    RatingBarItem(stars = 2, progress = 0.0f)
                    RatingBarItem(stars = 1, progress = 0.0f)
                }
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Couple of mock reviews
            ReviewCommentItem(
                name = "Aman Verma (IIT Student)",
                stars = 5,
                comment = "Absolutely delicious! Tastes exactly like home-cooked meals. Not oily or spicy, perfect for daily hostel dinner.",
                date = "2 days ago"
            )
            ReviewCommentItem(
                name = "Sneha Reddy (Software Engineer)",
                stars = 4,
                comment = "Subscribed to their weekly plan and it's been great! Food always arrives on time and packaging is very clean.",
                date = "1 week ago"
            )
        }
    }
}

@Composable
fun RatingBarItem(stars: Int, progress: Float) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
        Text(text = "$stars★", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(20.dp))
        LinearProgressIndicator(
            progress = progress,
            color = MessMateOrange,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun ReviewCommentItem(name: String, stars: Int, comment: String, date: String) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = date, fontSize = 10.sp, color = Color.Gray)
        }
        Row(modifier = Modifier.padding(vertical = 2.dp)) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < stars) Color(0xFFFFB300) else Color.LightGray,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        Text(text = comment, fontSize = 11.sp, color = Color.DarkGray, lineHeight = 15.sp)
    }
}

@Composable
fun SimulatedLocationMap(vendor: VendorEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Kitchen Location Map",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Canvas drawing a clean simulated Google Map!
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F2F1))
            ) {
                // Draw simulated streets
                val width = size.width
                val height = size.height

                // Draw roads
                drawLine(Color.White, Offset(0f, height * 0.4f), Offset(width, height * 0.4f), strokeWidth = 30f)
                drawLine(Color.White, Offset(width * 0.3f, 0f), Offset(width * 0.3f, height), strokeWidth = 30f)
                drawLine(Color.White, Offset(width * 0.7f, 0f), Offset(width * 0.7f, height), strokeWidth = 20f)

                // Draw parks/rivers
                drawRect(Color(0xFFC8E6C9), Offset(10f, 10f), size = size.copy(width = width * 0.25f, height = height * 0.35f))
                drawRect(Color(0xFFB2DFDB), Offset(width * 0.75f, height * 0.5f), size = size.copy(width = width * 0.2f, height = height * 0.4f))

                // Draw hostel building location
                drawCircle(Color(0xFF90CAF9), radius = 25f, center = Offset(width * 0.3f, height * 0.8f))

                // Draw vendor kitchen pin (the heart center)
                drawCircle(Color.Red.copy(alpha = 0.2f), radius = 40f, center = Offset(width * 0.5f, height * 0.4f))
                drawCircle(Color.Red, radius = 12f, center = Offset(width * 0.5f, height * 0.4f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "📍 ${vendor.name} is verified on Mess Mate and is strictly operating within ${vendor.deliveryRadiusKm.toInt()} km.",
                fontSize = 11.sp,
                color = MessMateGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
