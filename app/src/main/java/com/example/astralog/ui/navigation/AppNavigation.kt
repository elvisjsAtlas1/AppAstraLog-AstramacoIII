package com.example.astralog.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.astralog.data.local.TokenManager
import com.example.astralog.ui.screens.carga.CargaScreen
import com.example.astralog.ui.screens.login.LoginScreen
import com.example.astralog.ui.screens.pedidos.PedidosScreen
import com.example.astralog.ui.screens.profile.ProfileScreen
import com.example.astralog.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as Application
    val tokenManager = remember { TokenManager(context) }
    val startRoute = remember { mutableStateOf("splash") }

    LaunchedEffect(Unit) {
        startRoute.value = if (tokenManager.isLoggedIn()) "profile" else "login"
    }

    if (startRoute.value == "splash") {
        SplashScreen()
    } else {
        NavHost(
            navController = navController,
            startDestination = startRoute.value
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("profile") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    },
                    onOpenCarga = {
                        navController.navigate("carga")
                    },
                    onOpenPedidos = {
                        navController.navigate("pedidos")
                    }
                )
            }

            composable("carga") {
                CargaScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("pedidos") {
                PedidosScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}