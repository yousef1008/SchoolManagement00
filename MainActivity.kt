// Main Activity and Application Setup
package com.schoolmanagement

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolmanagement.data.local.SchoolDatabase
import com.schoolmanagement.presentation.ui.theme.SchoolManagementTheme
import com.schoolmanagement.presentation.ui.auth.LoginScreen
import com.schoolmanagement.presentation.ui.dashboard.DashboardScreen
import com.schoolmanagement.presentation.ui.student.StudentListScreen
import com.schoolmanagement.presentation.ui.installment.InstallmentListScreen
import com.schoolmanagement.presentation.ui.employee.EmployeeListScreen
import com.schoolmanagement.presentation.ui.whatsapp.WhatsAppScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchoolManagementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SchoolManagementApp()
                }
            }
        }
    }
}

@Composable
fun SchoolManagementApp() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "dashboard" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onNavigateToStudents = { navController.navigate("students") },
                onNavigateToInstallments = { navController.navigate("installments") },
                onNavigateToEmployees = { navController.navigate("employees") },
                onNavigateToWhatsApp = { navController.navigate("whatsapp") },
                onLogout = {
                    isLoggedIn = false
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
        
        composable("students") {
            StudentListScreen()
        }
        
        composable("installments") {
            InstallmentListScreen()
        }
        
        composable("employees") {
            EmployeeListScreen()
        }
        
        composable("whatsapp") {
            WhatsAppScreen()
        }
    }
}

class SchoolManagementApplication : Application() {
    val database by lazy { SchoolDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any required services or configurations
    }
}

