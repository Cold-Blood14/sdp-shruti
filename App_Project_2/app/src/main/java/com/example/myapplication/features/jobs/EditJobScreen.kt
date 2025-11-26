package com.example.myapplication.features.jobs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.jobs.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJobScreen(
    navController: NavController,
    jobId: String?,          // null if adding a new job
    employerId: String,      // current employer
    jobViewModel: JobViewModel
) {
    // ------------------------
    // Load job data if editing
    // ------------------------
    val job = jobId?.let { jobViewModel.getJobById(it) }

    var title by remember { mutableStateOf(job?.title ?: "") }
    var company by remember { mutableStateOf(job?.company ?: "") }
    var location by remember { mutableStateOf(job?.location ?: "") }
    var salary by remember { mutableStateOf(job?.salary ?: "") }
    var description by remember { mutableStateOf(job?.description ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (jobId == null) "Add Job" else "Edit Job") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Salary") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Job Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 6
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (jobId == null) {
                        // Add new job
                        jobViewModel.addJob(
                            employerId = employerId,
                            title = title,
                            company = company,
                            location = location,
                            salary = salary,
                            description = description
                        )
                    } else {
                        // Update existing job
                        jobViewModel.updateJob(
                            jobId = jobId,
                            title = title,
                            company = company,
                            location = location,
                            salary = salary,
                            description = description
                        )
                    }
                    // Navigate back to previous screen
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (jobId == null) "Post Job" else "Save Changes")
            }
        }
    }
}