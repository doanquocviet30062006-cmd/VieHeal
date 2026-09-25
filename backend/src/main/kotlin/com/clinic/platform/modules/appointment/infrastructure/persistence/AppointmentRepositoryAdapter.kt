package com.clinic.platform.modules.appointment.infrastructure.persistence

import com.clinic.platform.modules.appointment.domain.Appointment
import com.clinic.platform.modules.appointment.domain.AppointmentRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class AppointmentRepositoryAdapter(
    private val appointmentJpaRepository: AppointmentJpaRepository
) : AppointmentRepository {

    override fun save(
        appointment: Appointment
    ): Appointment =
        appointmentJpaRepository
            .save(
                appointment.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): Appointment? =
        appointmentJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findAllByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): List<Appointment> =
        appointmentJpaRepository
            .findAllByOrganizationIdAndFacilityId(
                organizationId = organizationId,
                facilityId = facilityId
            )
            .map {
                it.toDomain()
            }

    override fun existsScheduledPractitionerOverlap(
        organizationId: UUID,
        practitionerId: UUID,
        scheduledStartAt: Instant,
        scheduledEndAt: Instant,
        excludeAppointmentId: UUID?
    ): Boolean =
        if (excludeAppointmentId == null) {
            appointmentJpaRepository
                .existsScheduledPractitionerOverlap(
                    organizationId = organizationId,
                    practitionerId = practitionerId,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt
                )
        } else {
            appointmentJpaRepository
                .existsScheduledPractitionerOverlapExcluding(
                    organizationId = organizationId,
                    practitionerId = practitionerId,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt,
                    excludeAppointmentId = excludeAppointmentId
                )
        }

    override fun existsScheduledPatientOverlap(
        organizationId: UUID,
        patientId: UUID,
        scheduledStartAt: Instant,
        scheduledEndAt: Instant,
        excludeAppointmentId: UUID?
    ): Boolean =
        if (excludeAppointmentId == null) {
            appointmentJpaRepository
                .existsScheduledPatientOverlap(
                    organizationId = organizationId,
                    patientId = patientId,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt
                )
        } else {
            appointmentJpaRepository
                .existsScheduledPatientOverlapExcluding(
                    organizationId = organizationId,
                    patientId = patientId,
                    scheduledStartAt = scheduledStartAt,
                    scheduledEndAt = scheduledEndAt,
                    excludeAppointmentId = excludeAppointmentId
                )
        }

    private fun Appointment.toJpaEntity(): AppointmentJpaEntity =
        AppointmentJpaEntity(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            patientId = patientId,
            practitionerId = practitionerId,
            facilityServiceId = facilityServiceId,
            scheduledStartAt = scheduledStartAt,
            scheduledEndAt = scheduledEndAt,
            status = status,
            reason = reason,
            cancellationReason = cancellationReason,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun AppointmentJpaEntity.toDomain(): Appointment =
        Appointment(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            patientId = patientId,
            practitionerId = practitionerId,
            facilityServiceId = facilityServiceId,
            scheduledStartAt = scheduledStartAt,
            scheduledEndAt = scheduledEndAt,
            status = status,
            reason = reason,
            cancellationReason = cancellationReason,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}