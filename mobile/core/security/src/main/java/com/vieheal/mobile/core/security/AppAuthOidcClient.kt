package com.vieheal.mobile.core.security

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.vieheal.mobile.core.model.OidcConfiguration
import com.vieheal.mobile.core.model.TokenSet
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.CodeVerifierUtil
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import net.openid.appauth.connectivity.ConnectionBuilder
import net.openid.appauth.connectivity.DefaultConnectionBuilder

private val LOCAL_DEVELOPMENT_HOSTS =
    setOf(
        "localhost",
        "127.0.0.1",
        "10.0.2.2",
    )

private object LocalDevelopmentConnectionBuilder : ConnectionBuilder {

    override fun openConnection(uri: Uri): HttpURLConnection {
        val scheme = uri.scheme?.lowercase()
        val host = uri.host?.lowercase()

        if (scheme != "http" && scheme != "https") {
            throw IOException("Unsupported AppAuth URI scheme: $scheme")
        }

        if (scheme == "http") {
            if (host == null || host !in LOCAL_DEVELOPMENT_HOSTS) {
                throw IOException(
                    "Cleartext AppAuth is permitted only for local development hosts",
                )
            }
        }

        return (URL(uri.toString()).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 10_000
            instanceFollowRedirects = false
        }
    }
}

class AppAuthOidcClient(
    context: Context,
    private val configuration: OidcConfiguration,
) : OidcClient {

    private val issuerUri = Uri.parse(configuration.issuer)

    private val useDevelopmentCleartext =
        configuration.allowCleartextForDevelopment &&
            issuerUri.scheme.equals("http", ignoreCase = true) &&
            issuerUri.host
                ?.lowercase()
                ?.let { it in LOCAL_DEVELOPMENT_HOSTS } == true

    private val connectionBuilder: ConnectionBuilder =
        if (useDevelopmentCleartext) {
            LocalDevelopmentConnectionBuilder
        } else {
            DefaultConnectionBuilder.INSTANCE
        }

    private val appAuthConfiguration =
        AppAuthConfiguration.Builder()
            .setConnectionBuilder(connectionBuilder)
            .setSkipIssuerHttpsCheck(useDevelopmentCleartext)
            .build()

    private val authorizationService =
        AuthorizationService(
            context.applicationContext,
            appAuthConfiguration,
        )

    @Volatile
    private var discoveredConfiguration: AuthorizationServiceConfiguration? = null

    override suspend fun prepareAuthorization(): AuthOperationResult<AuthorizationLaunch> {
        if (!configuration.isValid()) {
            return AuthOperationResult.Failure(AuthFailure.InvalidConfiguration)
        }

        val serviceConfiguration =
            when (val discovery = discover()) {
                is AuthOperationResult.Success -> discovery.value
                is AuthOperationResult.Failure -> return discovery
            }

        val verifier = CodeVerifierUtil.generateRandomCodeVerifier()
        val nonce = CodeVerifierUtil.generateRandomCodeVerifier()

        val request =
            AuthorizationRequest.Builder(
                serviceConfiguration,
                configuration.clientId,
                ResponseTypeValues.CODE,
                Uri.parse(configuration.redirectUri),
            )
                .setScope("openid profile email")
                .setNonce(nonce)
                .setCodeVerifier(
                    verifier,
                    CodeVerifierUtil.deriveCodeVerifierChallenge(verifier),
                    CodeVerifierUtil.getCodeVerifierChallengeMethod(),
                )
                .build()

        return AuthOperationResult.Success(
            AuthorizationLaunch(
                authorizationService.getAuthorizationRequestIntent(request),
            ),
        )
    }

    override suspend fun exchangeAuthorizationResponse(
        data: Intent?,
    ): AuthOperationResult<TokenSet> {
        if (data == null) {
            return AuthOperationResult.Failure(
                AuthFailure.AuthorizationCancelled,
            )
        }

        val exception = AuthorizationException.fromIntent(data)

        if (exception != null) {
            val reason =
                when {
                    exception.error == "state_mismatch" ->
                        AuthFailure.CallbackValidationFailed

                    exception.error == "access_denied" ->
                        AuthFailure.AuthorizationRejected

                    exception.code ==
                        AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.code ->
                        AuthFailure.AuthorizationCancelled

                    else ->
                        AuthFailure.AuthorizationRejected
                }

            return AuthOperationResult.Failure(reason)
        }

        val response =
            AuthorizationResponse.fromIntent(data)
                ?: return AuthOperationResult.Failure(
                    AuthFailure.CallbackValidationFailed,
                )

        discoveredConfiguration = response.request.configuration

        return performTokenRequest(
            response.createTokenExchangeRequest(),
        )
    }

    override suspend fun refresh(
        tokenSet: TokenSet,
    ): AuthOperationResult<TokenSet> {
        val refreshToken =
            tokenSet.refreshToken
                ?: return AuthOperationResult.Failure(
                    AuthFailure.AuthenticationRequired,
                )

        val serviceConfiguration =
            discoveredConfiguration
                ?: when (val discovery = discover()) {
                    is AuthOperationResult.Success -> discovery.value
                    is AuthOperationResult.Failure -> return discovery
                }

        val request =
            TokenRequest.Builder(
                serviceConfiguration,
                configuration.clientId,
            )
                .setGrantType(GrantTypeValues.REFRESH_TOKEN)
                .setRefreshToken(refreshToken)
                .setScope(tokenSet.scope)
                .build()

        return performTokenRequest(
            request,
            previous = tokenSet,
        )
    }

    override fun createEndSessionLaunch(
        idToken: String?,
    ): AuthorizationLaunch? {
        val serviceConfiguration =
            discoveredConfiguration ?: return null

        if (
            serviceConfiguration.endSessionEndpoint == null ||
            idToken.isNullOrBlank()
        ) {
            return null
        }

        val request =
            EndSessionRequest.Builder(serviceConfiguration)
                .setIdTokenHint(idToken)
                .setPostLogoutRedirectUri(
                    Uri.parse(
                        "${Uri.parse(configuration.redirectUri).scheme}://logout/callback",
                    ),
                )
                .build()

        return AuthorizationLaunch(
            authorizationService.getEndSessionRequestIntent(request),
        )
    }

    private suspend fun discover():
        AuthOperationResult<AuthorizationServiceConfiguration> =
        suspendCancellableCoroutine { continuation ->

            AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse(configuration.issuer),
                { config, exception ->
                    if (!continuation.isActive) {
                        return@fetchFromIssuer
                    }

                    if (config != null) {
                        discoveredConfiguration = config

                        continuation.resume(
                            AuthOperationResult.Success(config),
                        )
                    } else {
                        continuation.resume(
                            AuthOperationResult.Failure(
                                if (exception?.cause is IOException) {
                                    AuthFailure.NetworkUnavailable
                                } else {
                                    AuthFailure.DiscoveryFailed
                                },
                            ),
                        )
                    }
                },
                connectionBuilder,
            )
        }

    private suspend fun performTokenRequest(
        request: TokenRequest,
        previous: TokenSet? = null,
    ): AuthOperationResult<TokenSet> =
        suspendCancellableCoroutine { continuation ->

            authorizationService.performTokenRequest(request) { response, exception ->
                if (!continuation.isActive) {
                    return@performTokenRequest
                }

                if (response == null) {
                    continuation.resume(
                        AuthOperationResult.Failure(
                            exception.toFailure(previous != null),
                        ),
                    )

                    return@performTokenRequest
                }

                continuation.resume(
                    response.toTokenSet(previous),
                )
            }
        }

    private fun TokenResponse.toTokenSet(
        previous: TokenSet?,
    ): AuthOperationResult<TokenSet> {
        val accessToken =
            accessToken
                ?: return AuthOperationResult.Failure(
                    AuthFailure.MalformedTokenResponse,
                )

        val expiresAt =
            accessTokenExpirationTime
                ?: return AuthOperationResult.Failure(
                    AuthFailure.MalformedTokenResponse,
                )

        return AuthOperationResult.Success(
            TokenSet(
                accessToken = accessToken,
                refreshToken = refreshToken ?: previous?.refreshToken,
                idToken = idToken ?: previous?.idToken,
                tokenType = tokenType ?: "Bearer",
                accessTokenExpiresAtEpochMillis = expiresAt,
                refreshTokenExpiresAtEpochMillis =
                    previous?.refreshTokenExpiresAtEpochMillis,
                scope = scope ?: previous?.scope,
            ),
        )
    }

    private fun AuthorizationException?.toFailure(
        isRefresh: Boolean,
    ): AuthFailure =
        when {
            this?.cause is IOException ->
                AuthFailure.NetworkUnavailable

            isRefresh &&
                (this?.error == "invalid_grant" || this?.code == 2002) ->
                AuthFailure.RefreshRejected

            isRefresh ->
                AuthFailure.ServerFailure

            else ->
                AuthFailure.TokenExchangeFailed
        }

    private fun OidcConfiguration.isValid(): Boolean {
        if (
            issuer.isBlank() ||
            clientId.isBlank() ||
            redirectUri.isBlank() ||
            backendBaseUrl.isBlank()
        ) {
            return false
        }

        val issuerUri = Uri.parse(issuer)
        val backendUri = Uri.parse(backendBaseUrl)
        val redirect = Uri.parse(redirectUri)

        val issuerHost = issuerUri.host?.lowercase()
        val backendHost = backendUri.host?.lowercase()

        val secure =
            issuerUri.scheme.equals("https", ignoreCase = true) &&
                backendUri.scheme.equals("https", ignoreCase = true)

        val developmentCleartext =
            allowCleartextForDevelopment &&
                issuerUri.scheme.equals("http", ignoreCase = true) &&
                backendUri.scheme.equals("http", ignoreCase = true) &&
                issuerHost != null &&
                backendHost != null &&
                issuerHost in LOCAL_DEVELOPMENT_HOSTS &&
                backendHost in LOCAL_DEVELOPMENT_HOSTS

        return (secure || developmentCleartext) &&
            !issuerUri.host.isNullOrBlank() &&
            !backendUri.host.isNullOrBlank() &&
            !redirect.scheme.isNullOrBlank() &&
            !redirect.host.isNullOrBlank() &&
            !redirect.path.isNullOrBlank() &&
            !redirect.scheme.equals("http", ignoreCase = true) &&
            !redirect.scheme.equals("https", ignoreCase = true)
    }
}