package com.clinic.platform.shared.errors

class ResourceStateConflictException(
    message: String,
    val code: String,
    val currentState: String
) : RuntimeException(message)