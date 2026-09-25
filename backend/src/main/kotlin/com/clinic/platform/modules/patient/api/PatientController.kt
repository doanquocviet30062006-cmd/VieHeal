package com.clinic.platform.modules.patient.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.patient.api.dto.CreatePatientRequest
import com.clinic.platform.modules.patient.api.dto.PatientResponse
import com.clinic.platform.modules.patient.api.dto.UpdatePatientRequest
import com.clinic.platform.modules.patient.application.CreatePatientUseCase
import com.clinic.platform.modules.patient.application.GetPatientUseCase
import com.clinic.platform.modules.patient.application.ListPatientsUseCase
import com.clinic.platform.modules.patient.application.UpdatePatientUseCase
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
class PatientController(
    private val createPatientUseCase: CreatePatientUseCase,
    private val getPatientUseCase: GetPatientUseCase,
    private val listPatientsUseCase: ListPatientsUseCase,
    private val updatePatientUseCase: UpdatePatientUseCase,
    private val accessAuthorizationService: AccessAuthorizationService,
    private val currentIamUserResolver: CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/patients"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @Valid
        @RequestBody
        request: CreatePatientRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PatientResponse {

        accessAuthorizationService
            .requireFacilityPermission(
                jwt = jwt,
                facilityId = facilityId,
                permission = PermissionCodes.PATIENT_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val patient =
            createPatientUseCase.execute(
                organizationId = organizationId,
                managingFacilityId = facilityId,
                patientCode = request.patientCode,
                fullName = request.fullName,
                dateOfBirth = request.dateOfBirth,
                sex = request.sex,
                phone = request.phone,
                email = request.email,
                addressLine = request.addressLine,
                ward = request.ward,
                district = request.district,
                province = request.province,
                countryCode = request.countryCode,
                actorUserId = currentUser.id
            )

        return PatientResponse.from(patient)
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/patients"
    )
    fun list(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<PatientResponse> {

        accessAuthorizationService
            .requireFacilityPermission(
                jwt = jwt,
                facilityId = facilityId,
                permission = PermissionCodes.PATIENT_READ
            )

        return listPatientsUseCase
            .execute(
                organizationId = organizationId,
                managingFacilityId = facilityId
            )
            .map(PatientResponse::from)
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/patients/{patientId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        patientId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PatientResponse {

        accessAuthorizationService
            .requireFacilityPermission(
                jwt = jwt,
                facilityId = facilityId,
                permission = PermissionCodes.PATIENT_READ
            )

        val patient =
            getPatientUseCase.execute(
                organizationId = organizationId,
                managingFacilityId = facilityId,
                patientId = patientId
            )

        return PatientResponse.from(patient)
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/patients/{patientId}"
    )
    fun update(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        patientId: UUID,

        @Valid
        @RequestBody
        request: UpdatePatientRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PatientResponse {

        accessAuthorizationService
            .requireFacilityPermission(
                jwt = jwt,
                facilityId = facilityId,
                permission = PermissionCodes.PATIENT_UPDATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val patient =
            updatePatientUseCase.execute(
                organizationId = organizationId,
                managingFacilityId = facilityId,
                patientId = patientId,
                fullName = request.fullName,
                dateOfBirth = request.dateOfBirth,
                sex = request.sex,
                phone = request.phone,
                email = request.email,
                addressLine = request.addressLine,
                ward = request.ward,
                district = request.district,
                province = request.province,
                countryCode = request.countryCode,
                actorUserId = currentUser.id
            )

        return PatientResponse.from(patient)
    }
}