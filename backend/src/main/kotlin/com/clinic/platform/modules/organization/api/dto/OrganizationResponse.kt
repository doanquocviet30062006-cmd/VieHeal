package com.clinic.platform.modules.organization.api.dto

import com.clinic.platform.modules.organization.domain.Organization
import com.clinic.platform.modules.organization.domain.OrganizationStatus
import java.time.Instant
import java.util.UUID

data class OrganizationResponse(
    val id: UUID,
    val code: String,
    val name: String,
    val status: OrganizationStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun fromDomain(
            organization: Organization
        ): OrganizationResponse {

            return OrganizationResponse(
                id = organization.id,
                code = organization.code,
                name = organization.name,
                status = organization.status,
                createdAt = organization.createdAt,
                updatedAt = organization.updatedAt
            )
        }
    }
}