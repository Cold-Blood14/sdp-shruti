package com.example.myapplication.jobs

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

// Simple job model used across the app
data class Job(
    val id: String = "",
    val employerId: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val salary: String = "",
    val description: String = ""
)

class JobViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val jobsCollection = firestore.collection("jobs")

    // In-memory list that stays in sync with Firestore
    private val _jobs: SnapshotStateList<Job> = mutableStateListOf()
    val jobs: List<Job> get() = _jobs

    private var listenerRegistration: ListenerRegistration? = null

    init {
        observeJobs()
    }

    private fun observeJobs() {
        // Listen to all jobs and keep _jobs up to date
        listenerRegistration = jobsCollection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            _jobs.clear()
            for (doc in snapshot.documents) {
                val job = Job(
                    id = doc.id,
                    employerId = doc.getString("employerId") ?: "",
                    title = doc.getString("title") ?: "",
                    company = doc.getString("company") ?: "",
                    location = doc.getString("location") ?: "",
                    salary = doc.getString("salary") ?: "",
                    description = doc.getString("description") ?: ""
                )
                _jobs.add(job)
            }
        }
    }

    // Called by EmployerDashboardScreen
    fun getJobsForEmployer(employerId: String): List<Job> {
        return _jobs.filter { it.employerId == employerId }
    }

    // Called by EditJobScreen when editing an existing job
    fun getJobById(jobId: String): Job? {
        return _jobs.find { it.id == jobId }
    }

    // Called by EditJobScreen when posting a NEW job
    fun addJob(
        employerId: String,
        title: String,
        company: String,
        location: String,
        salary: String,
        description: String
    ) {
        val jobData = hashMapOf(
            "employerId" to employerId,
            "title" to title,
            "company" to company,
            "location" to location,
            "salary" to salary,
            "description" to description
        )

        jobsCollection
            .add(jobData)
            .addOnSuccessListener { doc ->
                // Optionally store its own ID
                jobsCollection.document(doc.id).update("id", doc.id)
            }
            .addOnFailureListener {
                // You can log or handle error here if you want
            }
    }

    // Called by EditJobScreen when updating an existing job
    fun updateJob(
        jobId: String,
        title: String,
        company: String,
        location: String,
        salary: String,
        description: String
    ) {
        val updates = mapOf(
            "title" to title,
            "company" to company,
            "location" to location,
            "salary" to salary,
            "description" to description
        )

        jobsCollection
            .document(jobId)
            .update(updates)
            .addOnFailureListener {
                // Handle error if needed
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
