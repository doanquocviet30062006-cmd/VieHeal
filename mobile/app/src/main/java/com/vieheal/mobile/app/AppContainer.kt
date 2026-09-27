package com.vieheal.mobile.app

import android.content.Context
import com.vieheal.mobile.BuildConfig
import com.vieheal.mobile.core.model.OidcConfiguration
import com.vieheal.mobile.core.network.HttpApplicationSessionGateway
import com.vieheal.mobile.core.security.AndroidKeystoreTokenStore
import com.vieheal.mobile.core.security.AppAuthOidcClient
import com.vieheal.mobile.core.security.AuthSessionManager
import com.vieheal.mobile.core.security.SessionController
import com.vieheal.mobile.core.security.SessionStateProvider

interface AppContainer {
    val sessionStateProvider: SessionStateProvider
    val sessionController: SessionController
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val configuration =
        OidcConfiguration(
            issuer = BuildConfig.OIDC_ISSUER,
            clientId = BuildConfig.OIDC_CLIENT_ID,
            redirectUri = BuildConfig.OIDC_REDIRECT_URI,
            backendBaseUrl = BuildConfig.BACKEND_BASE_URL,
            allowCleartextForDevelopment = BuildConfig.DEBUG,
        )

    private val sessionManager =
        AuthSessionManager(
            tokenStore = AndroidKeystoreTokenStore(context),
            oidcClient = AppAuthOidcClient(context, configuration),
            applicationSessionGateway = HttpApplicationSessionGateway(configuration.backendBaseUrl),
        )

    override val sessionStateProvider: SessionStateProvider = sessionManager
    override val sessionController: SessionController = sessionManager
}
