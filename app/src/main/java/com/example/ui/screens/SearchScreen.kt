package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun SearchScreen(
    viewModel: MessMateViewModel,
    onNavigateToVendor: (Int) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val vegOnly by viewModel.vegOnly.collectAsState()
    val distanceRange by viewModel.distanceRange.collectAsState()
    val maxPrice by viewModel.maxPrice.collectAsState()
    val minRating by viewModel.minRating.collectAsState()
    val filteredVendors by viewModel.filteredVendors.collectAsState()
    val locationName by viewModel.locationName.collectAsState()

    var showFiltersSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFCFB)) // Sleek Background
            .testTag("search_screen")
    ) {
        // Top Sticky Search bar & Filter Trigger
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Discover Tiffin Services",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search tiffin, dish, chef...", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF94A3B8)) },
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
                        .weight(1f)
                        .testTag("search_input_field")
                )

                // Filter Button with Badge
                val activeFiltersCount = (if (vegOnly) 1 else 0) +
                        (if (distanceRange < 10f) 1 else 0) +
                        (if (maxPrice < 200f) 1 else 0) +
                        (if (minRating > 3.0f) 1 else 0)

                Box {
                    IconButton(
                        onClick = { showFiltersSheet = true },
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                color = if (activeFiltersCount > 0) Color(0xFFFFEDD5) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (activeFiltersCount > 0) Color(0xFFFED7AA) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("filter_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filters",
                            tint = if (activeFiltersCount > 0) Color(0xFFEA580C) else Color(0xFF0F172A)
                        )
                    }

                    if (activeFiltersCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFFEF4444), RoundedCornerShape(9.dp))
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeFiltersCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Filters scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Veg Filter chip
                CategoryChip(
                    text = "Veg Only",
                    isSelected = vegOnly,
                    onClick = { viewModel.setVegOnly(!vegOnly) }
                )

                // Quick Budget Filter chip
                CategoryChip(
                    text = "Budget (Under ₹100)",
                    isSelected = maxPrice <= 100f,
                    onClick = { viewModel.setMaxPrice(if (maxPrice <= 100f) 200f else 100f) }
                )

                // Quick Top Rated Filter chip
                CategoryChip(
                    text = "Top Rated (4.5⭐+)",
                    isSelected = minRating >= 4.5f,
                    onClick = { viewModel.setMinRating(if (minRating >= 4.5f) 3.0f else 4.5f) }
                )
            }
        }

        // Location Info Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFEDD5))
                .border(width = (0.5).dp, color = Color(0xFFFED7AA).copy(alpha = 0.5f))
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Location", tint = Color(0xFFEA580C), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Showing results near $locationName",
                fontSize = 12.sp,
                color = Color(0xFFEA580C),
                fontWeight = FontWeight.Bold
            )
        }

        // Vendor List
        if (filteredVendors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "No Results",
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No vendors match your search or filters.",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Try adjusting filters or searching for something else.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.updateSearchQuery("")
                            viewModel.setVegOnly(false)
                            viewModel.setDistanceRange(10f)
                            viewModel.setMaxPrice(200f)
                            viewModel.setMinRating(3.0f)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MessMateOrange)
                    ) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp)
            ) {
                items(filteredVendors) { vendor ->
                    SearchVendorCard(
                        vendor = vendor,
                        onViewDetails = { onNavigateToVendor(vendor.id) },
                        onOrderNow = { viewModel.placeOrder(vendor, "Single Meal") }
                    )
                }
            }
        }
    }

    // Bottom Sheet Dialog for filters
    if (showFiltersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFiltersSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filters",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Clear All",
                        color = MessMateOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable {
                                viewModel.setVegOnly(false)
                                viewModel.setDistanceRange(10f)
                                viewModel.setMaxPrice(200f)
                                viewModel.setMinRating(3.0f)
                            }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Food Type Filter
                Text(
                    text = "Dietary Preference",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.setVegOnly(true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (vegOnly) MessMateGreen else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Veg Only",
                            color = if (vegOnly) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { viewModel.setVegOnly(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!vegOnly) MessMateOrange else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Veg & Non-Veg",
                            color = if (!vegOnly) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Distance Range Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Distance Radius",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Within ${distanceRange.toInt()} km",
                        fontWeight = FontWeight.Bold,
                        color = MessMateOrange,
                        fontSize = 14.sp
                    )
                }
                Slider(
                    value = distanceRange,
                    onValueChange = { viewModel.setDistanceRange(it) },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MessMateOrange,
                        thumbColor = MessMateOrange
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Price Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Max Budget per Meal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Up to ₹${maxPrice.toInt()}",
                        fontWeight = FontWeight.Bold,
                        color = MessMateOrange,
                        fontSize = 14.sp
                    )
                }
                Slider(
                    value = maxPrice,
                    onValueChange = { viewModel.setMaxPrice(it) },
                    valueRange = 50f..200f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MessMateOrange,
                        thumbColor = MessMateOrange
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rating Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Minimum Vendor Rating",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${minRating}⭐ & above",
                        fontWeight = FontWeight.Bold,
                        color = MessMateOrange,
                        fontSize = 14.sp
                    )
                }
                Slider(
                    value = minRating,
                    onValueChange = { viewModel.setMinRating(it) },
                    valueRange = 3.0f..4.9f,
                    steps = 18,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MessMateOrange,
                        thumbColor = MessMateOrange
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showFiltersSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MessMateOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_filters_button")
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun SearchVendorCard(
    vendor: VendorEntity,
    onViewDetails: () -> Unit,
    onOrderNow: () -> Unit
) {
    val isVeg = vendor.foodType == "Veg"
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onViewDetails() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Image/Logo Column using gorgeous emoji container
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isVeg) Color(0xFFDCFCE7) else Color(0xFFFFEDD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isVeg) "🍱" else "🍛",
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Details Column
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

                        // Rating Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEF08A))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFEAB308),
                                modifier = Modifier.size(12.dp)
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

                    // Location/Delivery distance stats
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (isVeg) Color(0xFF22C55E) else Color(0xFFEF4444), RoundedCornerShape(1.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = vendor.foodType,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVeg) Color(0xFF15803D) else Color(0xFFB91C1C)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "${vendor.distanceKm} km away", fontSize = 11.sp, color = Color(0xFF64748B))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Timer, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "${vendor.deliveryTimeMinutes} mins", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }

                    // Menu summary
                    Text(
                        text = "Today: ${vendor.menuToday}",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Price per Meal",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "₹${vendor.pricePerMeal.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFEA580C)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Details", fontSize = 12.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onOrderNow() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("vendor_order_now_button")
                    ) {
                        Text("Order Now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
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
        shadowElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF0F172A),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

