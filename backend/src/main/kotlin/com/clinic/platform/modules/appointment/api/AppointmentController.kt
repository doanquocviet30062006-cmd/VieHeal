package com.clinic.platform.modules.appointment.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.appointment.api.dto.AppointmentResponse
import com.clinic.platform.modules.appointment.api.dto.CancelAppointmentRequest
import com.clinic.platform.modules.appointment.api.dto.CreateAppointmentRequest
import com.clinic.platform.modules.appointment.api.dto.RescheduleAppointmentRequest
import com.clinic.platform.modules.appointment.api.dto.UpdateAppointmentReasonRequest
import com.clinic.platform.modules.appointment.application.CancelAppointmentUseCase
import com.clinic.platform.modules.appointment.application.CompleteAppointmentUseCase
import com.clinic.platform.modules.appointment.application.CreateAppointmentUseCase
import com.clinic.platform.modules.appointment.application.GetAppointmentUseCase
import com.clinic.platform.modules.appointment.application.ListAppointmentsUseCase
import com.clinic.platform.modules.appointment.application.MarkAppointmentNoShowUseCase
import com.clinic.platform.modules.appointment.application.RescheduleAppointmentUseCase
import com.clinic.platform.modules.appointment.application.UpdateAppointmentReasonUseCase
import com.clinic.platform.shared.security.PermissionCodes
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class AppointmentController(
    private val createAppointmentUseCase:
        CreateAppointmentUseCase,
    private val getAppointmentUseCase:
        GetAppointmentUseCase,
    private val listAppointmentsUseCase:
        ListAppointmentsUseCase,
    private val rescheduleAppointmentUseCase:
        RescheduleAppointmentUseCase,
    private val updateAppointmentReasonUseCase:
        UpdateAppointmentReasonUseCase,
    private val cancelAppointmentUseCase:
        CancelAppointmentUseCase,
    private val completeAppointmentUseCase:
        CompleteAppointmentUseCase,
    private val markAppointmentNoShowUseCase:
        MarkAppointmentNoShowUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService,
    private val currentIamUserResolver:
        CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/appointments"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @Valid
        @RequestBody
        request: CreateAppointmentRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): AppointmentResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission =
                    PermissionCodes.APPOINTMENT_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val appointment =
            createAppointmentUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                patientId = request.patientId,
                practitionerId =
                    request.practitionerId,
                facilityServiceId =
                    request.facilityServiceId,
                scheduledStartAt =
                    request.scheduledStartAt,
                reason = request.reason,
                actorUserId =
                    currentUser.id
            )

        return AppointmentResponse.from(
            appointment
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/appointments"
    )
    fun list(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<AppointmentResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission =
                    PermissionCodes.APPOINTMENT_READ
            )

        return listAppointmentsUseCase
            .execute(
                organizationId = organizationId,
                facilityId = facilityId
            )
            .map(
                AppointmentResponse::from
            )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        appointmentId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): AppointmentResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission =
                    PermissionCodes.APPOINTMENT_READ
            )

        val appointment =
            getAppointmentUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointmentId
            )

        return AppointmentResponse.from(
            appointment
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/reschedule"
    )
    fun reschedule(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        appointmentId: UUID,

        @Valid
        @RequestBody
        request: RescheduleAppointmentRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): AppointmentResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val appointment =
            rescheduleAppointmentUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointmentId,
                scheduledStartAt =
                    request.scheduledStartAt,
                actorUserId =
                    currentUser.id
            )

        return AppointmentResponse.from(
            appointment
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/reason"
    )
    fun updateReason(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        appointmentId: UUID,

        @Valid
        @RequestBody
        request: UpdateAppointmentReasonRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): AppointmentResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val appointment =
            updateAppointmentReasonUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointmentId,
                reason = request.reason,
                actorUserId =
                    currentUser.id
            )

        return AppointmentResponse.from(
            appointment
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/cancel"
    )
    fun cancel(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        appointmentId: UUID,

        @Valid
        @RequestBody
        request: CancelAppointmentRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): AppointmentResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val appointment =
            cancelAppointmentUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointmentId,
                cancellationReason =
                    request.cancellationReason,
                actorUserId =
                    currentUser.id
            )

        return AppointmentResponse.from(
            appointment
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/complete"
    )
    fun complete(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        appointmentId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): AppointmentResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val appointment =
            completeAppointmentUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointmentId,
                actorUserId =
                    currentUser.id
            )

        return AppointmentResponse.from(
            appointment
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/no-show"
    )
    fun markNoShow(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        appointmentId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): AppointmentResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val appointment =
            markAppointmentNoShowUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId = appointmentId,
                actorUserId =
                    currentUser.id
            )

        return AppointmentResponse.from(
            appointment
        )
    }

    private fun requireUpdatePermission(
        jwt: Jwt,
        organizationId: UUID
    ) {
        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission =
                    PermissionCodes.APPOINTMENT_UPDATE
            )
    }
}