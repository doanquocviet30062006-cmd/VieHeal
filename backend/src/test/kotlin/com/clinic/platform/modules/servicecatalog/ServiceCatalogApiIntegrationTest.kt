package com.clinic.platform.modules.servicecatalog

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.util.UUID
import kotlin.test.assertEquals

@Testcontainers
@SpringBootTest(
    properties = [
        "spring.jpa.open-in-view=false",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.jdbc.time_zone=UTC",
        "spring.flyway.enabled=true",
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:65535/test-jwks"
    ]
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ServiceCatalogApiIntegrationTest(
    @Autowired
    private val mockMvc: MockMvc,

    @Autowired
    private val jdbcTemplate: JdbcTemplate
) {

    companion object {

        @Container
        @ServiceConnection
        @JvmField
        val postgres =
            PostgreSQLContainer("postgres:17")

        private val ORGANIZATION_A_ID =
            UUID.fromString(
                "11111111-1111-1111-1111-111111111111"
            )

        private val ORGANIZATION_B_ID =
            UUID.fromString(
                "22222222-2222-2222-2222-222222222222"
            )

        private val FACILITY_A1_ID =
            UUID.fromString(
                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1"
            )

        private val FACILITY_B1_ID =
            UUID.fromString(
                "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1"
            )

        private val ADMIN_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000501"
            )

        private val MANAGER_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000502"
            )

        private val DOCTOR_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000503"
            )

        private val ADMIN_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000501"
            )

        private val ADMIN_B_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000502"
            )

        private val MANAGER_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000503"
            )

        private val DOCTOR_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000504"
            )

        private val ORGANIZATION_ADMIN_ROLE_ID =
            UUID.fromString(
                "00000000-0000-0000-0000-000000000002"
            )

        private val CLINIC_MANAGER_ROLE_ID =
            UUID.fromString(
                "00000000-0000-0000-0000-000000000003"
            )

        private val DOCTOR_ROLE_ID =
            UUID.fromString(
                "00000000-0000-0000-0000-000000000004"
            )

        private const val ADMIN_SUBJECT =
            "service-catalog-test-admin"

        private const val MANAGER_SUBJECT =
            "service-catalog-test-manager"

        private const val DOCTOR_SUBJECT =
            "service-catalog-test-doctor"
    }

    @BeforeEach
    fun setUp() {

        cleanBusinessData()

        seedOrganizationsAndFacilities()
        seedUsers()
        seedMemberships()
        seedMembershipRoles()

        /*
         * Intentionally no facility assignments are seeded.
         *
         * Service Catalog permissions are organization-scoped.
         * FacilityService application logic validates that the
         * target facility belongs to the organization.
         */
    }

    @Test
    fun `create medical service without JWT returns 401`() {

        mockMvc.perform(
            post(medicalServiceCollectionUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createMedicalServiceJson(
                        serviceCode = "SVC-401"
                    )
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `doctor cannot create medical service`() {

        mockMvc.perform(
            post(medicalServiceCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createMedicalServiceJson(
                        serviceCode = "SVC-DOCTOR"
                    )
                )
        )
            .andExpect(status().isForbidden)

        assertEquals(
            0L,
            medicalServiceCountByCode(
                organizationId = ORGANIZATION_A_ID,
                serviceCode = "SVC-DOCTOR"
            )
        )
    }

    @Test
    fun `organization admin can create medical service and audit actor is stored`() {

        mockMvc.perform(
            post(medicalServiceCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createMedicalServiceJson(
                        serviceCode = " svc-create-001 "
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.serviceCode")
                    .value("SVC-CREATE-001")
            )
            .andExpect(
                jsonPath("$.name")
                    .value("General Consultation")
            )
            .andExpect(
                jsonPath("$.defaultDurationMinutes")
                    .value(30)
            )
            .andExpect(
                jsonPath("$.status")
                    .value("ACTIVE")
            )
            .andExpect(
                jsonPath("$.organizationId")
                    .value(
                        ORGANIZATION_A_ID.toString()
                    )
            )

        val serviceId =
            medicalServiceIdByCode(
                organizationId = ORGANIZATION_A_ID,
                serviceCode = "SVC-CREATE-001"
            )

        assertEquals(
            ADMIN_USER_ID.toString(),
            medicalServiceCreatedBy(serviceId)
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            medicalServiceUpdatedBy(serviceId)
        )
    }

    @Test
    fun `clinic manager can create medical service`() {

        mockMvc.perform(
            post(medicalServiceCollectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createMedicalServiceJson(
                        serviceCode = "SVC-MANAGER"
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.serviceCode")
                    .value("SVC-MANAGER")
            )

        assertEquals(
            1L,
            medicalServiceCountByCode(
                organizationId = ORGANIZATION_A_ID,
                serviceCode = "SVC-MANAGER"
            )
        )
    }

    @Test
    fun `duplicate medical service code returns 409`() {

        createMedicalServiceAsAdmin(
            serviceCode = "SVC-DUPLICATE"
        )

        mockMvc.perform(
            post(medicalServiceCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createMedicalServiceJson(
                        serviceCode =
                            " svc-duplicate "
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1L,
            medicalServiceCountByCode(
                organizationId = ORGANIZATION_A_ID,
                serviceCode = "SVC-DUPLICATE"
            )
        )
    }

    @Test
    fun `invalid medical service duration returns 400`() {

        mockMvc.perform(
            post(medicalServiceCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createMedicalServiceJson(
                        serviceCode = "SVC-BAD-DURATION",
                        defaultDurationMinutes = 0
                    )
                )
        )
            .andExpect(status().isBadRequest)

        assertEquals(
            0L,
            medicalServiceCountByCode(
                organizationId = ORGANIZATION_A_ID,
                serviceCode = "SVC-BAD-DURATION"
            )
        )
    }

    @Test
    fun `doctor can get and list medical services`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-READ"
            )

        mockMvc.perform(
            get(
                medicalServiceItemUrl(
                    serviceId = serviceId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.serviceCode")
                    .value("SVC-READ")
            )

        mockMvc.perform(
            get(medicalServiceCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$[0].serviceCode")
                    .value("SVC-READ")
            )
    }

    @Test
    fun `medical service from another organization returns 404`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-ORG-A"
            )

        /*
         * ADMIN_SUBJECT has memberships in both A and B.
         * Authorization for organization B therefore succeeds,
         * then the application tenant check must hide the
         * organization A resource with 404.
         */
        mockMvc.perform(
            get(
                medicalServiceItemUrl(
                    serviceId = serviceId,
                    organizationId =
                        ORGANIZATION_B_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `organization admin can update medical service while identity remains unchanged`() {

        val serviceId =
            createMedicalServiceAsManager(
                serviceCode = "SVC-IMMUTABLE"
            )

        mockMvc.perform(
            put(
                medicalServiceItemUrl(
                    serviceId = serviceId
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    updateMedicalServiceJson(
                        name =
                            "Updated Consultation",
                        defaultDurationMinutes = 45
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.serviceCode")
                    .value("SVC-IMMUTABLE")
            )
            .andExpect(
                jsonPath("$.name")
                    .value("Updated Consultation")
            )
            .andExpect(
                jsonPath("$.defaultDurationMinutes")
                    .value(45)
            )

        assertEquals(
            "SVC-IMMUTABLE",
            medicalServiceCode(serviceId)
        )

        assertEquals(
            MANAGER_USER_ID.toString(),
            medicalServiceCreatedBy(serviceId)
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            medicalServiceUpdatedBy(serviceId)
        )
    }

    @Test
    fun `organization admin can configure service for facility without facility assignment`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-FACILITY"
            )

        mockMvc.perform(
            post(facilityServiceCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId = serviceId,
                        currencyCode = "vnd"
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.organizationId")
                    .value(
                        ORGANIZATION_A_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("$.facilityId")
                    .value(
                        FACILITY_A1_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("$.serviceId")
                    .value(serviceId.toString())
            )
            .andExpect(
                jsonPath("$.durationMinutes")
                    .value(30)
            )
            .andExpect(
                jsonPath("$.currencyCode")
                    .value("VND")
            )
            .andExpect(
                jsonPath("$.bookingEnabled")
                    .value(true)
            )
            .andExpect(
                jsonPath("$.status")
                    .value("ACTIVE")
            )

        val facilityServiceId =
            facilityServiceId(
                organizationId =
                    ORGANIZATION_A_ID,
                facilityId =
                    FACILITY_A1_ID,
                serviceId =
                    serviceId
            )

        assertEquals(
            ADMIN_USER_ID.toString(),
            facilityServiceCreatedBy(
                facilityServiceId
            )
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            facilityServiceUpdatedBy(
                facilityServiceId
            )
        )
    }

    @Test
    fun `doctor cannot configure service for facility`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-DOCTOR-FACILITY"
            )

        mockMvc.perform(
            post(facilityServiceCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId = serviceId
                    )
                )
        )
            .andExpect(status().isForbidden)

        assertEquals(
            0L,
            facilityServiceCount(
                organizationId =
                    ORGANIZATION_A_ID,
                facilityId =
                    FACILITY_A1_ID,
                serviceId =
                    serviceId
            )
        )
    }

    @Test
    fun `duplicate facility service returns 409`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-FACILITY-DUP"
            )

        createFacilityServiceAsAdmin(
            serviceId = serviceId
        )

        mockMvc.perform(
            post(facilityServiceCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId = serviceId
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1L,
            facilityServiceCount(
                organizationId =
                    ORGANIZATION_A_ID,
                facilityId =
                    FACILITY_A1_ID,
                serviceId =
                    serviceId
            )
        )
    }

    @Test
    fun `facility from another organization returns 404 when configuring service`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-CROSS-FACILITY"
            )

        mockMvc.perform(
            post(
                facilityServiceCollectionUrl(
                    organizationId =
                        ORGANIZATION_A_ID,
                    facilityId =
                        FACILITY_B1_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId = serviceId
                    )
                )
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `service from another organization returns 404 when configuring facility`() {

        val serviceIdFromB =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-ORG-B",
                organizationId =
                    ORGANIZATION_B_ID
            )

        mockMvc.perform(
            post(
                facilityServiceCollectionUrl(
                    organizationId =
                        ORGANIZATION_A_ID,
                    facilityId =
                        FACILITY_A1_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId =
                            serviceIdFromB
                    )
                )
        )
            .andExpect(status().isNotFound)

        assertEquals(
            0L,
            facilityServiceCount(
                organizationId =
                    ORGANIZATION_A_ID,
                facilityId =
                    FACILITY_A1_ID,
                serviceId =
                    serviceIdFromB
            )
        )
    }

    @Test
    fun `inactive medical service cannot be configured for facility`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-INACTIVE"
            )

        jdbcTemplate.update(
            """
            UPDATE service_catalog.services
            SET status = 'INACTIVE'
            WHERE id = ?
            """.trimIndent(),
            serviceId
        )

        mockMvc.perform(
            post(facilityServiceCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId = serviceId
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value("SERVICE_NOT_ACTIVE")
            )
    }

    @Test
    fun `doctor can get and list facility services`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-FACILITY-READ"
            )

        val facilityServiceId =
            createFacilityServiceAsAdmin(
                serviceId = serviceId
            )

        mockMvc.perform(
            get(
                facilityServiceItemUrl(
                    facilityServiceId =
                        facilityServiceId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(
                        facilityServiceId.toString()
                    )
            )
            .andExpect(
                jsonPath("$.serviceId")
                    .value(serviceId.toString())
            )

        mockMvc.perform(
            get(facilityServiceCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$[0].id")
                    .value(
                        facilityServiceId.toString()
                    )
            )
    }

    @Test
    fun `organization admin can update facility service and immutable identity remains unchanged`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-FACILITY-UPDATE"
            )

        val facilityServiceId =
            createFacilityServiceAsManager(
                serviceId = serviceId
            )

        mockMvc.perform(
            put(
                facilityServiceItemUrl(
                    facilityServiceId =
                        facilityServiceId
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    updateFacilityServiceJson(
                        durationMinutes = 60,
                        priceAmount = "350000.00",
                        currencyCode = "usd",
                        bookingEnabled = false
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(
                        facilityServiceId.toString()
                    )
            )
            .andExpect(
                jsonPath("$.facilityId")
                    .value(
                        FACILITY_A1_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("$.serviceId")
                    .value(serviceId.toString())
            )
            .andExpect(
                jsonPath("$.durationMinutes")
                    .value(60)
            )
            .andExpect(
                jsonPath("$.currencyCode")
                    .value("USD")
            )
            .andExpect(
                jsonPath("$.bookingEnabled")
                    .value(false)
            )

        assertEquals(
            ORGANIZATION_A_ID.toString(),
            facilityServiceOrganizationId(
                facilityServiceId
            )
        )

        assertEquals(
            FACILITY_A1_ID.toString(),
            facilityServiceFacilityId(
                facilityServiceId
            )
        )

        assertEquals(
            serviceId.toString(),
            facilityServiceMedicalServiceId(
                facilityServiceId
            )
        )

        assertEquals(
            MANAGER_USER_ID.toString(),
            facilityServiceCreatedBy(
                facilityServiceId
            )
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            facilityServiceUpdatedBy(
                facilityServiceId
            )
        )
    }

    @Test
    fun `invalid negative facility price returns 400`() {

        val serviceId =
            createMedicalServiceAsAdmin(
                serviceCode = "SVC-BAD-PRICE"
            )

        mockMvc.perform(
            post(facilityServiceCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId = serviceId,
                        priceAmount = "-1.00"
                    )
                )
        )
            .andExpect(status().isBadRequest)

        assertEquals(
            0L,
            facilityServiceCount(
                organizationId =
                    ORGANIZATION_A_ID,
                facilityId =
                    FACILITY_A1_ID,
                serviceId =
                    serviceId
            )
        )
    }

    private fun cleanBusinessData() {

        jdbcTemplate.update(
            "DELETE FROM service_catalog.facility_services"
        )

        jdbcTemplate.update(
            "DELETE FROM service_catalog.services"
        )

        jdbcTemplate.update(
            "DELETE FROM practitioner.practitioners"
        )

        jdbcTemplate.update(
            "DELETE FROM patient.patients"
        )

        jdbcTemplate.update(
            "DELETE FROM iam.facility_assignments"
        )

        jdbcTemplate.update(
            "DELETE FROM iam.membership_roles"
        )

        jdbcTemplate.update(
            "DELETE FROM iam.organization_memberships"
        )

        jdbcTemplate.update(
            "DELETE FROM iam.users"
        )

        jdbcTemplate.update(
            "DELETE FROM organization.facilities"
        )

        jdbcTemplate.update(
            "DELETE FROM organization.organizations"
        )
    }

    private fun seedOrganizationsAndFacilities() {

        jdbcTemplate.update(
            """
            INSERT INTO organization.organizations (
                id,
                code,
                name,
                status
            )
            VALUES (?, 'ORG-A', 'Organization A', 'ACTIVE')
            """.trimIndent(),
            ORGANIZATION_A_ID
        )

        jdbcTemplate.update(
            """
            INSERT INTO organization.organizations (
                id,
                code,
                name,
                status
            )
            VALUES (?, 'ORG-B', 'Organization B', 'ACTIVE')
            """.trimIndent(),
            ORGANIZATION_B_ID
        )

        jdbcTemplate.update(
            """
            INSERT INTO organization.facilities (
                id,
                organization_id,
                code,
                name,
                country_code,
                status
            )
            VALUES (?, ?, 'FAC-A1', 'Facility A1', 'VN', 'ACTIVE')
            """.trimIndent(),
            FACILITY_A1_ID,
            ORGANIZATION_A_ID
        )

        jdbcTemplate.update(
            """
            INSERT INTO organization.facilities (
                id,
                organization_id,
                code,
                name,
                country_code,
                status
            )
            VALUES (?, ?, 'FAC-B1', 'Facility B1', 'VN', 'ACTIVE')
            """.trimIndent(),
            FACILITY_B1_ID,
            ORGANIZATION_B_ID
        )
    }

    private fun seedUsers() {

        insertUser(
            id = ADMIN_USER_ID,
            subject = ADMIN_SUBJECT,
            email = "catalog-admin@example.com",
            displayName = "Catalog Admin"
        )

        insertUser(
            id = MANAGER_USER_ID,
            subject = MANAGER_SUBJECT,
            email = "catalog-manager@example.com",
            displayName = "Catalog Manager"
        )

        insertUser(
            id = DOCTOR_USER_ID,
            subject = DOCTOR_SUBJECT,
            email = "catalog-doctor@example.com",
            displayName = "Catalog Doctor"
        )
    }

    private fun insertUser(
        id: UUID,
        subject: String,
        email: String,
        displayName: String
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO iam.users (
                id,
                external_subject,
                identity_provider,
                email,
                display_name,
                status
            )
            VALUES (?, ?, 'keycloak', ?, ?, 'ACTIVE')
            """.trimIndent(),
            id,
            subject,
            email,
            displayName
        )
    }

    private fun seedMemberships() {

        insertMembership(
            id = ADMIN_A_MEMBERSHIP_ID,
            userId = ADMIN_USER_ID,
            organizationId = ORGANIZATION_A_ID
        )

        insertMembership(
            id = ADMIN_B_MEMBERSHIP_ID,
            userId = ADMIN_USER_ID,
            organizationId = ORGANIZATION_B_ID
        )

        insertMembership(
            id = MANAGER_A_MEMBERSHIP_ID,
            userId = MANAGER_USER_ID,
            organizationId = ORGANIZATION_A_ID
        )

        insertMembership(
            id = DOCTOR_A_MEMBERSHIP_ID,
            userId = DOCTOR_USER_ID,
            organizationId = ORGANIZATION_A_ID
        )
    }

    private fun insertMembership(
        id: UUID,
        userId: UUID,
        organizationId: UUID
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO iam.organization_memberships (
                id,
                user_id,
                organization_id,
                status
            )
            VALUES (?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            id,
            userId,
            organizationId
        )
    }

    private fun seedMembershipRoles() {

        insertMembershipRole(
            membershipId =
                ADMIN_A_MEMBERSHIP_ID,
            roleId =
                ORGANIZATION_ADMIN_ROLE_ID
        )

        insertMembershipRole(
            membershipId =
                ADMIN_B_MEMBERSHIP_ID,
            roleId =
                ORGANIZATION_ADMIN_ROLE_ID
        )

        insertMembershipRole(
            membershipId =
                MANAGER_A_MEMBERSHIP_ID,
            roleId =
                CLINIC_MANAGER_ROLE_ID
        )

        insertMembershipRole(
            membershipId =
                DOCTOR_A_MEMBERSHIP_ID,
            roleId =
                DOCTOR_ROLE_ID
        )
    }

    private fun insertMembershipRole(
        membershipId: UUID,
        roleId: UUID
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO iam.membership_roles (
                membership_id,
                role_id
            )
            VALUES (?, ?)
            """.trimIndent(),
            membershipId,
            roleId
        )
    }

    private fun createMedicalServiceAsAdmin(
        serviceCode: String,
        organizationId: UUID =
            ORGANIZATION_A_ID
    ): UUID {

        mockMvc.perform(
            post(
                medicalServiceCollectionUrl(
                    organizationId
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createMedicalServiceJson(
                        serviceCode = serviceCode
                    )
                )
        )
            .andExpect(status().isCreated)

        return medicalServiceIdByCode(
            organizationId = organizationId,
            serviceCode =
                serviceCode.trim().uppercase()
        )
    }

    private fun createMedicalServiceAsManager(
        serviceCode: String
    ): UUID {

        mockMvc.perform(
            post(medicalServiceCollectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createMedicalServiceJson(
                        serviceCode = serviceCode
                    )
                )
        )
            .andExpect(status().isCreated)

        return medicalServiceIdByCode(
            organizationId =
                ORGANIZATION_A_ID,
            serviceCode =
                serviceCode.trim().uppercase()
        )
    }

    private fun createFacilityServiceAsAdmin(
        serviceId: UUID
    ): UUID {

        mockMvc.perform(
            post(facilityServiceCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId = serviceId
                    )
                )
        )
            .andExpect(status().isCreated)

        return facilityServiceId(
            organizationId =
                ORGANIZATION_A_ID,
            facilityId =
                FACILITY_A1_ID,
            serviceId =
                serviceId
        )
    }

    private fun createFacilityServiceAsManager(
        serviceId: UUID
    ): UUID {

        mockMvc.perform(
            post(facilityServiceCollectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createFacilityServiceJson(
                        serviceId = serviceId
                    )
                )
        )
            .andExpect(status().isCreated)

        return facilityServiceId(
            organizationId =
                ORGANIZATION_A_ID,
            facilityId =
                FACILITY_A1_ID,
            serviceId =
                serviceId
        )
    }

    private fun jwtFor(
        subject: String
    ) =
        jwt().jwt {
            it.subject(subject)
        }

    private fun medicalServiceCollectionUrl(
        organizationId: UUID =
            ORGANIZATION_A_ID
    ): String =
        "/api/v1/organizations/" +
            "$organizationId/services"

    private fun medicalServiceItemUrl(
        serviceId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID
    ): String =
        medicalServiceCollectionUrl(
            organizationId
        ) + "/$serviceId"

    private fun facilityServiceCollectionUrl(
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        "/api/v1/organizations/" +
            "$organizationId/facilities/" +
            "$facilityId/services"

    private fun facilityServiceItemUrl(
        facilityServiceId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        facilityServiceCollectionUrl(
            organizationId = organizationId,
            facilityId = facilityId
        ) + "/$facilityServiceId"

    private fun createMedicalServiceJson(
        serviceCode: String,
        name: String =
            "General Consultation",
        defaultDurationMinutes: Int = 30
    ): String =
        """
        {
          "serviceCode": "$serviceCode",
          "name": "$name",
          "description": "General medical consultation",
          "category": "CONSULTATION",
          "defaultDurationMinutes": $defaultDurationMinutes
        }
        """.trimIndent()

    private fun updateMedicalServiceJson(
        name: String,
        defaultDurationMinutes: Int
    ): String =
        """
        {
          "name": "$name",
          "description": "Updated description",
          "category": "CONSULTATION",
          "defaultDurationMinutes": $defaultDurationMinutes
        }
        """.trimIndent()

    private fun createFacilityServiceJson(
        serviceId: UUID,
        durationMinutes: Int = 30,
        priceAmount: String = "250000.00",
        currencyCode: String = "VND",
        bookingEnabled: Boolean = true
    ): String =
        """
        {
          "serviceId": "$serviceId",
          "durationMinutes": $durationMinutes,
          "priceAmount": $priceAmount,
          "currencyCode": "$currencyCode",
          "bookingEnabled": $bookingEnabled
        }
        """.trimIndent()

    private fun updateFacilityServiceJson(
        durationMinutes: Int,
        priceAmount: String,
        currencyCode: String,
        bookingEnabled: Boolean
    ): String =
        """
        {
          "durationMinutes": $durationMinutes,
          "priceAmount": $priceAmount,
          "currencyCode": "$currencyCode",
          "bookingEnabled": $bookingEnabled
        }
        """.trimIndent()

    private fun medicalServiceIdByCode(
        organizationId: UUID,
        serviceCode: String
    ): UUID {

        val id =
            jdbcTemplate.queryForObject(
                """
                SELECT id::text
                FROM service_catalog.services
                WHERE organization_id = ?
                  AND service_code = ?
                """.trimIndent(),
                String::class.java,
                organizationId,
                serviceCode
            )

        return UUID.fromString(id)
    }

    private fun medicalServiceCountByCode(
        organizationId: UUID,
        serviceCode: String
    ): Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM service_catalog.services
            WHERE organization_id = ?
              AND service_code = ?
            """.trimIndent(),
            Long::class.javaObjectType,
            organizationId,
            serviceCode
        ) ?: 0L

    private fun medicalServiceCode(
        serviceId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT service_code
            FROM service_catalog.services
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            serviceId
        )

    private fun medicalServiceCreatedBy(
        serviceId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT created_by_user_id::text
            FROM service_catalog.services
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            serviceId
        )

    private fun medicalServiceUpdatedBy(
        serviceId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT updated_by_user_id::text
            FROM service_catalog.services
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            serviceId
        )

    private fun facilityServiceId(
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ): UUID {

        val id =
            jdbcTemplate.queryForObject(
                """
                SELECT id::text
                FROM service_catalog.facility_services
                WHERE organization_id = ?
                  AND facility_id = ?
                  AND service_id = ?
                """.trimIndent(),
                String::class.java,
                organizationId,
                facilityId,
                serviceId
            )

        return UUID.fromString(id)
    }

    private fun facilityServiceCount(
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ): Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM service_catalog.facility_services
            WHERE organization_id = ?
              AND facility_id = ?
              AND service_id = ?
            """.trimIndent(),
            Long::class.javaObjectType,
            organizationId,
            facilityId,
            serviceId
        ) ?: 0L

    private fun facilityServiceCreatedBy(
        facilityServiceId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT created_by_user_id::text
            FROM service_catalog.facility_services
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            facilityServiceId
        )

    private fun facilityServiceUpdatedBy(
        facilityServiceId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT updated_by_user_id::text
            FROM service_catalog.facility_services
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            facilityServiceId
        )

    private fun facilityServiceOrganizationId(
        facilityServiceId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT organization_id::text
            FROM service_catalog.facility_services
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            facilityServiceId
        )

    private fun facilityServiceFacilityId(
        facilityServiceId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT facility_id::text
            FROM service_catalog.facility_services
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            facilityServiceId
        )

    private fun facilityServiceMedicalServiceId(
        facilityServiceId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT service_id::text
            FROM service_catalog.facility_services
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            facilityServiceId
        )
}