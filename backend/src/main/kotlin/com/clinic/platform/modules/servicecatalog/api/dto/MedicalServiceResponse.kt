package com.clinic.platform.modules.servicecatalog.api.dto

import com.clinic.platform.modules.servicecatalog.domain.MedicalService
import java.time.Instant
import java.util.UUID

data class MedicalServiceResponse(
    val id: UUID,
    val organizationId: UUID,
    val serviceCode: String,
    val name: String,
    val description: String?,
    val category: String?,
    val defaultDurationMinutes: Int,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            medicalService: MedicalService
        ): MedicalServiceResponse =
            MedicalServiceResponse(
                id = medicalService.id,
                organizationId = medicalService.organizationId,
                serviceCode = medicalService.serviceCode,
                name = medicalService.name,
                description = medicalService.description,
                category = medicalService.category,
                defaultDurationMinutes =
                    medicalService.defaultDurationMinutes,
                status = medicalService.status.name,
                createdAt = medicalService.createdAt,
                updatedAt = medicalService.updatedAt
            )
    }
}