package com.clinic.platform.modules.encounter.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.encounter.api.dto.CancelEncounterRequest
import com.clinic.platform.modules.encounter.api.dto.ClinicalNoteResponse
import com.clinic.platform.modules.encounter.api.dto.EncounterResponse
import com.clinic.platform.modules.encounter.api.dto.StartEncounterRequest
import com.clinic.platform.modules.encounter.api.dto.UpdateClinicalNoteRequest
import com.clinic.platform.modules.encounter.application.CancelEncounterUseCase
import com.clinic.platform.modules.encounter.application.CompleteEncounterUseCase
import com.clinic.platform.modules.encounter.application.GetClinicalNoteUseCase
import com.clinic.platform.modules.encounter.application.GetEncounterUseCase
import com.clinic.platform.modules.encounter.application.ListEncountersUseCase
import com.clinic.platform.modules.encounter.application.StartEncounterUseCase
import com.clinic.platform.modules.encounter.application.UpdateClinicalNoteUseCase
import com.clinic.platform.modules.encounter.domain.EncounterStatus
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
class EncounterController(
    private val startEncounterUseCase:
        StartEncounterUseCase,
    private val getEncounterUseCase:
        GetEncounterUseCase,
    private val listEncountersUseCase:
        ListEncountersUseCase,
    private val getClinicalNoteUseCase:
        GetClinicalNoteUseCase,
    private val updateClinicalNoteUseCase:
        UpdateClinicalNoteUseCase,
    private val completeEncounterUseCase:
        CompleteEncounterUseCase,
    private val cancelEncounterUseCase:
        CancelEncounterUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService,
    private val currentIamUserResolver:
        CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/encounters"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun start(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @Valid
        @RequestBody
        request: StartEncounterRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): EncounterResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId =
                    organizationId,
                permission =
                    PermissionCodes.ENCOUNTER_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(
                jwt
            )

        val encounter =
            startEncounterUseCase.execute(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                queueEntryId =
                    request.queueEntryId,
                actorUserId =
                    currentUser.id
            )

        return EncounterResponse.from(
            encounter
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/encounters"
    )
    fun list(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @RequestParam(required = false)
        status: EncounterStatus?,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<EncounterResponse> {

        requireEncounterReadPermission(
            jwt = jwt,
            organizationId =
                organizationId
        )

        return listEncountersUseCase
            .execute(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                status =
                    status
            )
            .map {
                EncounterResponse.from(it)
            }
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        encounterId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): EncounterResponse {

        requireEncounterReadPermission(
            jwt = jwt,
            organizationId =
                organizationId
        )

        val encounter =
            getEncounterUseCase.execute(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                encounterId =
                    encounterId
            )

        return EncounterResponse.from(
            encounter
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}/clinical-note"
    )
    fun getClinicalNote(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        encounterId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): ClinicalNoteResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId =
                    organizationId,
                permission =
                    PermissionCodes.CLINICAL_NOTE_READ
            )

        val clinicalNote =
            getClinicalNoteUseCase.execute(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                encounterId =
                    encounterId
            )

        return ClinicalNoteResponse.from(
            clinicalNote
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}/clinical-note"
    )
    fun updateClinicalNote(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        encounterId: UUID,

        @Valid
        @RequestBody
        request: UpdateClinicalNoteRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): ClinicalNoteResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId =
                    organizationId,
                permission =
                    PermissionCodes.CLINICAL_NOTE_UPDATE
            )

        val currentUser =
            currentIamUserResolver.resolve(
                jwt
            )

        val clinicalNote =
            updateClinicalNoteUseCase.execute(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                encounterId =
                    encounterId,
                chiefComplaint =
                    request.chiefComplaint,
                subjective =
                    request.subjective,
                objective =
                    request.objective,
                assessment =
                    request.assessment,
                plan =
                    request.plan,
                actorUserId =
                    currentUser.id
            )

        return ClinicalNoteResponse.from(
            clinicalNote
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}/complete"
    )
    fun complete(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        encounterId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): EncounterResponse {

        requireEncounterUpdatePermission(
            jwt = jwt,
            organizationId =
                organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(
                jwt
            )

        val encounter =
            completeEncounterUseCase.execute(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                encounterId =
                    encounterId,
                actorUserId =
                    currentUser.id
            )

        return EncounterResponse.from(
            encounter
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}/cancel"
    )
    fun cancel(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        encounterId: UUID,

        @Valid
        @RequestBody
        request: CancelEncounterRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): EncounterResponse {

        requireEncounterUpdatePermission(
            jwt = jwt,
            organizationId =
                organizationId
        )

        val currentUser =
            currentIamUserResolver.resolve(
                jwt
            )

        val encounter =
            cancelEncounterUseCase.execute(
                organizationId =
                    organizationId,
                facilityId =
                    facilityId,
                encounterId =
                    encounterId,
                cancellationReason =
                    request.cancellationReason,
                actorUserId =
                    currentUser.id
            )

        return EncounterResponse.from(
            encounter
        )
    }

    private fun requireEncounterReadPermission(
        jwt: Jwt,
        organizationId: UUID
    ) {
        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId =
                    organizationId,
                permission =
                    PermissionCodes.ENCOUNTER_READ
            )
    }

    private fun requireEncounterUpdatePermission(
        jwt: Jwt,
        organizationId: UUID
    ) {
        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId =
                    organizationId,
                permission =
                    PermissionCodes.ENCOUNTER_UPDATE
            )
    }
}