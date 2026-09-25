package com.clinic.platform.modules.patient.infrastructure.persistence

import com.clinic.platform.modules.patient.domain.PatientSex
import com.clinic.platform.modules.patient.domain.PatientStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(
    name = "patients",
    schema = "patient"
)
class PatientJpaEntity(

    @Id
    @Column(
        name = "id",
        nullable = false
    )
    var id: UUID,

    @Column(
        name = "organization_id",
        nullable = false
    )
    var organizationId: UUID,

    @Column(
        name = "managing_facility_id",
        nullable = false
    )
    var managingFacilityId: UUID,

    @Column(
        name = "patient_code",
        nullable = false,
        length = 50
    )
    var patientCode: String,

    @Column(
        name = "full_name",
        nullable = false,
        length = 255
    )
    var fullName: String,

    @Column(
        name = "date_of_birth"
    )
    var dateOfBirth: LocalDate?,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "sex",
        nullable = false,
        length = 20
    )
    var sex: PatientSex,

    @Column(
        name = "phone",
        length = 30
    )
    var phone: String?,

    @Column(
        name = "email",
        length = 255
    )
    var email: String?,

    @Column(
        name = "address_line",
        length = 255
    )
    var addressLine: String?,

    @Column(
        name = "ward",
        length = 100
    )
    var ward: String?,

    @Column(
        name = "district",
        length = 100
    )
    var district: String?,

    @Column(
        name = "province",
        length = 100
    )
    var province: String?,

    @Column(
        name = "country_code",
        nullable = false,
        length = 2
    )
    var countryCode: String,

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    var status: PatientStatus,

    @Column(
        name = "created_by_user_id",
        nullable = false
    )
    var createdByUserId: UUID,

    @Column(
        name = "updated_by_user_id",
        nullable = false
    )
    var updatedByUserId: UUID,

    @Column(
        name = "created_at",
        nullable = false
    )
    var createdAt: Instant,

    @Column(
        name = "updated_at",
        nullable = false
    )
    var updatedAt: Instant
)
