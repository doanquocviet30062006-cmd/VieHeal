package com.vieheal.mobile.app

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vieheal.mobile.R
import com.vieheal.mobile.app.navigation.AppNavHost
import com.vieheal.mobile.core.designsystem.VieHealTheme
import com.vieheal.mobile.core.ui.VieHealLoading
import com.vieheal.mobile.feature.auth.AuthUiEffect
import com.vieheal.mobile.feature.auth.AuthViewModel

@Composable
fun VieHealApp(
    container: AppContainer,
    appViewModel: AppViewModel =
        viewModel(factory = AppViewModel.factory(container.sessionController)),
    authViewModel: AuthViewModel =
        viewModel(factory = AuthViewModel.factory(container.sessionController)),
) {
    val startupState by appViewModel.startupState.collectAsStateWithLifecycle()
    val sessionState by authViewModel.sessionState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val authorizationLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            authViewModel.completeAuthorization(result.data)
        }

    LaunchedEffect(authViewModel) {
        authViewModel.effects.collect { effect ->
            when (effect) {
                is AuthUiEffect.LaunchAuthorization -> authorizationLauncher.launch(effect.launch.intent)
                is AuthUiEffect.LaunchEndSession ->
                    runCatching { context.startActivity(effect.launch.intent) }
            }
        }
    }

    VieHealTheme {
        Surface {
            if (startupState == AppStartupState.Initializing) {
                VieHealLoading(label = stringResource(R.string.startup_loading))
            } else {
                AppNavHost(
                    startupState = startupState,
                    authUiState = authViewModel.uiState(sessionState),
                    onSignIn = authViewModel::signIn,
                    onRetry = authViewModel::retry,
                    onCancel = authViewModel::cancelSignIn,
                    onSignOut = authViewModel::signOut,
                )
            }
        }
    }
}
