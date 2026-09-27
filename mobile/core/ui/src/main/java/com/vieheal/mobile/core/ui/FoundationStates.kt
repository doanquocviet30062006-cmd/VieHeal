package com.vieheal.mobile.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.vieheal.mobile.core.designsystem.VieHealSpacing

@Composable
fun VieHealLoading(
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .semantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
        Text(text = label, modifier = Modifier.padding(top = VieHealSpacing.Medium))
    }
}

@Composable
fun VieHealError(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) = MessageState(title = title, message = message, modifier = modifier)

@Composable
fun VieHealEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) = MessageState(title = title, message = message, modifier = modifier)

@Composable
fun VieHealPermissionDenied(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) = MessageState(title = title, message = message, modifier = modifier)

@Composable
fun VieHealConnectivityMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        modifier = modifier.padding(VieHealSpacing.Medium),
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun MessageState(
    title: String,
    message: String,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(VieHealSpacing.Large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = message,
            modifier = Modifier.padding(top = VieHealSpacing.Small),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
