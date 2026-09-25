package com.clinic.platform.modules.practitioner.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.util.UUID

data class Practitioner(
    val id: UUID,
    val organizationId: UUID,
    val membershipId: UUID,
    val practitionerCode: String,
    val fullName: String,
    val practitionerType: PractitionerType,
    val licenseNumber: String?,
    val specialty: String?,
    val phone: String?,
    val email: String?,
    val status: PractitionerStatus,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            membershipId: UUID,
            practitionerCode: String,
            fullName: String,
            practitionerType: PractitionerType,
            licenseNumber: String?,
            specialty: String?,
            phone: String?,
            email: String?,
            actorUserId: UUID
        ): Practitioner {

            val normalizedPractitionerCode =
                practitionerCode
                    .trim()
                    .uppercase()

            val normalizedFullName =
                fullName.trim()

            val normalizedLicenseNumber =
                normalizeOptional(licenseNumber)

            val normalizedSpecialty =
                normalizeOptional(specialty)

            val normalizedPhone =
                normalizeOptional(phone)

            val normalizedEmail =
                normalizeOptional(email)
                    ?.lowercase()

            validatePractitionerCode(
                normalizedPractitionerCode
            )

            validateFullName(
                normalizedFullName
            )

            validateOptionalFieldLengths(
                licenseNumber = normalizedLicenseNumber,
                specialty = normalizedSpecialty,
                phone = normalizedPhone,
                email = normalizedEmail
            )

            val now = Instant.now()

            return Practitioner(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                membershipId = membershipId,
                practitionerCode = normalizedPractitionerCode,
                fullName = normalizedFullName,
                practitionerType = practitionerType,
                licenseNumber = normalizedLicenseNumber,
                specialty = normalizedSpecialty,
                phone = normalizedPhone,
                email = normalizedEmail,
                status = PractitionerStatus.ACTIVE,

                createdByUserId = actorUserId,
                updatedByUserId = actorUserId,

                createdAt = now,
                updatedAt = now
            )
        }

        private fun normalizeOptional(
            value: String?
        ): String? =
            value
                ?.trim()
                ?.takeIf { it.isNotBlank() }

        private fun validatePractitionerCode(
            practitionerCode: String
        ) {
            if (practitionerCode.isBlank()) {
                throw DomainValidationException(
                    message =
                        "Practitioner code must not be blank",
                    code =
                        "PRACTITIONER_CODE_BLANK"
                )
            }

            if (practitionerCode.length > 50) {
                throw DomainValidationException(
                    message =
                        "Practitioner code must not exceed 50 characters",
                    code =
                        "PRACTITIONER_CODE_TOO_LONG"
                )
            }
        }

        private fun validateFullName(
            fullName: String
        ) {
            if (fullName.isBlank()) {
                throw DomainValidationException(
                    message =
                        "Practitioner full name must not be blank",
                    code =
                        "PRACTITIONER_FULL_NAME_BLANK"
                )
            }

            if (fullName.length > 255) {
                throw DomainValidationException(
                    message =
                        "Practitioner full name must not exceed 255 characters",
                    code =
                        "PRACTITIONER_FULL_NAME_TOO_LONG"
                )
            }
        }

        private fun validateOptionalFieldLengths(
            licenseNumber: String?,
            specialty: String?,
            phone: String?,
            email: String?
        ) {
            if (
                licenseNumber != null &&
                licenseNumber.length > 100
            ) {
                throw DomainValidationException(
                    message =
                        "Practitioner license number must not exceed 100 characters",
                    code =
                        "PRACTITIONER_LICENSE_NUMBER_TOO_LONG"
                )
            }

            if (
                specialty != null &&
                specialty.length > 150
            ) {
                throw DomainValidationException(
                    message =
                        "Practitioner specialty must not exceed 150 characters",
                    code =
                        "PRACTITIONER_SPECIALTY_TOO_LONG"
                )
            }

            if (
                phone != null &&
                phone.length > 30
            ) {
                throw DomainValidationException(
                    message =
                        "Practitioner phone must not exceed 30 characters",
                    code =
                        "PRACTITIONER_PHONE_TOO_LONG"
                )
            }

            if (
                email != null &&
                email.length > 255
            ) {
                throw DomainValidationException(
                    message =
                        "Practitioner email must not exceed 255 characters",
                    code =
                        "PRACTITIONER_EMAIL_TOO_LONG"
                )
            }
        }
    }

    fun updateProfile(
        fullName: String,
        practitionerType: PractitionerType,
        licenseNumber: String?,
        specialty: String?,
        phone: String?,
        email: String?,
        actorUserId: UUID
    ): Practitioner {

        val normalizedFullName =
            fullName.trim()

        val normalizedLicenseNumber =
            normalizeOptional(licenseNumber)

        val normalizedSpecialty =
            normalizeOptional(specialty)

        val normalizedPhone =
            normalizeOptional(phone)

        val normalizedEmail =
            normalizeOptional(email)
                ?.lowercase()

        validateFullName(
            normalizedFullName
        )

        validateOptionalFieldLengths(
            licenseNumber = normalizedLicenseNumber,
            specialty = normalizedSpecialty,
            phone = normalizedPhone,
            email = normalizedEmail
        )

        return copy(
            fullName = normalizedFullName,
            practitionerType = practitionerType,
            licenseNumber = normalizedLicenseNumber,
            specialty = normalizedSpecialty,
            phone = normalizedPhone,
            email = normalizedEmail,

            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}