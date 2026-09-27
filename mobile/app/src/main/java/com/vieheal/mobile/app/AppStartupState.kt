package com.vieheal.mobile.app

import com.vieheal.mobile.core.navigation.TopLevelDestination
import com.vieheal.mobile.core.security.SessionState

sealed interface AppStartupState {
    data object Initializing : AppStartupState
    data object RequiresAuthentication : AppStartupState
    data object Authenticated : AppStartupState
    data class FatalConfigurationError(val reason: String) : AppStartupState
}

internal fun resolveStartupState(sessionState: SessionState): AppStartupState =
    when (sessionState) {
        SessionState.Unauthenticated -> AppStartupState.RequiresAuthentication
        SessionState.Authenticated -> AppStartupState.Authenticated
        is SessionState.ConfigurationError -> AppStartupState.FatalConfigurationError(sessionState.reason)
    }

internal fun AppStartupState.topLevelDestination(): TopLevelDestination? =
    when (this) {
        AppStartupState.RequiresAuthentication -> TopLevelDestination.Auth
        AppStartupState.Authenticated -> TopLevelDestination.Home
        AppStartupState.Initializing,
        is AppStartupState.FatalConfigurationError,
        -> null
    }
