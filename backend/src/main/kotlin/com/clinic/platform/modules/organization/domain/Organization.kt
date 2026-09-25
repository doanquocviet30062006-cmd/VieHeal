package com.clinic.platform.modules.organization.domain

import java.time.Instant
import java.util.UUID

data class Organization(
    val id: UUID,
    val code: String,
    val name: String,
    val status: OrganizationStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            code: String,
            name: String
        ): Organization {

            val normalizedCode = code.trim().uppercase()
            val normalizedName = name.trim()

            require(normalizedCode.isNotBlank()) {
                "Organization code must not be blank"
            }

            require(normalizedName.isNotBlank()) {
                "Organization name must not be blank"
            }

            val now = Instant.now()

            return Organization(
                id = UUID.randomUUID(),
                code = normalizedCode,
                name = normalizedName,
                status = OrganizationStatus.ACTIVE,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}