package com.vieheal.mobile.core.common

sealed interface Outcome<out T> {
    data class Success<T>(val value: T) : Outcome<T>

    data class Failure(val cause: Throwable) : Outcome<Nothing>
}
