package com.vieheal.mobile.core.security

import android.content.Intent
import com.vieheal.mobile.core.model.TokenSet

interface OidcClient {
    suspend fun prepareAuthorization(): AuthOperationResult<AuthorizationLaunch>
    suspend fun exchangeAuthorizationResponse(data: Intent?): AuthOperationResult<TokenSet>
    suspend fun refresh(tokenSet: TokenSet): AuthOperationResult<TokenSet>
    fun createEndSessionLaunch(idToken: String?): AuthorizationLaunch?
}
