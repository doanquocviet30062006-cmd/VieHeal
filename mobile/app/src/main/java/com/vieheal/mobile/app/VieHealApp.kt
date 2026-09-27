package com.vieheal.mobile.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vieheal.mobile.R
import com.vieheal.mobile.app.navigation.AppNavHost
import com.vieheal.mobile.core.designsystem.VieHealTheme
import com.vieheal.mobile.core.ui.VieHealError
import com.vieheal.mobile.core.ui.VieHealLoading

@Composable
fun VieHealApp(
    container: AppContainer,
    appViewModel: AppViewModel =
        viewModel(factory = AppViewModel.factory(container.sessionStateProvider)),
) {
    val startupState by appViewModel.startupState.collectAsStateWithLifecycle()

    VieHealTheme {
        Surface {
            when (val state = startupState) {
                AppStartupState.Initializing ->
                    VieHealLoading(label = stringResource(R.string.startup_loading))

                AppStartupState.RequiresAuthentication,
                AppStartupState.Authenticated,
                -> AppNavHost(startupState = state)

                is AppStartupState.FatalConfigurationError ->
                    VieHealError(
                        title = stringResource(R.string.configuration_error_title),
                        message = state.reason,
                    )
            }
        }
    }
}
