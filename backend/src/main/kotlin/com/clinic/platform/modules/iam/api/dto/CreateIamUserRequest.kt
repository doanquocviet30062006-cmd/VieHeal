package com.clinic.platform.modules.iam.api.dto

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateIamUserRequest(

    @field:Size(max = 255)
    val externalSubject: String? = null,

    @field:Size(max = 50)
    val identityProvider: String? = null,

    @field:Email
    @field:Size(max = 255)
    val email: String? = null,

    @field:Size(max = 30)
    val phone: String? = null,

    @field:NotBlank
    @field:Size(min = 2, max = 255)
    val displayName: String

) {

    @get:AssertTrue(
        message = "identityProvider and externalSubject must be provided together"
    )
    val validExternalIdentity: Boolean
        get() {
            val hasProvider = !identityProvider.isNullOrBlank()
            val hasSubject = !externalSubject.isNullOrBlank()

            return hasProvider == hasSubject
        }
}