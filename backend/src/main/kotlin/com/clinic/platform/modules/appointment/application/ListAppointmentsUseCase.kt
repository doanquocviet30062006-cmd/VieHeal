package com.clinic.platform.modules.appointment.application

import com.clinic.platform.modules.appointment.domain.Appointment
import com.clinic.platform.modules.appointment.domain.AppointmentRepository
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListAppointmentsUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val appointmentRepository: AppointmentRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID
    ): List<Appointment> {

        organizationRepository.findById(organizationId)
            ?: throw ResourceNotFoundException(
                "Organization not found: $organizationId"
            )

        val facility =
            facilityRepository.findById(facilityId)
                ?: throw ResourceNotFoundException(
                    "Facility not found in organization: $facilityId"
                )

        if (facility.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Facility not found in organization: $facilityId"
            )
        }

        return appointmentRepository
            .findAllByOrganizationIdAndFacilityId(
                organizationId = organizationId,
                facilityId = facilityId
            )
    }
}