package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MessMateRepository(private val dao: MessMateDao) {
    val allVendors: Flow<List<VendorEntity>> = dao.getAllVendors()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()

    fun getVendorByIdFlow(id: Int): Flow<VendorEntity?> = dao.getVendorByIdFlow(id)
    fun getOrderByIdFlow(id: Int): Flow<OrderEntity?> = dao.getOrderByIdFlow(id)

    suspend fun getVendorById(id: Int): VendorEntity? = dao.getVendorById(id)

    suspend fun insertVendor(vendor: VendorEntity): Long = dao.insertVendor(vendor)

    suspend fun insertOrder(order: OrderEntity): Long = dao.insertOrder(order)

    suspend fun updateOrderStatus(orderId: Int, status: String) = dao.updateOrderStatus(orderId, status)

    suspend fun deleteOrderById(id: Int) = dao.deleteOrderById(id)

    suspend fun prepopulateIfEmpty() {
        val currentVendors = dao.getAllVendors().first()
        if (currentVendors.isEmpty()) {
            val sampleVendors = listOf(
                VendorEntity(
                    name = "Maa Ki Rasoi",
                    phone = "+91 94123 45678",
                    address = "Flat 104, Sunrise Apartments, Near IIT Hostel Gate, New Delhi",
                    foodType = "Veg",
                    menuToday = "Ghar Ki Thali: Shahi Paneer, Homestyle Arhar Dal, 4 Butter Rotis, Steamed Jeera Rice, Salad, Rice Kheer",
                    weeklyMenuJson = """[
                        {"day": "Monday", "lunch": "Shahi Paneer + Dal Tadka + 4 Roti + Rice", "dinner": "Aloo Jeera + Chana Masala + 4 Roti"},
                        {"day": "Tuesday", "lunch": "Kadi Pakoda + Rice + Roti + Aloo Methi", "dinner": "Mix Veg + Dal Makhani + 4 Roti"},
                        {"day": "Wednesday", "lunch": "Chole Bhature + Rice Thali", "dinner": "Lauki Kofta + Dal Fry + 4 Roti"},
                        {"day": "Thursday", "lunch": "Matar Paneer + Yellow Dal + Rice + Roti", "dinner": "Baingan Bharta + Dal Tadka + Roti"},
                        {"day": "Friday", "lunch": "Veg Pulao + Raita + Shahi Paneer", "dinner": "Bhindi Masala + Dal Makhani + Roti"},
                        {"day": "Saturday", "lunch": "Rajma Masala + Jeera Rice + Roti + Curd", "dinner": "Aloo Gobi + Yellow Dal + Roti"},
                        {"day": "Sunday", "lunch": "Special Paneer Butter Masala + Sweet", "dinner": "Kadhai Paneer + Dal Tadka + Roti"}
                    ]""",
                    pricePerMeal = 90.0,
                    rating = 4.8f,
                    distanceKm = 1.2,
                    deliveryTimeMinutes = 25,
                    deliveryRadiusKm = 5.0,
                    availabilityTiming = "11:00 AM - 3:00 PM, 6:30 PM - 10:00 PM",
                    reviewsCount = 142
                ),
                VendorEntity(
                    name = "The Punjabi Tiffin",
                    phone = "+91 98234 56789",
                    address = "Shop 12, Guru Nanak Market, Opp. Sector 4 Hostel, New Delhi",
                    foodType = "Veg",
                    menuToday = "Amritsari Rajma, Aloo Gobi Dry, 4 Whole Wheat Chapatis, Boondi Raita, Pickle, Steamed Basmati Rice",
                    weeklyMenuJson = """[
                        {"day": "Monday", "lunch": "Rajma + Rice + 4 Roti + Raita", "dinner": "Aloo Gobi + Yellow Dal + Roti"},
                        {"day": "Tuesday", "lunch": "Chole + Bhature + Rice + Curd", "dinner": "Ghiya Kofta + Dal Tadka + Roti"},
                        {"day": "Wednesday", "lunch": "Dal Makhani + Mix Veg + Roti", "dinner": "Matar Paneer + Rice + Roti"},
                        {"day": "Thursday", "lunch": "Kadi Chawal + 4 Roti + Salad", "dinner": "Allo Shimlamirch + Dal + Roti"},
                        {"day": "Friday", "lunch": "Shahi Paneer + Dal Tadka + Roti", "dinner": "Bhindi Do Pyaza + Dal + Roti"},
                        {"day": "Saturday", "lunch": "Black Chana + Rice + Roti", "dinner": "Aloo Beans + Yellow Dal + Roti"},
                        {"day": "Sunday", "lunch": "Punjabi Kadhai Paneer + Raita Thali", "dinner": "Mix Veg + Dal Makhani + Roti"}
                    ]""",
                    pricePerMeal = 80.0,
                    rating = 4.6f,
                    distanceKm = 2.5,
                    deliveryTimeMinutes = 35,
                    deliveryRadiusKm = 7.0,
                    availabilityTiming = "11:30 AM - 3:30 PM, 7:00 PM - 10:30 PM",
                    reviewsCount = 89
                ),
                VendorEntity(
                    name = "South Spice Kitchen",
                    phone = "+91 97345 67890",
                    address = "Plot 55, South Indian Colony, Near Tech Park, New Delhi",
                    foodType = "Veg",
                    menuToday = "Authentic Sambar, Tomato Rasam, Beetroot Poriyal, 3 Fluffy Idlis, Spiced Lemon Rice, Coconut Chutney",
                    weeklyMenuJson = """[
                        {"day": "Monday", "lunch": "Sambar + Rice + Poriyal + Appalam", "dinner": "Idli (4 pcs) + Sambar + Chutney"},
                        {"day": "Tuesday", "lunch": "Lemon Rice + Potato Fry + Curd", "dinner": "Dosa (2 pcs) + Sambar + Chutney"},
                        {"day": "Wednesday", "lunch": "Veg Kurma + Parotta (3 pcs) + Rice", "dinner": "Upma + Coconut Chutney + Rasam"},
                        {"day": "Thursday", "lunch": "Tamarind Rice + Beans Poriyal + Curd", "dinner": "Uttapam (2 pcs) + Tomato Chutney"},
                        {"day": "Friday", "lunch": "Sambar + Rasam + Rice + Cabbage Poriyal", "dinner": "Idli (4 pcs) + Podi + Chutney"},
                        {"day": "Saturday", "lunch": "Curd Rice + Pickle + Lemon Rice Thali", "dinner": "Adai Dosa + Aviyal + Sambar"},
                        {"day": "Sunday", "lunch": "Special South Feast (Sambar, Rasam, Payasam)", "dinner": "Idli & Vada Sambar Combo"}
                    ]""",
                    pricePerMeal = 85.0,
                    rating = 4.7f,
                    distanceKm = 3.4,
                    deliveryTimeMinutes = 30,
                    deliveryRadiusKm = 6.0,
                    availabilityTiming = "8:00 AM - 11:00 AM, 12:00 PM - 3:00 PM, 7:00 PM - 9:30 PM",
                    reviewsCount = 115
                ),
                VendorEntity(
                    name = "Elite Non-Veg Delights",
                    phone = "+91 91456 78901",
                    address = "Street 4, Zakir Nagar, Near Jamia Hostel, New Delhi",
                    foodType = "Both",
                    menuToday = "Classic Murgh Curry, Masala Egg Roast, 3 Flaky Malabar Parottas, Spiced Basmati Biryani Rice, Raita",
                    weeklyMenuJson = """[
                        {"day": "Monday", "lunch": "Chicken Curry + Parotta (3) + Rice", "dinner": "Egg Masala + Dal + 4 Roti"},
                        {"day": "Tuesday", "lunch": "Chicken Biryani + Raita + Salad", "dinner": "Aloo Jeera + Yellow Dal + Roti"},
                        {"day": "Wednesday", "lunch": "Egg Curry + Jeera Rice + 4 Roti", "dinner": "Chicken Kadhai + 4 Roti"},
                        {"day": "Thursday", "lunch": "Fish Curry + Steamed Rice Thali", "dinner": "Mix Veg + Dal Makhani + Roti"},
                        {"day": "Friday", "lunch": "Special Chicken Biryani + Kheer", "dinner": "Egg Bhurji + Dal Fry + Roti"},
                        {"day": "Saturday", "lunch": "Butter Chicken + 3 Butter Roti + Rice", "dinner": "Aloo Methi + Dal Tadka + Roti"},
                        {"day": "Sunday", "lunch": "Special Mutton Korma thali + Sweet", "dinner": "Chicken Keema + 4 Roti"}
                    ]""",
                    pricePerMeal = 130.0,
                    rating = 4.5f,
                    distanceKm = 4.1,
                    deliveryTimeMinutes = 40,
                    deliveryRadiusKm = 8.0,
                    availabilityTiming = "12:00 PM - 4:00 PM, 7:00 PM - 11:00 PM",
                    reviewsCount = 210
                ),
                VendorEntity(
                    name = "Healthy Green Bites",
                    phone = "+91 95567 89012",
                    address = "Penthouse C, Block 9, Green Park Residency, New Delhi",
                    foodType = "Veg",
                    menuToday = "High-Protein Soya Chunk Sabzi, Mixed Panchmel Dal, Oats Roti (3 pcs), Organic Quinoa Pulao, Cucumber Mint Greek Curd",
                    weeklyMenuJson = """[
                        {"day": "Monday", "lunch": "Soya Curry + Oats Roti + Quinoa + Curd", "dinner": "Palak Paneer (Low Fat) + 3 Roti"},
                        {"day": "Tuesday", "lunch": "Brown Rice + Moong Dal + Salad Thali", "dinner": "Methi Thepla + Curd + Stir fry Veg"},
                        {"day": "Wednesday", "lunch": "Paneer Bhurji + 3 Multi-grain Roti", "dinner": "Lauki Sabzi + Chana Dal + Oats Roti"},
                        {"day": "Thursday", "lunch": "Oats Khichdi + Curd + Roasted Papad", "dinner": "Mushroom Masala + Yellow Dal + Roti"},
                        {"day": "Friday", "lunch": "Soya Keema + Brown Rice + 3 Roti", "dinner": "Tofu Stir Fry + Mix Veg Soup + 2 Roti"},
                        {"day": "Saturday", "lunch": "Kadi Chawal (Brown Rice) + Salad", "dinner": "Allo Beans + Dal + 3 Roti"},
                        {"day": "Sunday", "lunch": "Paneer Tikka Salad + Sautéed Veggies", "dinner": "Nutritious Mix Veg Khichdi + Curd"}
                    ]""",
                    pricePerMeal = 110.0,
                    rating = 4.9f,
                    distanceKm = 1.8,
                    deliveryTimeMinutes = 20,
                    deliveryRadiusKm = 4.0,
                    availabilityTiming = "11:00 AM - 2:30 PM, 6:00 PM - 9:00 PM",
                    reviewsCount = 76
                )
            )
            for (vendor in sampleVendors) {
                dao.insertVendor(vendor)
            }
        }
    }
}
