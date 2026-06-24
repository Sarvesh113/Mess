package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vendorId: Int,
    val vendorName: String,
    val vendorPhone: String,
    val planType: String, // "Single Meal", "Daily", "Weekly", "Monthly"
    val menuDetails: String,
    val price: Double,
    val status: String, // "Confirmed", "Preparing", "Out for Delivery", "Delivered"
    val orderTime: Long = System.currentTimeMillis(),
    val deliveryBoyName: String = "Rahul Sharma",
    val deliveryBoyPhone: String = "+91 98765 43210",
    val estimatedMinutes: Int = 35
)
