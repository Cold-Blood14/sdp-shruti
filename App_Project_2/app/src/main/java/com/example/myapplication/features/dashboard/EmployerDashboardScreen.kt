package com.example.myapplication.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.jobs.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerDashboardScreen(
    userName: String = "User",
    companyName: String = "Your Company",
    userProfileImageUrl: String? = null,
    showProfilePicture: Boolean = false,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToUpdateProfile: () -> Unit = {},
    onNavigateToPostJob: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onShowNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    onViewJobDetails: (String, String) -> Unit = { _, _ -> },
    onViewApplications: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    jobViewModel: JobViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val employerId = "employer123" // You can pass this as parameter

    // Get jobs and filter by search query
    val jobs = jobViewModel.getJobsForEmployer(employerId)
        .filter { it.title.contains(searchQuery.text, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employer Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToPostJob
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Job")
            }
        },
        bottomBar = {
            EmployerBottomNavigationBar(
                onHomeClick = { /* already here */ },
                onPostClick = onNavigateToPostJob,
                onSearchClick = onNavigateToSearch,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // Search Bar (like DashboardScreen)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search jobs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Quick Actions Section
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD)) // light blue background so buttons pop
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToPostJob,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF90CAF9)
                    )
                ) {
                    Text("Post Job")
                }
                Button(
                    onClick = onNavigateToProfile,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF90CAF9)
                    )
                ) {
                    Text("Profile")
                }
                Button(
                    onClick = onLogout,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF90CAF9)
                    )
                ) {
                    Text("Logout")
                }
            }

            // Posted Jobs Title
            Text(
                text = "Posted Jobs",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Job List
            if (jobs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No jobs posted yet.\nClick + to add your first job.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(jobs) { job ->
                        JobCard(
                            jobId = job.id,
                            title = job.title,
                            company = job.company,
                            location = job.location,
                            salary = job.salary,
                            onViewApplications = { onViewApplications(job.id) },
                            onDetailsClick = {
                                onViewJobDetails(job.title, job.company)
                            }
                        )
                    }
                }
            }
        }
    }
}

/* ----------------- JOB CARD ----------------- */

@Composable
fun JobCard(
    jobId: String,
    title: String,
    company: String,
    location: String,
    salary: String,
    onViewApplications: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailsClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = company,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Salary: $salary",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onDetailsClick) {
                    Text("Edit Job")
                }
                Button(onClick = onViewApplications) {
                    Text("View Applications")
                }
            }
        }
    }
}

/* ----------------- BOTTOM NAV ----------------- */

@Composable
fun EmployerBottomNavigationBar(
    onHomeClick: () -> Unit,
    onPostClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = true,
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onPostClick,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Post"
                )
            },
            label = { Text("Post") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onSearchClick,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            },
            label = { Text("Search") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") }
        )
    }
}
