package com.clinic.platform.modules.organization.infrastructure.persistence

import com.clinic.platform.modules.organization.domain.Facility
import com.clinic.platform.modules.organization.domain.FacilityStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "facilities",
    schema = "organization"
)
class FacilityJpaEntity(

    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "organization_id", nullable = false)
    var organizationId: UUID,

    @Column(name = "code", nullable = false, length = 50)
    var code: String,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "address_line", length = 500)
    var addressLine: String?,

    @Column(name = "ward", length = 150)
    var ward: String?,

    @Column(name = "district", length = 150)
    var district: String?,

    @Column(name = "province", length = 150)
    var province: String?,

    @Column(name = "country_code", nullable = false, length = 2)
    var countryCode: String,

    @Column(name = "phone", length = 30)
    var phone: String?,

    @Column(name = "email", length = 255)
    var email: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    var status: FacilityStatus,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
) {

    protected constructor() : this(
        id = UUID.randomUUID(),
        organizationId = UUID.randomUUID(),
        code = "",
        name = "",
        addressLine = null,
        ward = null,
        district = null,
        province = null,
        countryCode = "VN",
        phone = null,
        email = null,
        status = FacilityStatus.ACTIVE,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )

    fun toDomain(): Facility =
        Facility(
            id = id,
            organizationId = organizationId,
            code = code,
            name = name,
            addressLine = addressLine,
            ward = ward,
            district = district,
            province = province,
            countryCode = countryCode,
            phone = phone,
            email = email,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    companion object {

        fun fromDomain(
            facility: Facility
        ): FacilityJpaEntity =
            FacilityJpaEntity(
                id = facility.id,
                organizationId = facility.organizationId,
                code = facility.code,
                name = facility.name,
                addressLine = facility.addressLine,
                ward = facility.ward,
                district = facility.district,
                province = facility.province,
                countryCode = facility.countryCode,
                phone = facility.phone,
                email = facility.email,
                status = facility.status,
                createdAt = facility.createdAt,
                updatedAt = facility.updatedAt
            )
    }
}