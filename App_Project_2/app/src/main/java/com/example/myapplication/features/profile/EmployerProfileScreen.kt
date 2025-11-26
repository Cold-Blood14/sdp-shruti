package com.example.myapplication.features.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.R

// Define colors
val darkBlue = Color(0xFF0D47A1)
val lightOrange = Color(0xFFFF9800)
val lightPurple = Color(0xFFE1BEE7)

// Placeholder data class for JobListing
data class JobListing(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = ""
)

// Placeholder ViewModel
class JobsViewModel : androidx.lifecycle.ViewModel() {
    private val _jobs = kotlinx.coroutines.flow.MutableStateFlow<List<JobListing>>(emptyList())
    val jobs: kotlinx.coroutines.flow.StateFlow<List<JobListing>> = _jobs

    private val _isLoading = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isLoading: kotlinx.coroutines.flow.StateFlow<Boolean> = _isLoading

    fun fetchJobsByEmployer(employerId: String) {
        // Placeholder implementation
        _isLoading.value = true
        // Simulate loading
        _jobs.value = emptyList()
        _isLoading.value = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerProfileScreen(
    companyName: String = "Your Company",
    contactEmail: String = "",
    userProfileImageUrl: String? = null,
    showProfilePicture: Boolean = false,
    employerId: String = "",
    onBack: () -> Unit = {},
    onEditCompany: () -> Unit = {},
    onPostJob: () -> Unit = {},
    onViewPostedJobs: () -> Unit = {},
    onProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val jobsViewModel: JobsViewModel = viewModel()
    val jobs: List<JobListing> by jobsViewModel.jobs.collectAsState(initial = emptyList())
    val isLoading: Boolean by jobsViewModel.isLoading.collectAsState(initial = false)

    LaunchedEffect(employerId) {
        if (employerId.isNotBlank()) {
            jobsViewModel.fetchJobsByEmployer(employerId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Company Profile", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CompanyHeroCard(
                companyName = companyName,
                contactEmail = contactEmail,
                showProfilePicture = showProfilePicture,
                userProfileImageUrl = userProfileImageUrl,
                onEditCompany = onEditCompany
            )

            StatsRow(
                jobsPosted = jobs.size,
                isLoading = isLoading
            )

            QuickActionsSection(
                onPostJob = onPostJob,
                onViewPostedJobs = onViewPostedJobs,
                onEditCompany = onEditCompany
            )
        }
    }
}

@Composable
private fun CompanyHeroCard(
    companyName: String,
    contactEmail: String,
    showProfilePicture: Boolean,
    userProfileImageUrl: String?,
    onEditCompany: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = lightPurple),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (showProfilePicture && userProfileImageUrl != null) {
                    AsyncImage(
                        model = userProfileImageUrl,
                        contentDescription = "Company logo",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(id = R.drawable.shruti),
                        error = painterResource(id = R.drawable.shruti)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Company logo",
                        tint = darkBlue,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Text(companyName, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(contactEmail, fontSize = 14.sp, color = Color.Gray)

            OutlinedButton(
                onClick = onEditCompany,
                shape = RoundedCornerShape(30.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = darkBlue)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Edit Company Details", color = darkBlue)
            }
        }
    }
}

@Composable
private fun StatsRow(
    jobsPosted: Int,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Jobs Posted",
            value = if (isLoading) "..." else jobsPosted.toString(),
            icon = Icons.Default.ListAlt,
            gradientColors = listOf(darkBlue, Color(0xFF0A3871)),
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "Talent Reach",
            value = if (jobsPosted == 0) "Start posting" else "Growing",
            icon = Icons.Default.Group,
            gradientColors = listOf(lightOrange, Color(0xFFFFCC80)),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .wrapContentHeight(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        gradientColors.map { it.copy(alpha = 0.15f) }
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = darkBlue,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onPostJob: () -> Unit,
    onViewPostedJobs: () -> Unit,
    onEditCompany: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            QuickActionButton(
                label = "Post a New Job",
                icon = Icons.Default.PostAdd,
                background = darkBlue,
                contentColor = Color.White,
                onClick = onPostJob
            )

            QuickActionButton(
                label = "View Posted Jobs",
                icon = Icons.Default.ListAlt,
                background = lightOrange,
                contentColor = Color.White,
                onClick = onViewPostedJobs
            )

            QuickActionButton(
                label = "Update Company Profile",
                icon = Icons.Default.Edit,
                background = Color.White,
                contentColor = darkBlue,
                borderColor = darkBlue,
                onClick = onEditCompany
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: Color,
    contentColor: Color,
    borderColor: Color? = null,
    onClick: () -> Unit
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .height(56.dp)

    if (borderColor == null) {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = background,
                contentColor = contentColor
            )
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = contentColor)
            Spacer(modifier = Modifier.size(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                containerColor = background,
                contentColor = contentColor
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = contentColor)
            Spacer(modifier = Modifier.size(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}