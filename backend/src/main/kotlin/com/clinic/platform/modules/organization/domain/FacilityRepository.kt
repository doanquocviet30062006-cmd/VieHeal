package com.clinic.platform.modules.organization.domain

import java.util.UUID

interface FacilityRepository {

    fun save(facility: Facility): Facility

    fun findById(id: UUID): Facility?

    fun findAllByOrganizationId(
        organizationId: UUID
    ): List<Facility>

    fun existsByOrganizationIdAndCode(
        organizationId: UUID,
        code: String
    ): Boolean
}