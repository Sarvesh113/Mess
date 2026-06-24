package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MessMateViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToVendor: (Int) -> Unit,
    onNavigateToOnboard: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val locationName by viewModel.locationName.collectAsState()
    val isDetectingLocation by viewModel.isDetectingLocation.collectAsState()
    val vendors by viewModel.filteredVendors.collectAsState()
    val vegOnly by viewModel.vegOnly.collectAsState()
    val maxPrice by viewModel.maxPrice.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero Section
        item {
            HeroSection(
                locationName = locationName,
                isDetectingLocation = isDetectingLocation,
                searchQuery = searchQuery,
                vegOnly = vegOnly,
                maxPrice = maxPrice,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onVegOnlyChange = { viewModel.setVegOnly(it) },
                onMaxPriceChange = { viewModel.setMaxPrice(it) },
                onDetectLocation = { viewModel.detectLocation() },
                onFindFoodClick = onNavigateToSearch
            )
        }

        // Features Section (4 Cards)
        item {
            FeaturesSection()
        }

        // How It Works Section
        item {
            HowItWorksSection()
        }

        // Quick Nearby Recommendations
        item {
            SectionHeader(
                title = "Popular Tiffin Services Nearby",
                subtitle = "Handpicked healthy home kitchens",
                actionText = "View All",
                onActionClick = onNavigateToSearch
            )
        }

        if (vendors.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tiffin services matching your filters in $locationName",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(vendors.take(3)) { vendor ->
                RecommendedVendorCard(
                    vendor = vendor,
                    onClick = { onNavigateToVendor(vendor.id) }
                )
            }
        }

        // Call to action banner for cooks
        item {
            VendorCallToActionBanner(onClick = onNavigateToOnboard)
        }
    }
}

@Composable
fun HeroSection(
    locationName: String,
    isDetectingLocation: Boolean,
    searchQuery: String,
    vegOnly: Boolean,
    maxPrice: Float,
    onSearchQueryChange: (String) -> Unit,
    onVegOnlyChange: (Boolean) -> Unit,
    onMaxPriceChange: (Float) -> Unit,
    onDetectLocation: () -> Unit,
    onFindFoodClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDFCFB))
            .padding(bottom = 12.dp)
    ) {
        // Header Section: Location & Profile
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDetectLocation() }
            ) {
                Text(
                    text = "DELIVERING TO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEA580C),
                    letterSpacing = 1.5.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = if (isDetectingLocation) "Locating..." else locationName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Location",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Profile Avatar Badge "RS" in orange accent container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEDD5))
                    .border(1.dp, Color(0xFFFED7AA), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RS",
                    color = Color(0xFFEA580C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Search Bar in Slate theme
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Search home cooks, tiffins...",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color(0xFF94A3B8)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = Color(0xFF94A3B8))
                        }
                    } else {
                        IconButton(onClick = onDetectLocation) {
                            Icon(Icons.Default.MyLocation, contentDescription = "Detect Location", tint = Color(0xFFEA580C))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    disabledContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color(0xFFE2E8F0),
                    unfocusedIndicatorColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_bar")
            )
        }

        // Category Chips (Horizontal Scroll)
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chip 1: All Meals
            item {
                CategoryChip(
                    text = "All Meals",
                    isSelected = !vegOnly && maxPrice > 150f,
                    onClick = {
                        onVegOnlyChange(false)
                        onMaxPriceChange(200f)
                    }
                )
            }
            // Chip 2: Veg Only
            item {
                CategoryChip(
                    text = "Veg Only",
                    isSelected = vegOnly,
                    onClick = {
                        onVegOnlyChange(!vegOnly)
                    }
                )
            }
            // Chip 3: Under ₹100
            item {
                CategoryChip(
                    text = "Under ₹100",
                    isSelected = maxPrice <= 100f,
                    onClick = {
                        if (maxPrice <= 100f) onMaxPriceChange(200f) else onMaxPriceChange(100f)
                    }
                )
            }
            // Chip 4: Monthly Plans
            item {
                CategoryChip(
                    text = "Monthly Plans",
                    isSelected = searchQuery.contains("Plan", ignoreCase = true),
                    onClick = {
                        if (searchQuery.contains("Plan", ignoreCase = true)) {
                            onSearchQueryChange("")
                        } else {
                            onSearchQueryChange("Plan")
                        }
                    }
                )
            }
        }

        // Hero Banner: Orange Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFF97316), Color(0xFFEA580C)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .testTag("hero_banner_container")
        ) {
            // Overlapping Abstract Decorative Circles (Sleek design)
            Canvas(
                modifier = Modifier
                    .matchParentSize()
            ) {
                // Bottom right decorative circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = 70.dp.toPx(),
                    center = Offset(this.size.width + 10.dp.toPx(), this.size.height + 10.dp.toPx())
                )
                // Top right smaller decorative circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = 36.dp.toPx(),
                    center = Offset(this.size.width - 40.dp.toPx(), 30.dp.toPx())
                )
            }

            // Banner Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Homemade food\nstarting at ₹75",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 24.sp
                )
                Text(
                    text = "Subscription plans for students",
                    fontSize = 11.sp,
                    color = Color(0xFFFFEDD5),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Button(
                    onClick = onFindFoodClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .testTag("find_food_button")
                ) {
                    Text(
                        text = "Find Near Me",
                        color = Color(0xFFEA580C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = if (isSelected) Color(0xFFEA580C) else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF0F172A),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}


@Composable
fun FeaturesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Why Mess Mate?",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeatureCard(
                icon = Icons.Outlined.Kitchen,
                title = "Nearby Tiffin",
                description = "Discover local home kitchens nearby",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = Icons.Outlined.DeliveryDining,
                title = "Fast Delivery",
                description = "Meals delivered hot in 30–60 mins",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeatureCard(
                icon = Icons.Outlined.Verified,
                title = "Verified Cooks",
                description = "Hygienic tiffins with high ratings",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = Icons.Outlined.Paid,
                title = "Affordable",
                description = "Daily, weekly, or monthly food plans",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MessMateOrangeLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MessMateOrange,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HowItWorksSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = "How It Works",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            StepItem(number = "1", title = "Enable GPS", desc = "Turn on location", modifier = Modifier.weight(1f))
            StepArrow()
            StepItem(number = "2", title = "Find Cooks", desc = "Browse tiffins", modifier = Modifier.weight(1f))
            StepArrow()
            StepItem(number = "3", title = "Choose Plan", desc = "Daily or sub", modifier = Modifier.weight(1f))
            StepArrow()
            StepItem(number = "4", title = "Get Food", desc = "Home delivered", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StepItem(number: String, title: String, desc: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MessMateOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = desc,
            fontSize = 9.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StepArrow() {
    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .size(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = MessMateOrange,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = actionText,
            color = MessMateOrange,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable { onActionClick() }
                .padding(4.dp)
        )
    }
}

@Composable
fun RecommendedVendorCard(
    vendor: VendorEntity,
    onClick: () -> Unit
) {
    val isVeg = vendor.foodType == "Veg"
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Type Badge and Icon placeholder (using gorgeous emoji as per the Sleek Interface card)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isVeg) Color(0xFFDCFCE7) else Color(0xFFFFEDD5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isVeg) "🍱" else "🍛",
                    fontSize = 32.sp
                )
                // Active status dot in top-right corner
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .clip(CircleShape)
                        .background(if (isVeg) Color(0xFF22C55E) else Color(0xFFF97316))
                        .border(1.5.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = vendor.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF08A))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFEAB308),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = vendor.rating.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF854D0E)
                        )
                    }
                }

                Text(
                    text = vendor.menuToday,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                ) {
                    // Veg badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isVeg) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = vendor.foodType,
                            color = if (isVeg) Color(0xFF15803D) else Color(0xFFB91C1C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Distance",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${vendor.distanceKm} km",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Delivery Time",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${vendor.deliveryTimeMinutes} mins",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${vendor.pricePerMeal.toInt()} per meal",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFFEA580C)
                    )
                    Text(
                        text = "Free Delivery",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF16A34A)
                    )
                }
            }
        }
    }
}

@Composable
fun VendorCallToActionBanner(
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MessMateOrangeLight.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Are you a Home Cook?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MessMateOrange
                )
                Text(
                    text = "Onboard your kitchen and start serving students near you.",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = MessMateOrange),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Join Us", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
