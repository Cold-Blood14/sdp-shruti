package com.example.myapplication.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _userType = MutableStateFlow<String>("")
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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid

                val user = mapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "userType" to userType
                )

                firestore.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener {
                        _userType.value = userType
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
                val uid = auth.currentUser!!.uid

                firestore.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val type = doc.getString("userType") ?: "Student"
                        _userType.value = type
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
    // GOOGLE LOGIN
    // ------------------------------------------------
    fun signInWithGoogle(
        idToken: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid

                // check firestore if user exists
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            // existing user
                            val type = doc.getString("userType") ?: "Student"
                            _userType.value = type
                            onSuccess(type)
                        } else {
                            // new Google user
                            val newUser = mapOf(
                                "uid" to uid,
                                "name" to (result.user!!.displayName ?: ""),
                                "email" to (result.user!!.email ?: ""),
                                "userType" to "Student"
                            )

                            firestore.collection("users")
                                .document(uid)
                                .set(newUser)
                                .addOnSuccessListener {
                                    _userType.value = "Student"
                                    onSuccess("Student")
                                }
                        }
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Google sign-in failed")
            }
    }
}
