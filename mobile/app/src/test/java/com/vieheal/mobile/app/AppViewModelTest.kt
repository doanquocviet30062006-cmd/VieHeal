package com.vieheal.mobile.app

import com.vieheal.mobile.core.model.AuthenticatedPrincipal
import com.vieheal.mobile.core.model.UserSessionContext
import com.vieheal.mobile.core.navigation.TopLevelDestination
import com.vieheal.mobile.core.security.SessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppViewModelTest {
    private val context =
        UserSessionContext(
            principal = AuthenticatedPrincipal("user-1", "subject-1", "User"),
            systemRoles = emptySet(),
            systemPermissions = emptySet(),
            organizations = emptyList(),
        )

    @Test
    fun `unauthenticated session resolves only to auth graph`() {
        val startupState = resolveStartupState(SessionState.Unauthenticated())
        assertEquals(AppStartupState.RequiresAuthentication(), startupState)
        assertEquals(TopLevelDestination.Auth, startupState.topLevelDestination())
    }

    @Test
    fun `backend-authorized session is the only state mapped to home`() {
        val startupState = resolveStartupState(SessionState.Authenticated(context))
        assertEquals(AppStartupState.Authenticated(context), startupState)
        assertEquals(TopLevelDestination.Home, startupState.topLevelDestination())
    }

    @Test
    fun `configuration failure remains inside auth boundary`() {
        val startupState = resolveStartupState(SessionState.ConfigurationError("Missing issuer"))
        assertEquals(AppStartupState.ConfigurationError("Missing issuer"), startupState)
        assertEquals(TopLevelDestination.Auth, startupState.topLevelDestination())
    }

    @Test
    fun `initializing has no navigation destination`() {
        assertNull(AppStartupState.Initializing.topLevelDestination())
    }
}
