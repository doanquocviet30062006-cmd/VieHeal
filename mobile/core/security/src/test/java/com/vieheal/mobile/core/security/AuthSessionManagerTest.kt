package com.vieheal.mobile.core.security

import android.content.Intent
import com.vieheal.mobile.core.model.ApplicationSessionGateway
import com.vieheal.mobile.core.model.ApplicationSessionResult
import com.vieheal.mobile.core.model.AuthenticatedPrincipal
import com.vieheal.mobile.core.model.TokenSet
import com.vieheal.mobile.core.model.UserSessionContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthSessionManagerTest {
    private val now = 1_000_000L
    private val context =
        UserSessionContext(
            principal = AuthenticatedPrincipal("user-1", "subject-1", "Clinic User"),
            systemRoles = emptySet(),
            systemPermissions = emptySet(),
            organizations = emptyList(),
        )

    @Test
    fun `no stored credentials restores unauthenticated`() = runTest {
        val fixture = fixture()
        fixture.manager.restore()
        assertTrue(fixture.manager.state.value is SessionState.Unauthenticated)
    }

    @Test
    fun `corrupted protected session fails closed`() = runTest {
        val fixture = fixture(stored = StoredTokenResult.Corrupted)
        fixture.manager.restore()
        assertEquals(
            SessionState.Unauthenticated(AuthFailure.SecureStorageFailure),
            fixture.manager.state.value,
        )
    }

    @Test
    fun `OIDC exchange and application context success authenticates`() = runTest {
        val fixture = fixture(exchangeResult = AuthOperationResult.Success(validTokens()))
        fixture.manager.completeAuthorization(null)
        assertEquals(SessionState.Authenticated(context), fixture.manager.state.value)
        assertEquals(validTokens(), fixture.store.tokens)
    }

    @Test
    fun `OIDC cancellation leaves safe unauthenticated state`() = runTest {
        val fixture = fixture(exchangeResult = AuthOperationResult.Failure(AuthFailure.AuthorizationCancelled))
        fixture.manager.completeAuthorization(null)
        assertEquals(
            SessionState.Unauthenticated(AuthFailure.AuthorizationCancelled),
            fixture.manager.state.value,
        )
    }

    @Test
    fun `callback validation failure never authenticates`() = runTest {
        val fixture = fixture(exchangeResult = AuthOperationResult.Failure(AuthFailure.CallbackValidationFailed))
        fixture.manager.completeAuthorization(null)
        assertEquals(
            SessionState.Unauthenticated(AuthFailure.CallbackValidationFailed),
            fixture.manager.state.value,
        )
    }

    @Test
    fun `usable stored session resolves application context`() = runTest {
        val fixture = fixture(stored = StoredTokenResult.Available(validTokens()))
        fixture.manager.restore()
        assertEquals(SessionState.Authenticated(context), fixture.manager.state.value)
    }

    @Test
    fun `expired access token refreshes and authenticates`() = runTest {
        val refreshed = validTokens().copy(accessToken = "refreshed")
        val fixture =
            fixture(
                stored = StoredTokenResult.Available(expiredTokens()),
                refreshResult = AuthOperationResult.Success(refreshed),
            )
        fixture.manager.restore()
        assertEquals(SessionState.Authenticated(context), fixture.manager.state.value)
        assertEquals(refreshed, fixture.store.tokens)
        assertEquals(1, fixture.oidc.refreshCount)
    }

    @Test
    fun `refresh rejection invalidates credentials`() = runTest {
        val fixture =
            fixture(
                stored = StoredTokenResult.Available(expiredTokens()),
                refreshResult = AuthOperationResult.Failure(AuthFailure.RefreshRejected),
            )
        fixture.manager.restore()
        assertEquals(SessionState.Unauthenticated(AuthFailure.RefreshRejected), fixture.manager.state.value)
        assertNull(fixture.store.tokens)
    }

    @Test
    fun `backend 401 invalidates credentials`() = runTest {
        val fixture = fixture(stored = StoredTokenResult.Available(validTokens()), gatewayResult = ApplicationSessionResult.Unauthorized)
        fixture.manager.restore()
        assertTrue(fixture.manager.state.value is SessionState.Unauthenticated)
        assertNull(fixture.store.tokens)
    }

    @Test
    fun `backend 403 retains credentials and reports access denied`() = runTest {
        val tokens = validTokens()
        val fixture = fixture(stored = StoredTokenResult.Available(tokens), gatewayResult = ApplicationSessionResult.Forbidden)
        fixture.manager.restore()
        assertTrue(fixture.manager.state.value is SessionState.AccessDenied)
        assertEquals(tokens, fixture.store.tokens)
    }

    @Test
    fun `backend 5xx retains credentials and reports recoverable failure`() = runTest {
        val tokens = validTokens()
        val fixture = fixture(stored = StoredTokenResult.Available(tokens), gatewayResult = ApplicationSessionResult.ServerFailure(503))
        fixture.manager.restore()
        assertTrue(fixture.manager.state.value is SessionState.RecoverableFailure)
        assertEquals(tokens, fixture.store.tokens)
    }

    @Test
    fun `network failure retains credentials and reports network state`() = runTest {
        val tokens = validTokens()
        val fixture = fixture(stored = StoredTokenResult.Available(tokens), gatewayResult = ApplicationSessionResult.NetworkUnavailable)
        fixture.manager.restore()
        assertTrue(fixture.manager.state.value is SessionState.NetworkUnavailable)
        assertEquals(tokens, fixture.store.tokens)
    }

    @Test
    fun `logout removes credentials and application context`() = runTest {
        val fixture = fixture(stored = StoredTokenResult.Available(validTokens()))
        fixture.manager.restore()
        fixture.manager.signOut()
        assertNull(fixture.store.tokens)
        assertEquals(SessionState.Unauthenticated(), fixture.manager.state.value)
    }

    @Test
    fun `concurrent expired requests perform one refresh`() = runTest {
        val refreshed = validTokens().copy(accessToken = "one-refreshed-token")
        val fixture =
            fixture(
                exchangeResult = AuthOperationResult.Success(expiredTokens()),
                refreshResult = AuthOperationResult.Success(refreshed),
                refreshDelayMillis = 10,
            )
        fixture.manager.completeAuthorization(null)

        val values = (1..20).map { async { fixture.manager.accessToken() } }.awaitAll()
        assertTrue(values.all { it == refreshed.accessToken })
        assertEquals(1, fixture.oidc.refreshCount)
    }

    @Test
    fun `token string representation is redacted`() {
        val rendered = validTokens().toString()
        assertEquals("TokenSet([REDACTED])", rendered)
        assertFalse(rendered.contains("access-token"))
        assertFalse(rendered.contains("refresh-token"))
    }

    private fun fixture(
        stored: StoredTokenResult = StoredTokenResult.Empty,
        exchangeResult: AuthOperationResult<TokenSet> = AuthOperationResult.Success(validTokens()),
        refreshResult: AuthOperationResult<TokenSet> = AuthOperationResult.Success(validTokens()),
        gatewayResult: ApplicationSessionResult = ApplicationSessionResult.Success(context),
        refreshDelayMillis: Long = 0,
    ): Fixture {
        val store = FakeTokenStore(stored)
        val oidc = FakeOidcClient(exchangeResult, refreshResult, refreshDelayMillis)
        val manager = AuthSessionManager(store, oidc, ApplicationSessionGateway { gatewayResult }) { now }
        return Fixture(manager, store, oidc)
    }

    private fun validTokens() =
        TokenSet("access-token", "refresh-token", "id-token", "Bearer", now + 120_000, scope = "openid")

    private fun expiredTokens() =
        TokenSet("expired-token", "refresh-token", "id-token", "Bearer", now - 1, scope = "openid")

    private data class Fixture(
        val manager: AuthSessionManager,
        val store: FakeTokenStore,
        val oidc: FakeOidcClient,
    )

    private class FakeTokenStore(initial: StoredTokenResult) : SecureTokenStore {
        var tokens: TokenSet? = (initial as? StoredTokenResult.Available)?.tokenSet
        private var result: StoredTokenResult = initial
        override suspend fun load(): StoredTokenResult = result
        override suspend fun save(tokenSet: TokenSet) {
            tokens = tokenSet
            result = StoredTokenResult.Available(tokenSet)
        }
        override suspend fun clear() {
            tokens = null
            result = StoredTokenResult.Empty
        }
    }

    private class FakeOidcClient(
        private val exchangeResult: AuthOperationResult<TokenSet>,
        private val refreshResult: AuthOperationResult<TokenSet>,
        private val refreshDelayMillis: Long,
    ) : OidcClient {
        var refreshCount = 0
        override suspend fun prepareAuthorization(): AuthOperationResult<AuthorizationLaunch> =
            AuthOperationResult.Failure(AuthFailure.AuthorizationCancelled)
        override suspend fun exchangeAuthorizationResponse(data: Intent?): AuthOperationResult<TokenSet> = exchangeResult
        override suspend fun refresh(tokenSet: TokenSet): AuthOperationResult<TokenSet> {
            refreshCount++
            if (refreshDelayMillis > 0) delay(refreshDelayMillis)
            return refreshResult
        }
        override fun createEndSessionLaunch(idToken: String?): AuthorizationLaunch? = null
    }
}
