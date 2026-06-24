package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendors")
data class VendorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String,
    val foodType: String, // "Veg", "Non-Veg", "Both"
    val menuToday: String,
    val weeklyMenuJson: String, // Calendar menu for the week
    val pricePerMeal: Double,
    val rating: Float,
    val distanceKm: Double,
    val deliveryTimeMinutes: Int,
    val deliveryRadiusKm: Double,
    val availabilityTiming: String,
    val reviewsCount: Int
)
