package com.vieheal.mobile.feature.auth

data class AuthUiState(
    val title: String,
    val message: String,
    val isLoading: Boolean = false,
    val canSignIn: Boolean = false,
    val canRetry: Boolean = false,
    val canCancel: Boolean = false,
    val canSignOut: Boolean = false,
)
