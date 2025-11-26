package com.example.myapplication.features.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ✅ Define the colors locally instead of importing from dashboard
private val darkBlue = Color(0xFF0D47A1)
private val lightPurple = Color(0xFFEDE7F6)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String // "application" for employers, "job" for students
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    isEmployer: Boolean = false,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Sample notifications - in real app, this would come from Firestore
    val notifications = remember {
        if (isEmployer) {
            listOf(
                Notification(
                    "1",
                    "New Application",
                    "John Doe applied for Senior Developer position",
                    "2 hours ago",
                    "application"
                ),
                Notification(
                    "2",
                    "New Application",
                    "Jane Smith applied for Product Manager position",
                    "5 hours ago",
                    "application"
                ),
                Notification(
                    "3",
                    "New Application",
                    "Bob Johnson applied for Senior Developer position",
                    "1 day ago",
                    "application"
                )
            )
        } else {
            listOf(
                Notification(
                    "1",
                    "Application Status",
                    "Your application for UI/UX Designer has been reviewed",
                    "1 hour ago",
                    "job"
                ),
                Notification(
                    "2",
                    "New Job Match",
                    "New job posting matches your profile: Backend Developer",
                    "3 hours ago",
                    "job"
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = if (isEmployer) "Job Applications" else "Notifications",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (notifications.isEmpty()) {
                Text("You have no new notifications.")
            } else {
                LazyColumn {
                    items(notifications) { notification ->
                        NotificationCard(notification)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun NotificationCard(notification: Notification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = lightPurple
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.timestamp,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
