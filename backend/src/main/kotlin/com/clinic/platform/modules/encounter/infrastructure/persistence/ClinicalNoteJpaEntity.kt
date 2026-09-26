package com.clinic.platform.modules.encounter.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    schema = "encounter",
    name = "clinical_notes"
)
class ClinicalNoteJpaEntity(

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
        name = "facility_id",
        nullable = false
    )
    var facilityId: UUID,

    @Column(
        name = "encounter_id",
        nullable = false
    )
    var encounterId: UUID,

    @Column(
        name = "chief_complaint",
        length = 2000
    )
    var chiefComplaint: String?,

    @Column(
        name = "subjective",
        columnDefinition = "TEXT"
    )
    var subjective: String?,

    @Column(
        name = "objective",
        columnDefinition = "TEXT"
    )
    var objective: String?,

    @Column(
        name = "assessment",
        columnDefinition = "TEXT"
    )
    var assessment: String?,

    @Column(
        name = "plan",
        columnDefinition = "TEXT"
    )
    var plan: String?,

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