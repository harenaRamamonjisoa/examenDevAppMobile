package com.example.getevent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getevent.ui.auth.LoginScreen
import com.example.getevent.ui.auth.RegisterScreen
import com.example.getevent.ui.event.EventListScreen
import com.example.getevent.ui.admin.AdminDashboardScreen
import com.example.getevent.ui.theme.GetEventTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GetEventTheme {
                GetEventAppNav()
            }
        }
    }
}

@Composable
fun GetEventAppNav() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { role ->
                        if (role == "ADMIN") {
                            navController.navigate("admin_dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("event_list") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }
            composable("event_list") {
                EventListScreen(
                    onEventClick = { eventId ->
                        // Naviguer vers les détails si nécessaire
                    },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }
            composable("admin_dashboard") {
                AdminDashboardScreen(
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}
