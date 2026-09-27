package com.vieheal.mobile.app

import com.vieheal.mobile.core.navigation.TopLevelDestination
import com.vieheal.mobile.core.security.SessionState
import com.vieheal.mobile.core.security.SessionStateProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppViewModelTest {
    @Test
    fun `unauthenticated session resolves to auth and never promotes itself`() {
        val viewModel = AppViewModel(SessionStateProvider { SessionState.Unauthenticated })

        assertEquals(AppStartupState.RequiresAuthentication, viewModel.startupState.value)
        assertEquals(TopLevelDestination.Auth, viewModel.startupState.value.topLevelDestination())

        viewModel.refreshStartupState()

        assertEquals(AppStartupState.RequiresAuthentication, viewModel.startupState.value)
    }

    @Test
    fun `authenticated session is the only state mapped to home`() {
        val startupState = resolveStartupState(SessionState.Authenticated)

        assertEquals(AppStartupState.Authenticated, startupState)
        assertEquals(TopLevelDestination.Home, startupState.topLevelDestination())
    }

    @Test
    fun `configuration failure cannot resolve to a navigation destination`() {
        val startupState = resolveStartupState(SessionState.ConfigurationError("Missing issuer"))

        assertEquals(AppStartupState.FatalConfigurationError("Missing issuer"), startupState)
        assertNull(startupState.topLevelDestination())
    }
}
