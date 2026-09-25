package com.clinic.platform.modules.servicecatalog.domain

import java.util.UUID

interface MedicalServiceRepository {

    fun save(
        service: MedicalService
    ): MedicalService

    fun findById(
        id: UUID
    ): MedicalService?

    fun findByOrganizationIdAndServiceCode(
        organizationId: UUID,
        serviceCode: String
    ): MedicalService?

    fun existsByOrganizationIdAndServiceCode(
        organizationId: UUID,
        serviceCode: String
    ): Boolean

    fun findAllByOrganizationId(
        organizationId: UUID
    ): List<MedicalService>
}