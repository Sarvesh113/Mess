package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessMateDao {
    // Vendor Queries
    @Query("SELECT * FROM vendors ORDER BY rating DESC")
    fun getAllVendors(): Flow<List<VendorEntity>>

    @Query("SELECT * FROM vendors WHERE id = :id LIMIT 1")
    fun getVendorByIdFlow(id: Int): Flow<VendorEntity?>

    @Query("SELECT * FROM vendors WHERE id = :id LIMIT 1")
    suspend fun getVendorById(id: Int): VendorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: VendorEntity): Long

    @Query("DELETE FROM vendors")
    suspend fun clearVendors()

    // Order Queries
    @Query("SELECT * FROM orders ORDER BY orderTime DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderByIdFlow(id: Int): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: Int)
}
