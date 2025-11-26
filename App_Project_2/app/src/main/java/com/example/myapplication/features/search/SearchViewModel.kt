package com.example.myapplication.features.search

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class JobListing(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val type: String = "",
    val duration: String = "",
    val salary: String = "",
    val description: String = "",
    val requirements: String = "",
    val postedDate: Timestamp? = null,
    val employerId: String = "",
    val companyName: String = "",
    val companyLogo: String? = null
)

class SearchViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // All jobs loaded from Firestore
    private val _allJobs = MutableStateFlow<List<JobListing>>(emptyList())

    // Jobs shown in the UI (filtered)
    private val _searchResults = MutableStateFlow<List<JobListing>>(emptyList())
    val searchResults: StateFlow<List<JobListing>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Called from UI whenever user types in search bar.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter(query)
    }

    /**
     * Filters the in-memory list of jobs based on the query string.
     */
    private fun applyFilter(rawQuery: String) {
        val query = rawQuery.trim().lowercase()

        if (query.isEmpty()) {
            // No query => show all jobs
            _searchResults.value = _allJobs.value
            return
        }

        _searchResults.value = _allJobs.value.filter { job ->
            job.title.lowercase().contains(query) ||
                    job.company.lowercase().contains(query) ||
                    job.companyName.lowercase().contains(query) ||
                    job.location.lowercase().contains(query) ||
                    job.type.lowercase().contains(query) ||
                    job.description.lowercase().contains(query) ||
                    job.requirements.lowercase().contains(query)
        }
    }

    /**
     * Loads all jobs from Firestore.
     * Called from SearchScreen (LaunchedEffect).
     */
    fun getAllJobs() {
        _isLoading.value = true
        _errorMessage.value = null

        fun handleSuccess(snapshot: QuerySnapshot) {
            val results = snapshot.documents.map { it.toJobListing(it.id) }
            _allJobs.value = results
            // Re-apply current search text to newly loaded jobs
            applyFilter(_searchQuery.value)
            _isLoading.value = false
        }

        db.collection("jobs")
            .orderBy("postedDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                handleSuccess(documents)
            }
            .addOnFailureListener {
                // If orderBy fails (e.g., missing index), try without ordering
                db.collection("jobs")
                    .get()
                    .addOnSuccessListener { docs ->
                        handleSuccess(docs)
                    }
                    .addOnFailureListener { e ->
                        _errorMessage.value = "Failed to load jobs: ${e.message ?: "Unknown error"}"
                        _isLoading.value = false
                        _searchResults.value = emptyList()
                    }
            }
    }

    /**
     * Reset search text & results back to all jobs.
     */
    fun clearResults() {
        _searchQuery.value = ""
        _searchResults.value = _allJobs.value
        _errorMessage.value = null
    }

    /**
     * Maps Firestore document to JobListing.
     */
    private fun DocumentSnapshot.toJobListing(id: String): JobListing {
        return JobListing(
            id = id,
            title = getString("title") ?: "",
            company = getString("company") ?: "",
            location = getString("location") ?: "",
            type = getString("type") ?: "",
            duration = getString("duration") ?: "",
            salary = getString("salary") ?: "",
            description = getString("description") ?: "",
            requirements = getString("requirements") ?: "",
            postedDate = getTimestamp("postedDate"),
            employerId = getString("employerId") ?: "",
            companyName = getString("companyName") ?: getString("company") ?: "",
            companyLogo = getString("companyLogo")
        )
    }
}
