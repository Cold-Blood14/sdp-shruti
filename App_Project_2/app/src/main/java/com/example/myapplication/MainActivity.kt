package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.features.auth.AuthViewModel
import com.example.myapplication.features.auth.LoginScreen
import com.example.myapplication.features.auth.SignUpScreen
import com.example.myapplication.features.auth.UserTypeSelectionScreen
import com.example.myapplication.features.auth.UserTypeUtils
import com.example.myapplication.features.company.CompanyProfileScreen
import com.example.myapplication.features.dashboard.DashboardScreen           // student/employee dashboard
import com.example.myapplication.features.dashboard.EmployerDashboardScreen // employer dashboard
import com.example.myapplication.features.home.HomeScreen
import com.example.myapplication.features.jobs.EditJobScreen
import com.example.myapplication.features.jobs.ViewApplicationsScreen
import com.example.myapplication.jobs.JobViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.util.Log
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes


// ---------- SharedPreferences helper (userType is stored as String) ----------
class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    fun saveUserType(rawType: String?) {
        if (rawType == null) return
        val normalized = UserTypeUtils.normalize(rawType) ?: rawType
        prefs.edit().putString("user_type", normalized).apply()
    }
    fun getUserType(): String? = prefs.getString("user_type", null)
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }
    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    fun clear() {
        prefs.edit().clear().apply()
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // JobViewModel is simple enough to construct directly
        val jobViewModel = JobViewModel()
        val employerId = "employer123"        // TODO: replace with real auth ID
        val companyName = "My Company"
        val userName = "John Doe"

        val userPrefs = UserPreferences(this)

        // Always start from HomeScreen (auth is handled inside)
        val initialRoute = "home"

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val context = LocalContext.current

                // ---------- GOOGLE SIGN-IN CLIENT ----------
                val googleSignInClient = remember {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        // Make sure you have this string resource configured from Firebase console
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    GoogleSignIn.getClient(context, gso)
                }

                // ---------- GOOGLE SIGN-IN LAUNCHER ----------
                val googleLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        val idToken = account.idToken

                        if (idToken.isNullOrEmpty()) {
                            Toast.makeText(context, "Google sign-in failed: no token", Toast.LENGTH_LONG).show()
                            return@rememberLauncherForActivityResult
                        }

                            authViewModel.signInWithGoogle(
                                idToken = idToken,
                                onSuccess = { type ->
                                    // Save userType locally as well
                                    userPrefs.saveUserType(type)
                                    userPrefs.setLoggedIn(true)

                                    val normalized = UserTypeUtils.normalize(type)
                                    val dest =
                                        if (UserTypeUtils.isEmployer(normalized)) {
                                            "employer_dashboard"
                                        } else {
                                            "dashboard"
                                        }

                                    navController.navigate(dest) {
                                        popUpTo("home") { inclusive = true }
                                    }

                                    Toast.makeText(context, "Logged in with Google", Toast.LENGTH_SHORT).show()
                                },
                                onError = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        } catch (e: ApiException) {
                            Log.e("GoogleSignIn", "signIn failed", e)
                            val msg = when (e.statusCode) {
                                GoogleSignInStatusCodes.SIGN_IN_CANCELLED ->
                                    "You cancelled sign-in or device has no available Google account."
                                GoogleSignInStatusCodes.SIGN_IN_FAILED ->
                                    "Google sign-in failed (status SIGN_IN_FAILED). Often SHA1/client-id misconfig."
                                GoogleSignInStatusCodes.DEVELOPER_ERROR ->
                                    "Google sign-in DEVELOPER_ERROR (10): usually wrong SHA1/package in Firebase."
                                GoogleSignInStatusCodes.NETWORK_ERROR ->
                                    "Google sign-in failed due to network error."
                                else ->
                                    "Google sign-in error: status=${e.statusCode}"
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Log.e("GoogleSignIn", "Unexpected error", e)
                            Toast.makeText(context, "Google sign-in error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                // Helper lambda to start Google sign-in
                val startGoogleSignIn: () -> Unit = {
                    googleLauncher.launch(googleSignInClient.signInIntent)
                }

                NavHost(
                    navController = navController,
                    startDestination = initialRoute
                ) {

                    // ---------------- HOME ----------------
                    composable("home") {
                        HomeScreen(
                            onLogin = { navController.navigate("login") },
                            onSignUp = { navController.navigate("user_type") },
                            onRegister = { navController.navigate("user_type") }
                        )
                    }
                    // ------------ USER TYPE SELECTION (before sign up) ------------
                    composable("user_type") {
                        UserTypeSelectionScreen(
                            onBack = { navController.popBackStack() },
                            onUserTypeSelected = { rawType ->
                                userPrefs.saveUserType(rawType)
                                navController.navigate("signup")
                            }
                        )
                    }
                    // ---------------- SIGN UP ----------------
                    composable("signup") {
                        val initialType =
                            UserTypeUtils.normalize(userPrefs.getUserType()) ?: "Student/Job Seeker"

                        SignUpScreen(
                            onClose = { navController.popBackStack() },
                            initialUserType = initialType,
                            onSubmit = { name, email, password, userType ->
                                authViewModel.signUpWithEmailPassword(
                                    name = name,
                                    email = email,
                                    password = password,
                                    userType = userType,
                                    onSuccess = {
                                        userPrefs.saveUserType(userType)
                                        userPrefs.setLoggedIn(true)

                                        val normalized = UserTypeUtils.normalize(userType)
                                        val dest =
                                            if (UserTypeUtils.isEmployer(normalized)) {
                                                "employer_dashboard"
                                            } else {
                                                "dashboard"
                                            }

                                        navController.navigate(dest) {
                                            popUpTo("home") { inclusive = true }
                                        }

                                        Toast.makeText(context, "Sign up successful!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onGoogleSignIn = {
                                startGoogleSignIn()
                            }
                        )
                    }
                    // ---------------- LOGIN ----------------
                    composable("login") {
                        LoginScreen(
                            onClose = { navController.popBackStack() },
                            onSignUp = { navController.navigate("user_type") },
                            onSubmit = { email, password ->
                                authViewModel.signInWithEmailPassword(
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        val firebaseUserType = authViewModel.userType.value
                                        userPrefs.saveUserType(firebaseUserType)
                                        userPrefs.setLoggedIn(true)

                                        val normalized = UserTypeUtils.normalize(firebaseUserType)
                                        val dest =
                                            if (UserTypeUtils.isEmployer(normalized)) {
                                                "employer_dashboard"
                                            } else {
                                                "dashboard"
                                            }

                                        navController.navigate(dest) {
                                            popUpTo("home") { inclusive = true }
                                        }

                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onGoogleSignIn = {
                                startGoogleSignIn()
                            }
                        )
                    }
                    // --------------- STUDENT / EMPLOYEE DASHBOARD ---------------
                    composable("dashboard") {
                        DashboardScreen(
                            navController = navController,
                            employerId = employerId,
                            jobViewModel = jobViewModel
                        )
                    }
                    // --------------- EMPLOYER DASHBOARD ---------------
                    composable("employer_dashboard") {
                        EmployerDashboardScreen(
                            userName = userName,
                            companyName = companyName,
                            onNavigateToProfile = {
                                navController.navigate("company_profile")
                            },
                            onNavigateToUpdateProfile = {
                                navController.navigate("company_profile")
                            },
                            onNavigateToPostJob = {
                                navController.navigate("add_job")
                            },
                            onNavigateToSearch = {
                                // TODO: navigate to search screen if needed
                            },
                            onShowNotifications = {
                                // TODO: open notifications screen
                            },
                            onLogout = {
                                userPrefs.clear()
                                navController.navigate("home") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onViewJobDetails = { title, _ ->
                                navController.navigate("edit_job/$title")
                            }
                        )
                    }
                    // --------------- COMPANY PROFILE (EMPLOYER) ---------------
                    composable("company_profile") {
                        CompanyProfileScreen(
                            companyName = companyName,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    // --------------- JOBS (EMPLOYER) ---------------
                    composable("add_job") {
                        EditJobScreen(
                            navController = navController,
                            jobId = null,
                            employerId = employerId,
                            jobViewModel = jobViewModel
                        )
                    }
                    composable(
                        route = "edit_job/{jobId}",
                        arguments = listOf(
                            navArgument("jobId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val jobId = backStackEntry.arguments?.getString("jobId")
                        EditJobScreen(
                            navController = navController,
                            jobId = jobId,
                            employerId = employerId,
                            jobViewModel = jobViewModel
                        )
                    }
                    composable(
                        route = "view_applications/{jobId}",
                        arguments = listOf(
                            navArgument("jobId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val jobId = backStackEntry.arguments?.getString("jobId")!!
                        ViewApplicationsScreen(
                            navController = navController,
                            jobId = jobId
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MyApplicationTheme {
        HomeScreen(
            onLogin = {},
            onSignUp = {},
            onRegister = {}
        )
    }
}
