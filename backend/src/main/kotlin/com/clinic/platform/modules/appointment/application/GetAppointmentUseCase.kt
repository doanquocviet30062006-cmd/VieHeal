package com.clinic.platform.modules.appointment.application

import com.clinic.platform.modules.appointment.domain.Appointment
import com.clinic.platform.modules.appointment.domain.AppointmentRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        appointmentId: UUID
    ): Appointment {

        val appointment =
            appointmentRepository.findById(appointmentId)
                ?: throw ResourceNotFoundException(
                    "Appointment not found in facility: $appointmentId"
                )

        if (
            appointment.organizationId != organizationId ||
            appointment.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Appointment not found in facility: $appointmentId"
            )
        }

        return appointment
    }
}