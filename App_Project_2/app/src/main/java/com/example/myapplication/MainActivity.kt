package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
import com.example.myapplication.features.dashboard.DashboardScreen           // student dashboard
import com.example.myapplication.features.dashboard.EmployerDashboardScreen // employer dashboard
import com.example.myapplication.features.home.HomeScreen
import com.example.myapplication.features.jobs.EditJobScreen
import com.example.myapplication.features.jobs.ViewApplicationsScreen
import com.example.myapplication.jobs.JobViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

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

        val jobViewModel = JobViewModel()
        val employerId = "employer123"        // TODO: replace with real auth ID
        val companyName = "My Company"
        val userName = "John Doe"

        val userPrefs = UserPreferences(this)

        // ✅ Always start from HomeScreen
        val initialRoute = "home"

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

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
                                // Create user in Firebase + save userType
                                authViewModel.signUpWithEmailPassword(
                                    name = name,
                                    email = email,
                                    password = password,
                                    userType = userType,
                                    onSuccess = {
                                        // Also mirror the type locally for fast access
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

                                        // Show success Toast
                                        Toast.makeText(this@MainActivity, "Sign up successful!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = {
                                        // Show error Toast
                                        Toast.makeText(this@MainActivity, "Sign up failed!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                    }

                    // ---------------- LOGIN ----------------
                    composable("login") {
                        LoginScreen(
                            onClose = { navController.popBackStack() },
                            onSignUp = { navController.navigate("user_type") },
                            onSubmit = { email, password ->
                                // 🔥 This is called when you press the Login button
                                authViewModel.signInWithEmailPassword(
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        // userType has been loaded from Firebase in AuthViewModel
                                        val firebaseUserType = authViewModel.userType.value
                                        // Save locally too
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

                                        // Show success Toast
                                        Toast.makeText(this@MainActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = {
                                        // Show error Toast
                                        Toast.makeText(this@MainActivity, "Login failed!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                    }

                    // --------------- STUDENT DASHBOARD (DashboardScreen) ---------------
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
