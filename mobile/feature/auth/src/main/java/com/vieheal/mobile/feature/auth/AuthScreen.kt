package com.vieheal.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    onSignIn: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                text = uiState.title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = uiState.message,
                modifier = Modifier.padding(top = VieHealSpacing.Medium),
                style = MaterialTheme.typography.bodyLarge,
            )
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = VieHealSpacing.Large))
            }
            if (uiState.canSignIn) {
                Button(
                    onClick = onSignIn,
                    modifier = Modifier.padding(top = VieHealSpacing.Large),
                ) {
                    Text(stringResource(R.string.auth_sign_in))
                }
            }
            if (uiState.canRetry) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = VieHealSpacing.Large),
                ) {
                    Text(stringResource(R.string.auth_retry))
                }
            }
            if (uiState.canCancel) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.padding(top = VieHealSpacing.Small),
                ) {
                    Text(stringResource(R.string.auth_cancel))
                }
            }
            if (uiState.canSignOut) {
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier.padding(top = VieHealSpacing.Small),
                ) {
                    Text(stringResource(R.string.auth_sign_out))
                }
            }
        }
    }
}
