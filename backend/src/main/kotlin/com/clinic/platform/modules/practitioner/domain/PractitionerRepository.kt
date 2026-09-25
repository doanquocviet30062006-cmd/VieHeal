package com.clinic.platform.modules.practitioner.domain

import java.util.UUID

interface PractitionerRepository {

    fun save(
        practitioner: Practitioner
    ): Practitioner

    fun findById(
        id: UUID
    ): Practitioner?

    fun findByOrganizationIdAndPractitionerCode(
        organizationId: UUID,
        practitionerCode: String
    ): Practitioner?

    fun existsByOrganizationIdAndPractitionerCode(
        organizationId: UUID,
        practitionerCode: String
    ): Boolean

    fun findByOrganizationIdAndMembershipId(
        organizationId: UUID,
        membershipId: UUID
    ): Practitioner?

    fun existsByOrganizationIdAndMembershipId(
        organizationId: UUID,
        membershipId: UUID
    ): Boolean

    fun findAllByOrganizationId(
        organizationId: UUID
    ): List<Practitioner>
}