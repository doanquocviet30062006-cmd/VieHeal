package com.vieheal.mobile.core.security

sealed interface SessionState {
    data object Unauthenticated : SessionState

    data object Authenticated : SessionState

    data class ConfigurationError(val reason: String) : SessionState
}

fun interface SessionStateProvider {
    fun currentState(): SessionState
}

/**
 * The honest pre-OIDC application state. This does not emulate login or issue credentials.
 */
object UnconfiguredSessionStateProvider : SessionStateProvider {
    override fun currentState(): SessionState = SessionState.Unauthenticated
}

interface SessionController {
    suspend fun beginSignIn()

    suspend fun signOut()
}
