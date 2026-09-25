package com.clinic.platform.shared.errors

class DomainValidationException(
    message: String,
    val code: String
) : RuntimeException(message)