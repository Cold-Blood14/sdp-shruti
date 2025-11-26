package com.example.myapplication.features.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userType = MutableStateFlow("")
    val userType: StateFlow<String> = _userType

    // ------------------------------------------------
    // EMAIL SIGN UP
    // ------------------------------------------------
    fun signUpWithEmailPassword(
        name: String,
        email: String,
        password: String,
        userType: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val normalizedType = UserTypeUtils.normalize(userType) ?: "Student/Job Seeker"

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    onError("Failed to create user (no UID)")
                    return@addOnSuccessListener
                }

                val user = mapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "userType" to normalizedType
                )

                firestore.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener {
                        _userType.value = normalizedType
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to save user")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Sign up failed")
            }
    }

    // ------------------------------------------------
    // EMAIL SIGN IN
    // ------------------------------------------------
    fun signInWithEmailPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    onError("Login succeeded but no UID found")
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val rawType = doc.getString("userType")
                        val normalizedType = UserTypeUtils.normalize(rawType) ?: "Student/Job Seeker"
                        _userType.value = normalizedType
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to read user type")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Login failed")
            }
    }

    // ------------------------------------------------
    // GOOGLE LOGIN / SIGN UP
    // ------------------------------------------------
    fun signInWithGoogle(
        idToken: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    onError("Google sign-in failed: no UID")
                    return@addOnSuccessListener
                }

                // Check Firestore if user exists
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            // Existing user → read stored type
                            val rawType = doc.getString("userType")
                            val normalizedType = UserTypeUtils.normalize(rawType) ?: "Student/Job Seeker"
                            _userType.value = normalizedType
                            onSuccess(normalizedType)
                        } else {
                            // New Google user → create with default type
                            val defaultType = "Student/Job Seeker"
                            val newUser = mapOf(
                                "uid" to uid,
                                "name" to (result.user?.displayName ?: ""),
                                "email" to (result.user?.email ?: ""),
                                "userType" to defaultType
                            )

                            firestore.collection("users")
                                .document(uid)
                                .set(newUser)
                                .addOnSuccessListener {
                                    _userType.value = defaultType
                                    onSuccess(defaultType)
                                }
                                .addOnFailureListener { e ->
                                    onError(e.message ?: "Failed to save Google user")
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to check Google user")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Google sign-in failed")
            }
    }
}
