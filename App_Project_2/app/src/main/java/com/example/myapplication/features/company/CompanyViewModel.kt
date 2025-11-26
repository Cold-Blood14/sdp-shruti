package com.example.myapplication.features.company

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myapplication.features.auth.UserTypeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CompanyProfile(
    val employerId: String = "",
    val companyName: String = "",
    val companyLogo: String? = null,
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val website: String = "",
    val industry: String = "",
    val services: String = "",
    val founded: String = "",
    val description: String = ""
)

class CompanyViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    
    private val _companyProfile = MutableStateFlow<CompanyProfile?>(null)
    val companyProfile: StateFlow<CompanyProfile?> = _companyProfile.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Fetch company profile by employerId
     */
    fun fetchCompanyByEmployerId(employerId: String) {
        _isLoading.value = true
        _errorMessage.value = null
        
        // First try to get from employers collection
        db.collection("employers").document(employerId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _companyProfile.value = document.toCompanyProfile(employerId)
                    _isLoading.value = false
                } else {
                    // If not in employers collection, try users collection
                    db.collection("users").document(employerId)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            if (userDoc.exists() && UserTypeUtils.isEmployer(userDoc.getString("userType"))) {
                                _companyProfile.value = userDoc.toCompanyProfile(employerId)
                                _isLoading.value = false
                            } else {
                                _errorMessage.value = "Company profile not found"
                                _isLoading.value = false
                            }
                        }
                        .addOnFailureListener { exception ->
                            _errorMessage.value = "Failed to load company: ${exception.message}"
                            _isLoading.value = false
                        }
                }
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = "Failed to load company: ${exception.message}"
                _isLoading.value = false
            }
    }
    
    private fun com.google.firebase.firestore.DocumentSnapshot.toCompanyProfile(employerId: String): CompanyProfile {
        return CompanyProfile(
            employerId = employerId,
            companyName = getString("companyName") ?: getString("displayName") ?: getString("name") ?: "",
            companyLogo = getString("companyLogo") ?: getString("photoUrl"),
            email = getString("email") ?: "",
            phone = getString("phone") ?: "",
            address = getString("address") ?: "",
            website = getString("website") ?: "",
            industry = getString("industry") ?: "",
            services = getString("services") ?: "",
            founded = getString("founded") ?: "",
            description = getString("description") ?: ""
        )
    }
}




