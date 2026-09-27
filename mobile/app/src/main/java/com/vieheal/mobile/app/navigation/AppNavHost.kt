package com.vieheal.mobile.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vieheal.mobile.app.AppStartupState
import com.vieheal.mobile.app.topLevelDestination
import com.vieheal.mobile.core.navigation.TopLevelDestination
import com.vieheal.mobile.feature.auth.AuthScreen
import com.vieheal.mobile.feature.auth.AuthUiState
import com.vieheal.mobile.feature.home.HomeScreen

@Composable
fun AppNavHost(startupState: AppStartupState) {
    val destination = checkNotNull(startupState.topLevelDestination())

    key(destination) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = destination.route) {
            when (destination) {
                TopLevelDestination.Auth -> {
                    composable(TopLevelDestination.Auth.route) {
                        AuthScreen(
                            uiState =
                                AuthUiState(
                                    isAuthenticationConfigured = false,
                                    isAuthenticated = false,
                                ),
                        )
                    }
                }

                TopLevelDestination.Home -> {
                    composable(TopLevelDestination.Home.route) { HomeScreen() }
                }
            }
        }
    }
}
