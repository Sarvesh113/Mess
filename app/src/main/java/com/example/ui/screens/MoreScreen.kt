package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MessMateOrange
import com.example.ui.theme.MessMateOrangeLight
import com.example.ui.theme.MessMateGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen() {
    var feedbackText by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var ratingSlider by remember { mutableStateOf(5.0f) }
    var showFeedbackSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("more_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Sticky Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MessMateOrange)
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "About & Support",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Find out more about Mess Mate mission and contact us",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // About Us section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = MessMateOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About Mess Mate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Mess Mate is a premium, community-first platform designed to bridge the gap between hard-working local home cooks and college students or working professionals who are craving healthy, affordable, and hygienic homestyle tiffin meals.",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Our Core Missions:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    MissionPointItem(text = "👩‍🍳 Support independent home chefs & cooks by offering them an automated platform to scale their food businesses.")
                    MissionPointItem(text = "🥗 Deliver freshly prepared, nutrient-rich meals that act as healthy alternatives to junk and fast foods.")
                    MissionPointItem(text = "🎓 Reduce the heavy dependency of hostel students on expensive, greasy commercial restaurants.")
                    MissionPointItem(text = "📦 Provide high-precision contactless delivery at extremely reasonable prices.")
                }
            }
        }

        // Contact details section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContactSupport, contentDescription = null, tint = MessMateOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Contact Support",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ContactDetailRow(
                        icon = Icons.Outlined.Mail,
                        title = "Email Support",
                        value = "support@messmate.com"
                    )

                    ContactDetailRow(
                        icon = Icons.Outlined.Phone,
                        title = "Helpline Contact",
                        value = "+91 11-4567-8901 (9 AM - 9 PM)"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // WhatsApp Support Button
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = MessMateGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.Forum, contentDescription = "WhatsApp", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chat with Us on WhatsApp", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Feedback Form section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Feedback, contentDescription = null, tint = MessMateOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share Your Feedback",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Text(
                        text = "We love to hear how we can improve your tiffin experience!",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    if (showFeedbackSuccess) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MessMateGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Thank you! Your feedback has been registered.",
                                color = MessMateGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = userEmail,
                            onValueChange = { userEmail = it },
                            label = { Text("Your Email") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            label = { Text("Feedback / Suggestions") },
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "App Rating:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = "${ratingSlider.toInt()} Stars", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MessMateOrange)
                        }

                        Slider(
                            value = ratingSlider,
                            onValueChange = { ratingSlider = it },
                            valueRange = 1.0f..5.0f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                activeTrackColor = MessMateOrange,
                                thumbColor = MessMateOrange
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Button(
                            onClick = {
                                if (feedbackText.isNotBlank()) {
                                    showFeedbackSuccess = true
                                    feedbackText = ""
                                    userEmail = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MessMateOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text("Submit Feedback", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MissionPointItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun ContactDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 10.sp, color = Color.Gray)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
