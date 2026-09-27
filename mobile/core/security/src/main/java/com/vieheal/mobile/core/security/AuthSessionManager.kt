package com.vieheal.mobile.core.security

import android.content.Intent
import com.vieheal.mobile.core.model.ApplicationSessionGateway
import com.vieheal.mobile.core.model.ApplicationSessionResult
import com.vieheal.mobile.core.model.TokenSet
import com.vieheal.mobile.core.network.AccessTokenProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AuthSessionManager(
    private val tokenStore: SecureTokenStore,
    private val oidcClient: OidcClient,
    private val applicationSessionGateway: ApplicationSessionGateway,
    private val nowEpochMillis: () -> Long = System::currentTimeMillis,
) : SessionController,
    AccessTokenProvider {
    private val mutableState = MutableStateFlow<SessionState>(SessionState.Initializing)
    override val state: StateFlow<SessionState> = mutableState.asStateFlow()

    private val refreshMutex = Mutex()

    @Volatile
    private var activeTokens: TokenSet? = null

    override suspend fun restore() {
        mutableState.value = SessionState.Initializing
        val stored =
            runCatching { tokenStore.load() }
                .getOrElse {
                    runCatching { tokenStore.clear() }
                    mutableState.value = SessionState.Unauthenticated(AuthFailure.SecureStorageFailure)
                    return
                }
        when (stored) {
            StoredTokenResult.Empty -> mutableState.value = SessionState.Unauthenticated()
            StoredTokenResult.Corrupted ->
                mutableState.value = SessionState.Unauthenticated(AuthFailure.SecureStorageFailure)
            is StoredTokenResult.Available -> {
                activeTokens = stored.tokenSet
                val token = usableAccessToken() ?: return
                resolveApplicationSession(token)
            }
        }
    }

    override suspend fun prepareAuthorization(): AuthOperationResult<AuthorizationLaunch> {
        mutableState.value = SessionState.Authorizing
        val result = oidcClient.prepareAuthorization()
        if (result is AuthOperationResult.Failure) applyAuthenticationFailure(result.reason)
        return result
    }

    override suspend fun completeAuthorization(data: Intent?): AuthOperationResult<Unit> {
        val result = oidcClient.exchangeAuthorizationResponse(data)
        return when (result) {
            is AuthOperationResult.Success -> {
                if (!persist(result.value)) {
                    AuthOperationResult.Failure(AuthFailure.SecureStorageFailure)
                } else {
                    resolveApplicationSession(result.value.accessToken)
                    AuthOperationResult.Success(Unit)
                }
            }
            is AuthOperationResult.Failure -> {
                applyAuthenticationFailure(result.reason)
                result
            }
        }
    }

    override suspend fun accessToken(): String? = usableAccessToken()

    override fun cancelAuthorization() {
        if (mutableState.value == SessionState.Authorizing) {
            mutableState.value = SessionState.Unauthenticated(AuthFailure.AuthorizationCancelled)
        }
    }

    override suspend fun retry() {
        val token = usableAccessToken() ?: return
        resolveApplicationSession(token)
    }

    override suspend fun signOut(): AuthorizationLaunch? {
        val idToken = activeTokens?.idToken
        activeTokens = null
        runCatching { tokenStore.clear() }
        mutableState.value = SessionState.Unauthenticated()
        return runCatching { oidcClient.createEndSessionLaunch(idToken) }.getOrNull()
    }

    private suspend fun usableAccessToken(): String? {
        val snapshot = activeTokens ?: return null
        if (snapshot.isAccessTokenUsable(nowEpochMillis())) return snapshot.accessToken

        return refreshMutex.withLock {
            val current = activeTokens ?: return@withLock null
            if (current.isAccessTokenUsable(nowEpochMillis())) return@withLock current.accessToken
            if (!current.isRefreshTokenUsable(nowEpochMillis())) {
                invalidate(AuthFailure.AuthenticationRequired)
                return@withLock null
            }

            when (val refreshed = oidcClient.refresh(current)) {
                is AuthOperationResult.Success -> {
                    if (persist(refreshed.value)) refreshed.value.accessToken else null
                }
                is AuthOperationResult.Failure -> {
                    when (refreshed.reason) {
                        AuthFailure.RefreshRejected,
                        AuthFailure.AuthenticationRequired,
                        -> invalidate(refreshed.reason)
                        AuthFailure.NetworkUnavailable ->
                            mutableState.value = SessionState.NetworkUnavailable("Network unavailable during session refresh")
                        else ->
                            mutableState.value = SessionState.RecoverableFailure("Session refresh could not be completed")
                    }
                    null
                }
            }
        }
    }

    private suspend fun persist(tokenSet: TokenSet): Boolean =
        runCatching {
            tokenStore.save(tokenSet)
            activeTokens = tokenSet
        }.fold(
            onSuccess = { true },
            onFailure = {
                activeTokens = null
                runCatching { tokenStore.clear() }
                mutableState.value = SessionState.Unauthenticated(AuthFailure.SecureStorageFailure)
                false
            },
        )

    private suspend fun resolveApplicationSession(accessToken: String) {
        mutableState.value = SessionState.ResolvingApplicationSession
        when (val result = applicationSessionGateway.resolve(accessToken)) {
            is ApplicationSessionResult.Success ->
                mutableState.value = SessionState.Authenticated(result.context)
            ApplicationSessionResult.Unauthorized -> invalidate(AuthFailure.AuthenticationRequired)
            ApplicationSessionResult.Forbidden ->
                mutableState.value = SessionState.AccessDenied("Your identity is not authorized for VieHeal")
            ApplicationSessionResult.NetworkUnavailable ->
                mutableState.value = SessionState.NetworkUnavailable("VieHeal could not reach the server")
            is ApplicationSessionResult.ServerFailure ->
                mutableState.value = SessionState.RecoverableFailure("VieHeal is temporarily unavailable")
            ApplicationSessionResult.MalformedResponse ->
                mutableState.value = SessionState.RecoverableFailure("VieHeal returned an unexpected response")
        }
    }

    private suspend fun invalidate(reason: AuthFailure) {
        activeTokens = null
        runCatching { tokenStore.clear() }
        mutableState.value = SessionState.Unauthenticated(reason)
    }

    private fun applyAuthenticationFailure(reason: AuthFailure) {
        mutableState.value =
            when (reason) {
                AuthFailure.InvalidConfiguration,
                AuthFailure.MissingConfiguration,
                -> SessionState.ConfigurationError("Authentication configuration is missing or invalid")
                AuthFailure.NetworkUnavailable ->
                    SessionState.NetworkUnavailable(
                        message = "Authentication service is unavailable",
                        duringAuthorization = true,
                    )
                AuthFailure.AuthorizationCancelled -> SessionState.Unauthenticated(reason)
                else -> SessionState.Unauthenticated(reason)
            }
    }
}
