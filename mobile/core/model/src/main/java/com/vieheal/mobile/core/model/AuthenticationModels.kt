package com.vieheal.mobile.core.model

data class OidcConfiguration(
    val issuer: String,
    val clientId: String,
    val redirectUri: String,
    val backendBaseUrl: String,
    val allowCleartextForDevelopment: Boolean = false,
)

data class TokenSet(
    val accessToken: String,
    val refreshToken: String?,
    val idToken: String?,
    val tokenType: String,
    val accessTokenExpiresAtEpochMillis: Long,
    val refreshTokenExpiresAtEpochMillis: Long? = null,
    val scope: String? = null,
) {
    fun isAccessTokenUsable(nowEpochMillis: Long, skewMillis: Long = 30_000): Boolean =
        accessTokenExpiresAtEpochMillis > nowEpochMillis + skewMillis

    fun isRefreshTokenUsable(nowEpochMillis: Long): Boolean =
        refreshToken != null &&
            (refreshTokenExpiresAtEpochMillis == null || refreshTokenExpiresAtEpochMillis > nowEpochMillis)

    override fun toString(): String = "TokenSet([REDACTED])"
}

data class AuthenticatedPrincipal(
    val userId: String,
    val externalSubject: String,
    val displayName: String,
)

data class FacilityContext(
    val facilityId: String,
)

data class OrganizationContext(
    val membershipId: String,
    val organizationId: String,
    val roles: Set<String>,
    val permissions: Set<String>,
    val facilities: List<FacilityContext>,
)

data class UserSessionContext(
    val principal: AuthenticatedPrincipal,
    val systemRoles: Set<String>,
    val systemPermissions: Set<String>,
    val organizations: List<OrganizationContext>,
)

sealed interface ApplicationSessionResult {
    data class Success(val context: UserSessionContext) : ApplicationSessionResult
    data object Unauthorized : ApplicationSessionResult
    data object Forbidden : ApplicationSessionResult
    data object NetworkUnavailable : ApplicationSessionResult
    data class ServerFailure(val statusCode: Int) : ApplicationSessionResult
    data object MalformedResponse : ApplicationSessionResult
}

fun interface ApplicationSessionGateway {
    suspend fun resolve(accessToken: String): ApplicationSessionResult
}
