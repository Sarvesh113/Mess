package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.ui.MessMateViewModel
import com.example.ui.theme.MessMateOrange
import com.example.ui.theme.MessMateOrangeLight
import com.example.ui.theme.MessMateGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: MessMateViewModel,
    onNavigateToVendor: (Int) -> Unit
) {
    val activeOrder by viewModel.activeOrder.collectAsState(initial = null)
    val allOrders by viewModel.allOrders.collectAsState(initial = emptyList())

    var showContactDialog by remember { mutableStateOf<Pair<String, String>?>(null) } // Name, Phone

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("orders_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Active Order / Delivery Tracker Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MessMateOrange)
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "My Tiffin Orders",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Track daily subscriptions & delivery history",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Active Order Card if present and NOT fully delivered (or let user track latest order anyway)
        val currentActiveOrder = activeOrder ?: allOrders.firstOrNull { it.status != "Delivered" }

        if (currentActiveOrder != null) {
            item {
                ActiveOrderTrackerCard(
                    order = currentActiveOrder,
                    onContactRider = { name, phone -> showContactDialog = Pair(name, phone) },
                    onContactVendor = { name, phone -> showContactDialog = Pair(name, phone) }
                )
            }
        }

        // Section Title: Order History
        item {
            Text(
                text = "Order & Subscription History",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp)
            )
        }

        if (allOrders.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "No orders",
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No orders placed yet",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Browse home kitchens and order your first healthy tiffin!",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(allOrders) { historicalOrder ->
                HistoricalOrderCard(
                    order = historicalOrder,
                    onOrderAgain = {
                        // Locate vendor and open or trigger order again
                        viewModel.selectVendor(historicalOrder.vendorId)
                        onNavigateToVendor(historicalOrder.vendorId)
                    }
                )
            }
        }
    }

    // Contact Dialog
    if (showContactDialog != null) {
        val contactInfo = showContactDialog!!
        AlertDialog(
            onDismissRequest = { showContactDialog = null },
            title = { Text("Contact ${contactInfo.first}") },
            text = {
                Column {
                    Text("Phone Number:")
                    Text(
                        text = contactInfo.second,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MessMateOrange,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text("Select contact method below:")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showContactDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MessMateOrange)
                ) {
                    Text("Call Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = null }) {
                    Text("WhatsApp")
                }
            }
        )
    }
}

@Composable
fun ActiveOrderTrackerCard(
    order: OrderEntity,
    onContactRider: (String, String) -> Unit,
    onContactVendor: (String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Vendor name, price, order type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "LIVE ORDER TRACKING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MessMateOrange
                    )
                    Text(
                        text = order.vendorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${order.planType} • ${order.menuDetails.take(28)}...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "₹${order.price.toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MessMateOrange
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            // Status stepper visualization
            OrderStatusTracker(status = order.status)

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            // ETA and Rider details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MessMateOrangeLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = "Rider",
                            tint = MessMateOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = order.deliveryBoyName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "Mess Mate Delivery Agent", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { onContactRider(order.deliveryBoyName, order.deliveryBoyPhone) },
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call rider", tint = MessMateOrange, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { onContactVendor(order.vendorName, order.vendorPhone) },
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = "Contact Vendor", tint = MessMateGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusTracker(status: String) {
    val steps = listOf("Confirmed", "Preparing", "Out for Delivery", "Delivered")
    val currentIndex = steps.indexOf(status).coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, step ->
                val isActive = index <= currentIndex
                val isCurrent = index == currentIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> MessMateOrange
                                    isActive -> MessMateGreen
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Default.Check
                                1 -> Icons.Default.Kitchen
                                2 -> Icons.Default.DeliveryDining
                                else -> Icons.Default.Home
                            },
                            contentDescription = step,
                            tint = if (isActive) Color.White else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = step,
                        fontSize = 9.sp,
                        fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (isActive) MaterialTheme.colorScheme.onSurface else Color.LightGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 11.sp,
                        maxLines = 2
                    )
                }

                // Draw connecting line except for last item
                if (index < steps.size - 1) {
                    val lineActive = index < currentIndex
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .height(3.dp)
                            .padding(bottom = 14.dp)
                            .background(
                                if (lineActive) MessMateGreen else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large textual message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when (status) {
                        "Confirmed" -> MessMateOrangeLight.copy(alpha = 0.3f)
                        "Preparing" -> MessMateOrangeLight.copy(alpha = 0.5f)
                        "Out for Delivery" -> MessMateGreen.copy(alpha = 0.1f)
                        else -> MessMateGreen.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            val trackingText = when (status) {
                "Confirmed" -> "🎉 Order confirmed! The kitchen is accepting your ticket."
                "Preparing" -> "👨‍🍳 Food is being freshly prepared with extreme hygiene."
                "Out for Delivery" -> "🚚 Out for delivery! Your rider is hot-footing to you."
                else -> "🍱 Fresh food delivered successfully. Enjoy your homestyle meal!"
            }
            Text(
                text = trackingText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (status == "Delivered") MessMateGreen else Color.DarkGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HistoricalOrderCard(
    order: OrderEntity,
    onOrderAgain: () -> Unit
) {
    val dateString = remember(order.orderTime) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(order.orderTime))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.vendorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = order.planType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MessMateOrange
                    )
                    Text(
                        text = dateString,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${order.price.toInt()}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(
                                if (order.status == "Delivered") MessMateGreen.copy(alpha = 0.15f) else MessMateOrangeLight,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = order.status,
                            color = if (order.status == "Delivered") MessMateGreen else MessMateOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Text(
                text = "Meal: ${order.menuDetails}",
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: #MM00${order.id}",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )

                OutlinedButton(
                    onClick = onOrderAgain,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Order Again", fontSize = 11.sp)
                }
            }
        }
    }
}
