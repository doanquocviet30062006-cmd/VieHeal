package com.clinic.platform.modules.scheduling.infrastructure.persistence

import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityException
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityExceptionRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PractitionerAvailabilityExceptionRepositoryAdapter(
    private val practitionerAvailabilityExceptionJpaRepository:
        PractitionerAvailabilityExceptionJpaRepository
) : PractitionerAvailabilityExceptionRepository {

    override fun save(
        exception: PractitionerAvailabilityException
    ): PractitionerAvailabilityException =
        practitionerAvailabilityExceptionJpaRepository
            .save(
                exception.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): PractitionerAvailabilityException? =
        practitionerAvailabilityExceptionJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findAllByOrganizationIdAndFacilityIdAndPractitionerId(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID
    ): List<PractitionerAvailabilityException> =
        practitionerAvailabilityExceptionJpaRepository
            .findAllByOrganizationIdAndFacilityIdAndPractitionerId(
                organizationId,
                facilityId,
                practitionerId
            )
            .map {
                it.toDomain()
            }

    private fun PractitionerAvailabilityException.toJpaEntity():
        PractitionerAvailabilityExceptionJpaEntity =
        PractitionerAvailabilityExceptionJpaEntity(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            practitionerId = practitionerId,
            exceptionType = exceptionType,
            startAt = startAt,
            endAt = endAt,
            reason = reason,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun PractitionerAvailabilityExceptionJpaEntity.toDomain():
        PractitionerAvailabilityException =
        PractitionerAvailabilityException(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            practitionerId = practitionerId,
            exceptionType = exceptionType,
            startAt = startAt,
            endAt = endAt,
            reason = reason,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}