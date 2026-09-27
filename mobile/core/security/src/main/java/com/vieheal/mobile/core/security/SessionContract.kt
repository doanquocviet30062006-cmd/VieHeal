package com.vieheal.mobile.core.security

import android.content.Intent
import com.vieheal.mobile.core.model.UserSessionContext
import kotlinx.coroutines.flow.StateFlow

sealed interface SessionState {
    data object Initializing : SessionState
    data class Unauthenticated(val reason: AuthFailure? = null) : SessionState
    data object Authorizing : SessionState
    data object ResolvingApplicationSession : SessionState
    data class Authenticated(val context: UserSessionContext) : SessionState
    data class AccessDenied(val message: String) : SessionState
    data class ConfigurationError(val message: String) : SessionState
    data class NetworkUnavailable(
        val message: String,
        val duringAuthorization: Boolean = false,
    ) : SessionState
    data class RecoverableFailure(val message: String) : SessionState
}

sealed interface AuthFailure {
    data object MissingConfiguration : AuthFailure
    data object InvalidConfiguration : AuthFailure
    data object DiscoveryFailed : AuthFailure
    data object AuthorizationCancelled : AuthFailure
    data object AuthorizationRejected : AuthFailure
    data object CallbackValidationFailed : AuthFailure
    data object TokenExchangeFailed : AuthFailure
    data object AccessTokenExpired : AuthFailure
    data object RefreshRejected : AuthFailure
    data object NetworkUnavailable : AuthFailure
    data object ServerFailure : AuthFailure
    data object MalformedTokenResponse : AuthFailure
    data object AuthenticationRequired : AuthFailure
    data object SecureStorageFailure : AuthFailure
}

sealed interface AuthOperationResult<out T> {
    data class Success<T>(val value: T) : AuthOperationResult<T>
    data class Failure(val reason: AuthFailure) : AuthOperationResult<Nothing>
}

class AuthorizationLaunch(val intent: Intent) {
    override fun toString(): String = "AuthorizationLaunch([REDACTED])"
}

interface SessionStateProvider {
    val state: StateFlow<SessionState>
}

interface SessionController : SessionStateProvider {
    suspend fun restore()
    suspend fun prepareAuthorization(): AuthOperationResult<AuthorizationLaunch>
    suspend fun completeAuthorization(data: Intent?): AuthOperationResult<Unit>
    fun cancelAuthorization()
    suspend fun signOut(): AuthorizationLaunch?
    suspend fun retry()
}
