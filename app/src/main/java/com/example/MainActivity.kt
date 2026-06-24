package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.MessMateViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.MessMateOrange
import com.example.ui.theme.MessMateGreen

// State-driven routing structure
sealed class Screen {
    object Home : Screen()
    object Search : Screen()
    data class VendorDetails(val vendorId: Int) : Screen()
    data class OrderTracking(val orderId: Int) : Screen()
    object OrdersList : Screen()
    object Onboard : Screen()
    object More : Screen()
}

class MainActivity : ComponentActivity() {
    private val viewModel: MessMateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                // Handles system back presses gracefully
                BackHandler(enabled = currentScreen != Screen.Home) {
                    when (currentScreen) {
                        is Screen.VendorDetails -> currentScreen = Screen.Search
                        is Screen.OrderTracking -> currentScreen = Screen.OrdersList
                        else -> currentScreen = Screen.Home
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            val activeTab = when (currentScreen) {
                                Screen.Home -> 0
                                Screen.Search, is Screen.VendorDetails -> 1
                                Screen.OrdersList, is Screen.OrderTracking -> 2
                                Screen.Onboard -> 3
                                Screen.More -> 4
                            }

                            NavigationBarItem(
                                selected = activeTab == 0,
                                onClick = { currentScreen = Screen.Home },
                                label = { Text("Home") },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MessMateOrange,
                                    selectedTextColor = MessMateOrange,
                                    indicatorColor = Color(0xFFFFEDD5)
                                ),
                                modifier = Modifier.testTag("nav_home")
                            )

                            NavigationBarItem(
                                selected = activeTab == 1,
                                onClick = { currentScreen = Screen.Search },
                                label = { Text("Search") },
                                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MessMateOrange,
                                    selectedTextColor = MessMateOrange,
                                    indicatorColor = Color(0xFFFFEDD5)
                                ),
                                modifier = Modifier.testTag("nav_search")
                            )

                            NavigationBarItem(
                                selected = activeTab == 2,
                                onClick = { currentScreen = Screen.OrdersList },
                                label = { Text("Orders") },
                                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MessMateOrange,
                                    selectedTextColor = MessMateOrange,
                                    indicatorColor = Color(0xFFFFEDD5)
                                ),
                                modifier = Modifier.testTag("nav_orders")
                            )

                            NavigationBarItem(
                                selected = activeTab == 3,
                                onClick = { currentScreen = Screen.Onboard },
                                label = { Text("Register") },
                                icon = { Icon(Icons.Default.Kitchen, contentDescription = "Register Cook") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MessMateGreen,
                                    selectedTextColor = MessMateGreen,
                                    indicatorColor = Color(0xFFDCFCE7)
                                ),
                                modifier = Modifier.testTag("nav_register")
                            )

                            NavigationBarItem(
                                selected = activeTab == 4,
                                onClick = { currentScreen = Screen.More },
                                label = { Text("More") },
                                icon = { Icon(Icons.Default.Menu, contentDescription = "More Info") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MessMateOrange,
                                    selectedTextColor = MessMateOrange,
                                    indicatorColor = Color(0xFFFFEDD5)
                                ),
                                modifier = Modifier.testTag("nav_more")
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (val screen = currentScreen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToSearch = { currentScreen = Screen.Search },
                                    onNavigateToVendor = { vendorId ->
                                        viewModel.selectVendor(vendorId)
                                        currentScreen = Screen.VendorDetails(vendorId)
                                    },
                                    onNavigateToOnboard = { currentScreen = Screen.Onboard }
                                )
                            }

                            is Screen.Search -> {
                                SearchScreen(
                                    viewModel = viewModel,
                                    onNavigateToVendor = { vendorId ->
                                        viewModel.selectVendor(vendorId)
                                        currentScreen = Screen.VendorDetails(vendorId)
                                    }
                                )
                            }

                            is Screen.VendorDetails -> {
                                VendorDetailsScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = Screen.Search },
                                    onNavigateToTracking = { orderId ->
                                        viewModel.selectActiveOrder(orderId)
                                        currentScreen = Screen.OrderTracking(orderId)
                                    }
                                )
                            }

                            is Screen.OrderTracking -> {
                                // For tracking, we show the full Orders tab with live tracking on top
                                OrdersScreen(
                                    viewModel = viewModel,
                                    onNavigateToVendor = { vendorId ->
                                        viewModel.selectVendor(vendorId)
                                        currentScreen = Screen.VendorDetails(vendorId)
                                    }
                                )
                            }

                            is Screen.OrdersList -> {
                                OrdersScreen(
                                    viewModel = viewModel,
                                    onNavigateToVendor = { vendorId ->
                                        viewModel.selectVendor(vendorId)
                                        currentScreen = Screen.VendorDetails(vendorId)
                                    }
                                )
                            }

                            is Screen.Onboard -> {
                                OnboardScreen(
                                    viewModel = viewModel,
                                    onSuccess = {
                                        currentScreen = Screen.Search
                                    }
                                )
                            }

                            is Screen.More -> {
                                MoreScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

