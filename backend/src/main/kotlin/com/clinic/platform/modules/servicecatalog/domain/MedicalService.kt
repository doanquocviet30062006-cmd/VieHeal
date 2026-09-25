package com.clinic.platform.modules.servicecatalog.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.util.UUID

data class MedicalService(
    val id: UUID,
    val organizationId: UUID,
    val serviceCode: String,
    val name: String,
    val description: String?,
    val category: String?,
    val defaultDurationMinutes: Int,
    val status: ServiceStatus,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            serviceCode: String,
            name: String,
            description: String?,
            category: String?,
            defaultDurationMinutes: Int,
            actorUserId: UUID
        ): MedicalService {

            val normalizedServiceCode =
                serviceCode
                    .trim()
                    .uppercase()

            val normalizedName =
                name.trim()

            val normalizedDescription =
                normalizeOptional(description)

            val normalizedCategory =
                normalizeOptional(category)

            validateServiceCode(
                normalizedServiceCode
            )

            validateName(
                normalizedName
            )

            validateDescription(
                normalizedDescription
            )

            validateCategory(
                normalizedCategory
            )

            validateDuration(
                defaultDurationMinutes
            )

            val now = Instant.now()

            return MedicalService(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                serviceCode = normalizedServiceCode,
                name = normalizedName,
                description = normalizedDescription,
                category = normalizedCategory,
                defaultDurationMinutes = defaultDurationMinutes,
                status = ServiceStatus.ACTIVE,
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

        private fun validateServiceCode(
            serviceCode: String
        ) {
            if (serviceCode.isBlank()) {
                throw DomainValidationException(
                    message =
                        "Service code must not be blank",
                    code =
                        "SERVICE_CODE_BLANK"
                )
            }

            if (serviceCode.length > 50) {
                throw DomainValidationException(
                    message =
                        "Service code must not exceed 50 characters",
                    code =
                        "SERVICE_CODE_TOO_LONG"
                )
            }
        }

        private fun validateName(
            name: String
        ) {
            if (name.isBlank()) {
                throw DomainValidationException(
                    message =
                        "Service name must not be blank",
                    code =
                        "SERVICE_NAME_BLANK"
                )
            }

            if (name.length > 255) {
                throw DomainValidationException(
                    message =
                        "Service name must not exceed 255 characters",
                    code =
                        "SERVICE_NAME_TOO_LONG"
                )
            }
        }

        private fun validateDescription(
            description: String?
        ) {
            if (
                description != null &&
                description.length > 1000
            ) {
                throw DomainValidationException(
                    message =
                        "Service description must not exceed 1000 characters",
                    code =
                        "SERVICE_DESCRIPTION_TOO_LONG"
                )
            }
        }

        private fun validateCategory(
            category: String?
        ) {
            if (
                category != null &&
                category.length > 100
            ) {
                throw DomainValidationException(
                    message =
                        "Service category must not exceed 100 characters",
                    code =
                        "SERVICE_CATEGORY_TOO_LONG"
                )
            }
        }

        private fun validateDuration(
            defaultDurationMinutes: Int
        ) {
            if (defaultDurationMinutes <= 0) {
                throw DomainValidationException(
                    message =
                        "Service default duration must be greater than zero",
                    code =
                        "SERVICE_DURATION_INVALID"
                )
            }
        }
    }

    fun updateProfile(
        name: String,
        description: String?,
        category: String?,
        defaultDurationMinutes: Int,
        actorUserId: UUID
    ): MedicalService {

        val normalizedName =
            name.trim()

        val normalizedDescription =
            normalizeOptional(description)

        val normalizedCategory =
            normalizeOptional(category)

        validateName(
            normalizedName
        )

        validateDescription(
            normalizedDescription
        )

        validateCategory(
            normalizedCategory
        )

        validateDuration(
            defaultDurationMinutes
        )

        return copy(
            name = normalizedName,
            description = normalizedDescription,
            category = normalizedCategory,
            defaultDurationMinutes = defaultDurationMinutes,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}