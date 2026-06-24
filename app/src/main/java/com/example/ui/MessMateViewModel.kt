package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MessMateDatabase
import com.example.data.MessMateRepository
import com.example.data.OrderEntity
import com.example.data.VendorEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessMateViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MessMateDatabase.getDatabase(application)
    private val repository = MessMateRepository(database.dao())

    // All vendors and orders from Room
    val allVendors = repository.allVendors
    val allOrders = repository.allOrders

    // Search and filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _vegOnly = MutableStateFlow(false)
    val vegOnly = _vegOnly.asStateFlow()

    private val _distanceRange = MutableStateFlow(10f) // Max 10km
    val distanceRange = _distanceRange.asStateFlow()

    private val _maxPrice = MutableStateFlow(200f) // Max ₹200
    val maxPrice = _maxPrice.asStateFlow()

    private val _minRating = MutableStateFlow(3.0f) // Min Rating
    val minRating = _minRating.asStateFlow()

    // Location state
    private val _locationName = MutableStateFlow("IIT Delhi Hostel Area")
    val locationName = _locationName.asStateFlow()

    private val _isDetectingLocation = MutableStateFlow(false)
    val isDetectingLocation = _isDetectingLocation.asStateFlow()

    // Filtered vendors combined state
    val filteredVendors: StateFlow<List<VendorEntity>> = combine(
        allVendors,
        _searchQuery,
        _vegOnly,
        _distanceRange,
        combine(_maxPrice, _minRating) { price, rating -> Pair(price, rating) }
    ) { vendorsList, query, veg, distance, priceAndRating ->
        val (price, rating) = priceAndRating
        vendorsList.filter { vendor ->
            val matchesQuery = vendor.name.contains(query, ignoreCase = true) ||
                    vendor.menuToday.contains(query, ignoreCase = true) ||
                    vendor.address.contains(query, ignoreCase = true)
            
            val matchesVeg = if (veg) vendor.foodType == "Veg" else true
            val matchesDistance = vendor.distanceKm <= distance
            val matchesPrice = vendor.pricePerMeal <= price
            val matchesRating = vendor.rating >= rating

            matchesQuery && matchesVeg && matchesDistance && matchesPrice && matchesRating
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently viewed vendor
    private val _selectedVendorId = MutableStateFlow<Int?>(null)
    val selectedVendor: StateFlow<VendorEntity?> = _selectedVendorId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getVendorByIdFlow(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active order for tracking page
    private val _activeOrderId = MutableStateFlow<Int?>(null)
    val activeOrder: StateFlow<OrderEntity?> = _activeOrderId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getOrderByIdFlow(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Prepopulate with sample data on startup
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    fun selectVendor(id: Int) {
        _selectedVendorId.value = id
    }

    fun selectActiveOrder(id: Int) {
        _activeOrderId.value = id
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setVegOnly(veg: Boolean) {
        _vegOnly.value = veg
    }

    fun setDistanceRange(distance: Float) {
        _distanceRange.value = distance
    }

    fun setMaxPrice(price: Float) {
        _maxPrice.value = price
    }

    fun setMinRating(rating: Float) {
        _minRating.value = rating
    }

    fun detectLocation() {
        viewModelScope.launch {
            _isDetectingLocation.value = true
            delay(1200) // Simulate GPS delay
            _locationName.value = "Sector 4, Rohini Hostel Complex"
            _isDetectingLocation.value = false
        }
    }

    // Place normal single-meal order
    fun placeOrder(vendor: VendorEntity, planType: String = "Single Meal", priceOverride: Double? = null) {
        viewModelScope.launch {
            val price = priceOverride ?: vendor.pricePerMeal
            val newOrder = OrderEntity(
                vendorId = vendor.id,
                vendorName = vendor.name,
                vendorPhone = vendor.phone,
                planType = planType,
                menuDetails = vendor.menuToday,
                price = price,
                status = "Confirmed"
            )
            val orderId = repository.insertOrder(newOrder).toInt()
            _activeOrderId.value = orderId
            
            // Start simulation of order tracking lifecycle!
            simulateOrderProgress(orderId)
        }
    }

    // Become a Mess Mate Vendor onboarding
    fun registerVendor(
        name: String,
        phone: String,
        address: String,
        foodType: String,
        menuToday: String,
        pricePerMeal: Double,
        deliveryRadius: Double,
        availabilityTiming: String
    ) {
        viewModelScope.launch {
            val newVendor = VendorEntity(
                name = name,
                phone = phone,
                address = address,
                foodType = foodType,
                menuToday = menuToday,
                weeklyMenuJson = """[
                    {"day": "Monday", "lunch": "$menuToday", "dinner": "$menuToday"},
                    {"day": "Tuesday", "lunch": "$menuToday", "dinner": "$menuToday"},
                    {"day": "Wednesday", "lunch": "$menuToday", "dinner": "$menuToday"},
                    {"day": "Thursday", "lunch": "$menuToday", "dinner": "$menuToday"},
                    {"day": "Friday", "lunch": "$menuToday", "dinner": "$menuToday"},
                    {"day": "Saturday", "lunch": "$menuToday", "dinner": "$menuToday"},
                    {"day": "Sunday", "lunch": "$menuToday", "dinner": "$menuToday"}
                ]""",
                pricePerMeal = pricePerMeal,
                rating = 4.5f,
                distanceKm = 0.5 + (Math.random() * 2.0), // generate a random distance close by
                deliveryTimeMinutes = 20 + (Math.random() * 20).toInt(),
                deliveryRadiusKm = deliveryRadius,
                availabilityTiming = availabilityTiming,
                reviewsCount = 1
            )
            repository.insertVendor(newVendor)
        }
    }

    // Simulator: Updates status step-by-step
    private fun simulateOrderProgress(orderId: Int) {
        viewModelScope.launch {
            // Step 1: Confirmed -> Preparing after 8 seconds
            delay(8000)
            repository.updateOrderStatus(orderId, "Preparing")
            
            // Step 2: Preparing -> Out for Delivery after 12 seconds
            delay(12000)
            repository.updateOrderStatus(orderId, "Out for Delivery")
            
            // Step 3: Out for Delivery -> Delivered after 15 seconds
            delay(15000)
            repository.updateOrderStatus(orderId, "Delivered")
        }
    }
}
