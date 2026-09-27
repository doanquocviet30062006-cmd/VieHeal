package com.vieheal.mobile.app

import com.vieheal.mobile.core.navigation.TopLevelDestination
import com.vieheal.mobile.core.model.UserSessionContext
import com.vieheal.mobile.core.security.AuthFailure
import com.vieheal.mobile.core.security.SessionState

sealed interface AppStartupState {
    data object Initializing : AppStartupState
    data class RequiresAuthentication(val reason: AuthFailure? = null) : AppStartupState
    data object Authorizing : AppStartupState
    data object ResolvingApplicationSession : AppStartupState
    data class Authenticated(val context: UserSessionContext) : AppStartupState
    data class AccessDenied(val message: String) : AppStartupState
    data class ConfigurationError(val message: String) : AppStartupState
    data class NetworkUnavailable(val message: String) : AppStartupState
    data class RecoverableFailure(val message: String) : AppStartupState
}

internal fun resolveStartupState(sessionState: SessionState): AppStartupState =
    when (sessionState) {
        SessionState.Initializing -> AppStartupState.Initializing
        is SessionState.Unauthenticated -> AppStartupState.RequiresAuthentication(sessionState.reason)
        SessionState.Authorizing -> AppStartupState.Authorizing
        SessionState.ResolvingApplicationSession -> AppStartupState.ResolvingApplicationSession
        is SessionState.Authenticated -> AppStartupState.Authenticated(sessionState.context)
        is SessionState.AccessDenied -> AppStartupState.AccessDenied(sessionState.message)
        is SessionState.ConfigurationError -> AppStartupState.ConfigurationError(sessionState.message)
        is SessionState.NetworkUnavailable -> AppStartupState.NetworkUnavailable(sessionState.message)
        is SessionState.RecoverableFailure -> AppStartupState.RecoverableFailure(sessionState.message)
    }

internal fun AppStartupState.topLevelDestination(): TopLevelDestination? =
    when (this) {
        AppStartupState.Initializing -> null
        is AppStartupState.Authenticated -> TopLevelDestination.Home
        else -> TopLevelDestination.Auth
    }
