package com.vieheal.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.vieheal.mobile.core.designsystem.VieHealSpacing

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    modifier: Modifier = Modifier,
) {
    check(!uiState.isAuthenticated) {
        "AuthScreen cannot render an authenticated session"
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(VieHealSpacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.auth_title),
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(R.string.auth_not_configured),
                modifier = Modifier.padding(top = VieHealSpacing.Medium),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.auth_future_scope),
                modifier = Modifier.padding(top = VieHealSpacing.Small),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
