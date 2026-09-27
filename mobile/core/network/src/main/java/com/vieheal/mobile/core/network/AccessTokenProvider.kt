package com.vieheal.mobile.core.network

/** Implemented by the OIDC session layer in P2. Null means no usable session. */
fun interface AccessTokenProvider {
    suspend fun accessToken(): String?
}
