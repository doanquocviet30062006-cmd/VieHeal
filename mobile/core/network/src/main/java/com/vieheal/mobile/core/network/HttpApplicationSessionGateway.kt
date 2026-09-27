package com.vieheal.mobile.core.network

import com.vieheal.mobile.core.model.ApplicationSessionGateway
import com.vieheal.mobile.core.model.ApplicationSessionResult
import com.vieheal.mobile.core.model.AuthenticatedPrincipal
import com.vieheal.mobile.core.model.FacilityContext
import com.vieheal.mobile.core.model.OrganizationContext
import com.vieheal.mobile.core.model.UserSessionContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

class HttpApplicationSessionGateway(
    private val baseUrl: String,
    private val policy: HttpClientPolicy = HttpClientPolicy(),
) : ApplicationSessionGateway {
    override suspend fun resolve(accessToken: String): ApplicationSessionResult =
        withContext(Dispatchers.IO) {
            val endpoint =
                runCatching { URI.create(baseUrl.trimEnd('/') + "/api/v1/auth/me").toURL() }
                    .getOrElse { return@withContext ApplicationSessionResult.MalformedResponse }
            val connection = endpoint.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = policy.connectTimeoutSeconds.toInt() * 1_000
                connection.readTimeout = policy.readTimeoutSeconds.toInt() * 1_000
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $accessToken")

                when (val status = connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> parseResponse(connection.inputStream.bufferedReader().use { it.readText() })
                    HttpURLConnection.HTTP_UNAUTHORIZED -> ApplicationSessionResult.Unauthorized
                    HttpURLConnection.HTTP_FORBIDDEN -> ApplicationSessionResult.Forbidden
                    in 500..599 -> ApplicationSessionResult.ServerFailure(status)
                    else -> ApplicationSessionResult.MalformedResponse
                }
            } catch (_: IOException) {
                ApplicationSessionResult.NetworkUnavailable
            } catch (_: RuntimeException) {
                ApplicationSessionResult.MalformedResponse
            } finally {
                connection.disconnect()
            }
        }

    private fun parseResponse(body: String): ApplicationSessionResult {
        val root = JSONObject(body)
        val user = root.getJSONObject("user")
        val context =
            UserSessionContext(
                principal =
                    AuthenticatedPrincipal(
                        userId = user.getString("id"),
                        externalSubject = user.getString("externalSubject"),
                        displayName = user.getString("displayName"),
                    ),
                systemRoles = root.getJSONArray("systemRoles").toStringSet(),
                systemPermissions = root.getJSONArray("systemPermissions").toStringSet(),
                organizations =
                    root.getJSONArray("organizations").objects().map { organization ->
                        OrganizationContext(
                            membershipId = organization.getString("membershipId"),
                            organizationId = organization.getString("organizationId"),
                            roles = organization.getJSONArray("roles").toStringSet(),
                            permissions = organization.getJSONArray("permissions").toStringSet(),
                            facilities =
                                organization.getJSONArray("facilities").objects().map { facility ->
                                    FacilityContext(facilityId = facility.getString("facilityId"))
                                },
                        )
                    },
            )
        return ApplicationSessionResult.Success(context)
    }

    private fun JSONArray.toStringSet(): Set<String> =
        (0 until length()).map { getString(it) }.toSet()

    private fun JSONArray.objects(): List<JSONObject> =
        (0 until length()).map { getJSONObject(it) }
}
