package com.vieheal.mobile.feature.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vieheal.mobile.core.security.AuthorizationLaunch
import com.vieheal.mobile.core.security.SessionController
import com.vieheal.mobile.core.security.SessionState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface AuthUiEffect {
    data class LaunchAuthorization(val launch: AuthorizationLaunch) : AuthUiEffect
    data class LaunchEndSession(val launch: AuthorizationLaunch) : AuthUiEffect
}

class AuthViewModel(
    private val sessionController: SessionController,
) : ViewModel() {
    val sessionState = sessionController.state

    private val effectsChannel = Channel<AuthUiEffect>(capacity = Channel.BUFFERED)
    val effects: Flow<AuthUiEffect> = effectsChannel.receiveAsFlow()

    fun signIn() {
        viewModelScope.launch {
            val result = sessionController.prepareAuthorization()
            if (result is com.vieheal.mobile.core.security.AuthOperationResult.Success) {
                effectsChannel.send(AuthUiEffect.LaunchAuthorization(result.value))
            }
        }
    }

    fun completeAuthorization(data: Intent?) {
        viewModelScope.launch { sessionController.completeAuthorization(data) }
    }

    fun cancelSignIn() {
        sessionController.cancelAuthorization()
    }

    fun retry() {
        if ((sessionState.value as? SessionState.NetworkUnavailable)?.duringAuthorization == true) {
            signIn()
        } else {
            viewModelScope.launch { sessionController.retry() }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            sessionController.signOut()?.let {
                effectsChannel.send(AuthUiEffect.LaunchEndSession(it))
            }
        }
    }

    fun uiState(state: SessionState): AuthUiState =
        when (state) {
            SessionState.Initializing -> AuthUiState("Preparing VieHeal", "Restoring your protected session", isLoading = true)
            is SessionState.Unauthenticated ->
                AuthUiState(
                    title = "Sign in required",
                    message = state.reason.safeMessage(),
                    canSignIn = true,
                )
            SessionState.Authorizing ->
                AuthUiState(
                    "Signing in",
                    "Complete sign-in in your browser.",
                    isLoading = true,
                    canCancel = true,
                )
            SessionState.ResolvingApplicationSession ->
                AuthUiState("Verifying access", "VieHeal is loading your authorized clinic context.", isLoading = true)
            is SessionState.AccessDenied ->
                AuthUiState("Access denied", state.message, canSignOut = true)
            is SessionState.ConfigurationError ->
                AuthUiState("Configuration error", state.message)
            is SessionState.NetworkUnavailable ->
                AuthUiState("Network unavailable", state.message, canRetry = true, canSignOut = true)
            is SessionState.RecoverableFailure ->
                AuthUiState("Service unavailable", state.message, canRetry = true, canSignOut = true)
            is SessionState.Authenticated ->
                AuthUiState("Authenticated", "Application access verified")
        }

    companion object {
        fun factory(sessionController: SessionController): ViewModelProvider.Factory =
            viewModelFactory { initializer { AuthViewModel(sessionController) } }
    }
}

private fun com.vieheal.mobile.core.security.AuthFailure?.safeMessage(): String =
    when (this) {
        com.vieheal.mobile.core.security.AuthFailure.AuthorizationCancelled ->
            "Sign-in was cancelled. You can try again."
        com.vieheal.mobile.core.security.AuthFailure.AuthorizationRejected ->
            "Sign-in was not approved by the identity provider."
        com.vieheal.mobile.core.security.AuthFailure.CallbackValidationFailed ->
            "The sign-in response could not be verified. Please try again."
        com.vieheal.mobile.core.security.AuthFailure.TokenExchangeFailed,
        com.vieheal.mobile.core.security.AuthFailure.MalformedTokenResponse,
        -> "Sign-in could not be completed. Please try again."
        com.vieheal.mobile.core.security.AuthFailure.RefreshRejected,
        com.vieheal.mobile.core.security.AuthFailure.AccessTokenExpired,
        com.vieheal.mobile.core.security.AuthFailure.AuthenticationRequired,
        -> "Your session has expired. Please sign in again."
        com.vieheal.mobile.core.security.AuthFailure.SecureStorageFailure ->
            "Your protected session could not be restored. Please sign in again."
        com.vieheal.mobile.core.security.AuthFailure.DiscoveryFailed ->
            "The identity provider could not be verified. Please try again."
        else -> "Use your clinic identity to continue."
    }
