package com.clinic.platform.shared.errors

class ResourceNotFoundException(
    message: String
) : RuntimeException(message)