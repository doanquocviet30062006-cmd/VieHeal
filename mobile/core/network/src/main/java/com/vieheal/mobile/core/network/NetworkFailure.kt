package com.vieheal.mobile.core.network

sealed interface NetworkFailure {
    data object NoConnectivity : NetworkFailure

    data class BadRequest(val message: String? = null) : NetworkFailure

    data object Unauthorized : NetworkFailure

    data object Forbidden : NetworkFailure

    data object NotFound : NetworkFailure

    data class Conflict(val message: String? = null) : NetworkFailure

    data class ServerError(val statusCode: Int) : NetworkFailure

    data class HttpError(val statusCode: Int) : NetworkFailure

    data class UnexpectedResponse(val cause: Throwable? = null) : NetworkFailure
}
