package com.clinic.platform.modules.organization.domain

import java.util.UUID

interface OrganizationRepository {

    fun save(
        organization: Organization
    ): Organization

    fun findById(
        id: UUID
    ): Organization?

    fun existsByCode(
        code: String
    ): Boolean
}