package com.vieheal.mobile.core.network

data class HttpClientPolicy(
    val connectTimeoutSeconds: Long = 15,
    val readTimeoutSeconds: Long = 30,
    val writeTimeoutSeconds: Long = 30,
    val logRequestMetadataInDebug: Boolean = false,
)
