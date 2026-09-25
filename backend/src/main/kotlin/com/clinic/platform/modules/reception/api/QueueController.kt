package com.clinic.platform.modules.reception.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.reception.api.dto.CheckInQueueEntryRequest
import com.clinic.platform.modules.reception.api.dto.QueueEntryResponse
import com.clinic.platform.modules.reception.api.dto.UpdateQueueEntryNoteRequest
import com.clinic.platform.modules.reception.application.CallQueueEntryUseCase
import com.clinic.platform.modules.reception.application.CancelQueueEntryUseCase
import com.clinic.platform.modules.reception.application.CheckInAppointmentUseCase
import com.clinic.platform.modules.reception.application.CompleteQueueEntryUseCase
import com.clinic.platform.modules.reception.application.GetQueueEntryUseCase
import com.clinic.platform.modules.reception.application.ListQueueEntriesUseCase
import com.clinic.platform.modules.reception.application.StartServingQueueEntryUseCase
import com.clinic.platform.modules.reception.application.UpdateQueueEntryNoteUseCase
import com.clinic.platform.modules.reception.domain.QueueEntryStatus
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class QueueController(
    private val checkInAppointmentUseCase:
        CheckInAppointmentUseCase,
    private val getQueueEntryUseCase:
        GetQueueEntryUseCase,
    private val listQueueEntriesUseCase:
        ListQueueEntriesUseCase,
    private val callQueueEntryUseCase:
        CallQueueEntryUseCase,
    private val startServingQueueEntryUseCase:
        StartServingQueueEntryUseCase,
    private val completeQueueEntryUseCase:
        CompleteQueueEntryUseCase,
    private val cancelQueueEntryUseCase:
        CancelQueueEntryUseCase,
    private val updateQueueEntryNoteUseCase:
        UpdateQueueEntryNoteUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService,
    private val currentIamUserResolver:
        CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/queue"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun checkIn(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @Valid
        @RequestBody
        request: CheckInQueueEntryRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): QueueEntryResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission =
                    PermissionCodes.QUEUE_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val queueEntry =
            checkInAppointmentUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                appointmentId =
                    request.appointmentId,
                note = request.note,
                actorUserId =
                    currentUser.id
            )

        return QueueEntryResponse.from(
            queueEntry
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/queue"
    )
    fun list(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @RequestParam(required = false)
        status: QueueEntryStatus?,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<QueueEntryResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission =
                    PermissionCodes.QUEUE_READ
            )

        return listQueueEntriesUseCase
            .execute(
                organizationId = organizationId,
                facilityId = facilityId,
                status = status
            )
            .map(
                QueueEntryResponse::from
            )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        queueEntryId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): QueueEntryResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission =
                    PermissionCodes.QUEUE_READ
            )

        val queueEntry =
            getQueueEntryUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                queueEntryId = queueEntryId
            )

        return QueueEntryResponse.from(
            queueEntry
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/call"
    )
    fun call(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        queueEntryId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): QueueEntryResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val queueEntry =
            callQueueEntryUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                queueEntryId = queueEntryId,
                actorUserId =
                    currentUser.id
            )

        return QueueEntryResponse.from(
            queueEntry
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/start-serving"
    )
    fun startServing(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        queueEntryId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): QueueEntryResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val queueEntry =
            startServingQueueEntryUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                queueEntryId = queueEntryId,
                actorUserId =
                    currentUser.id
            )

        return QueueEntryResponse.from(
            queueEntry
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/complete"
    )
    fun complete(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        queueEntryId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): QueueEntryResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val queueEntry =
            completeQueueEntryUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                queueEntryId = queueEntryId,
                actorUserId =
                    currentUser.id
            )

        return QueueEntryResponse.from(
            queueEntry
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/cancel"
    )
    fun cancel(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        queueEntryId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): QueueEntryResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val queueEntry =
            cancelQueueEntryUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                queueEntryId = queueEntryId,
                actorUserId =
                    currentUser.id
            )

        return QueueEntryResponse.from(
            queueEntry
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/note"
    )
    fun updateNote(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        queueEntryId: UUID,

        @Valid
        @RequestBody
        request: UpdateQueueEntryNoteRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): QueueEntryResponse {

        requireUpdatePermission(
            jwt = jwt,
            organizationId = organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val queueEntry =
            updateQueueEntryNoteUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                queueEntryId = queueEntryId,
                note = request.note,
                actorUserId =
                    currentUser.id
            )

        return QueueEntryResponse.from(
            queueEntry
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
                    PermissionCodes.QUEUE_UPDATE
            )
    }
}