package com.clinic.platform.modules.practitioner

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
class PractitionerApiIntegrationTest(
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

        private val ADMIN_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000101"
            )

        private val DOCTOR_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000102"
            )

        private val MANAGER_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000103"
            )

        private val NURSE_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000104"
            )

        private val INACTIVE_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000105"
            )

        private val TARGET_B_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000106"
            )

        private val ADMIN_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000101"
            )

        private val DOCTOR_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000102"
            )

        private val MANAGER_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000103"
            )

        private val NURSE_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000104"
            )

        private val INACTIVE_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000105"
            )

        private val ADMIN_B_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000106"
            )

        private val TARGET_B_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000107"
            )

        private val NON_EXISTENT_PRACTITIONER_ID =
            UUID.fromString(
                "99999999-9999-9999-9999-999999999999"
            )

        private const val ADMIN_SUBJECT =
            "practitioner-test-admin"

        private const val DOCTOR_SUBJECT =
            "practitioner-test-doctor"

        private const val MANAGER_SUBJECT =
            "practitioner-test-manager"
    }

    @BeforeEach
    fun setUp() {

        cleanBusinessData()

        seedOrganizations()
        seedUsers()
        seedMemberships()
        seedMembershipRoles()
    }

    @Test
    fun `create practitioner without JWT returns 401`() {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        practitionerCode =
                            "PRAC-401"
                    )
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `doctor cannot create practitioner`() {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        practitionerCode =
                            "PRAC-DOCTOR-CREATE"
                    )
                )
        )
            .andExpect(status().isForbidden)

        assertEquals(
            0L,
            practitionerCountByCode(
                "PRAC-DOCTOR-CREATE"
            )
        )
    }

    @Test
    fun `organization admin can create practitioner and audit actor is stored`() {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        practitionerCode =
                            "PRAC-CREATE-001"
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.practitionerCode")
                    .value("PRAC-CREATE-001")
            )
            .andExpect(
                jsonPath("$.fullName")
                    .value("Nguyen Van Bac Si")
            )
            .andExpect(
                jsonPath("$.organizationId")
                    .value(
                        ORGANIZATION_A_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("$.membershipId")
                    .value(
                        DOCTOR_A_MEMBERSHIP_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("$.practitionerType")
                    .value("DOCTOR")
            )
            .andExpect(
                jsonPath("$.status")
                    .value("ACTIVE")
            )

        val practitionerId =
            practitionerIdByCode(
                "PRAC-CREATE-001"
            )

        assertEquals(
            ADMIN_USER_ID.toString(),
            createdByUserId(practitionerId)
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            updatedByUserId(practitionerId)
        )
    }

    @Test
    fun `clinic manager can create practitioner`() {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        membershipId =
                            NURSE_A_MEMBERSHIP_ID,
                        practitionerCode =
                            "PRAC-MANAGER-001",
                        practitionerType =
                            "NURSE"
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.practitionerCode")
                    .value("PRAC-MANAGER-001")
            )
            .andExpect(
                jsonPath("$.practitionerType")
                    .value("NURSE")
            )

        val practitionerId =
            practitionerIdByCode(
                "PRAC-MANAGER-001"
            )

        assertEquals(
            MANAGER_USER_ID.toString(),
            createdByUserId(practitionerId)
        )
    }

    @Test
    fun `duplicate practitioner code returns 409`() {

        createPractitionerAsAdmin(
            practitionerCode =
                "PRAC-DUPLICATE"
        )

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        membershipId =
                            NURSE_A_MEMBERSHIP_ID,
                        practitionerCode =
                            "PRAC-DUPLICATE",
                        practitionerType =
                            "NURSE"
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1L,
            practitionerCountByCode(
                "PRAC-DUPLICATE"
            )
        )
    }

    @Test
    fun `duplicate practitioner membership returns 409`() {

        createPractitionerAsAdmin(
            practitionerCode =
                "PRAC-MEMBER-001"
        )

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        membershipId =
                            DOCTOR_A_MEMBERSHIP_ID,
                        practitionerCode =
                            "PRAC-MEMBER-002"
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1L,
            practitionerCountByMembership(
                DOCTOR_A_MEMBERSHIP_ID
            )
        )
    }

    @Test
    fun `membership from another organization returns 404`() {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        membershipId =
                            TARGET_B_MEMBERSHIP_ID,
                        practitionerCode =
                            "PRAC-WRONG-ORG-MEMBER"
                    )
                )
        )
            .andExpect(status().isNotFound)

        assertEquals(
            0L,
            practitionerCountByCode(
                "PRAC-WRONG-ORG-MEMBER"
            )
        )
    }

    @Test
    fun `inactive membership returns 409`() {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        membershipId =
                            INACTIVE_A_MEMBERSHIP_ID,
                        practitionerCode =
                            "PRAC-INACTIVE"
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value("MEMBERSHIP_NOT_ACTIVE")
            )

        assertEquals(
            0L,
            practitionerCountByCode(
                "PRAC-INACTIVE"
            )
        )
    }

    @Test
    fun `get practitioner without JWT returns 401`() {

        val practitionerId =
            createPractitionerAsAdmin(
                practitionerCode =
                    "PRAC-GET-401"
            )

        mockMvc.perform(
            get(
                practitionerItemUrl(
                    practitionerId
                )
            )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `doctor can get and list practitioners`() {

        val practitionerId =
            createPractitionerAsAdmin(
                practitionerCode =
                    "PRAC-READ-001"
            )

        mockMvc.perform(
            get(
                practitionerItemUrl(
                    practitionerId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(practitionerId.toString())
            )
            .andExpect(
                jsonPath("$.practitionerCode")
                    .value("PRAC-READ-001")
            )

        mockMvc.perform(
            get(practitionerCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$[0].id")
                    .value(practitionerId.toString())
            )
            .andExpect(
                jsonPath("$[0].practitionerCode")
                    .value("PRAC-READ-001")
            )
    }

    @Test
    fun `get nonexistent practitioner returns 404`() {

        mockMvc.perform(
            get(
                practitionerItemUrl(
                    NON_EXISTENT_PRACTITIONER_ID
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get practitioner through wrong organization returns 404`() {

        val practitionerId =
            createPractitionerAsAdmin(
                practitionerCode =
                    "PRAC-WRONG-ORG"
            )

        mockMvc.perform(
            get(
                practitionerItemUrl(
                    practitionerId =
                        practitionerId,
                    organizationId =
                        ORGANIZATION_B_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `update practitioner without JWT returns 401`() {

        val practitionerId =
            createPractitionerAsAdmin(
                practitionerCode =
                    "PRAC-UPDATE-401"
            )

        mockMvc.perform(
            put(
                practitionerItemUrl(
                    practitionerId
                )
            )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    updatePractitionerJson()
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `doctor cannot update practitioner`() {

        val practitionerId =
            createPractitionerAsAdmin(
                practitionerCode =
                    "PRAC-DOCTOR-UPDATE"
            )

        mockMvc.perform(
            put(
                practitionerItemUrl(
                    practitionerId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    updatePractitionerJson()
                )
        )
            .andExpect(status().isForbidden)

        assertEquals(
            "Nguyen Van Bac Si",
            practitionerFullName(
                practitionerId
            )
        )
    }

    @Test
    fun `organization admin can update practitioner and audit fields are correct`() {

        val practitionerId =
            createPractitionerAsManager(
                practitionerCode =
                    "PRAC-UPDATE-001"
            )

        assertEquals(
            MANAGER_USER_ID.toString(),
            createdByUserId(practitionerId)
        )

        assertEquals(
            MANAGER_USER_ID.toString(),
            updatedByUserId(practitionerId)
        )

        mockMvc.perform(
            put(
                practitionerItemUrl(
                    practitionerId
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    updatePractitionerJson(
                        fullName =
                            "Nguyen Van Bac Si Updated",
                        licenseNumber =
                            "LIC-UPDATED-001",
                        specialty =
                            "Cardiology",
                        phone =
                            "0909999999",
                        email =
                            "doctor.updated@example.com"
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(practitionerId.toString())
            )
            .andExpect(
                jsonPath("$.practitionerCode")
                    .value("PRAC-UPDATE-001")
            )
            .andExpect(
                jsonPath("$.fullName")
                    .value(
                        "Nguyen Van Bac Si Updated"
                    )
            )
            .andExpect(
                jsonPath("$.licenseNumber")
                    .value("LIC-UPDATED-001")
            )
            .andExpect(
                jsonPath("$.specialty")
                    .value("Cardiology")
            )
            .andExpect(
                jsonPath("$.email")
                    .value(
                        "doctor.updated@example.com"
                    )
            )

        assertEquals(
            "PRAC-UPDATE-001",
            practitionerCode(
                practitionerId
            )
        )

        assertEquals(
            DOCTOR_A_MEMBERSHIP_ID.toString(),
            practitionerMembershipId(
                practitionerId
            )
        )

        assertEquals(
            MANAGER_USER_ID.toString(),
            createdByUserId(practitionerId)
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            updatedByUserId(practitionerId)
        )
    }

    @Test
    fun `invalid email on create returns 400 and is not persisted`() {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPractitionerJson(
                        practitionerCode =
                            "PRAC-INVALID-EMAIL",
                        email =
                            "not-an-email"
                    )
                )
        )
            .andExpect(status().isBadRequest)

        assertEquals(
            0L,
            practitionerCountByCode(
                "PRAC-INVALID-EMAIL"
            )
        )
    }

    private fun cleanBusinessData() {

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

    private fun seedOrganizations() {

        jdbcTemplate.update(
            """
            INSERT INTO organization.organizations (
                id,
                code,
                name,
                status
            )
            VALUES (?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            ORGANIZATION_A_ID,
            "ORG-A",
            "Practitioner Test Organization A"
        )

        jdbcTemplate.update(
            """
            INSERT INTO organization.organizations (
                id,
                code,
                name,
                status
            )
            VALUES (?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            ORGANIZATION_B_ID,
            "ORG-B",
            "Practitioner Test Organization B"
        )
    }

    private fun seedUsers() {

        insertUser(
            id = ADMIN_USER_ID,
            subject = ADMIN_SUBJECT,
            email = "practitioner-admin@example.com",
            displayName = "Practitioner Admin"
        )

        insertUser(
            id = DOCTOR_USER_ID,
            subject = DOCTOR_SUBJECT,
            email = "practitioner-doctor@example.com",
            displayName = "Practitioner Doctor"
        )

        insertUser(
            id = MANAGER_USER_ID,
            subject = MANAGER_SUBJECT,
            email = "practitioner-manager@example.com",
            displayName = "Practitioner Manager"
        )

        insertUser(
            id = NURSE_USER_ID,
            subject = "practitioner-test-nurse",
            email = "practitioner-nurse@example.com",
            displayName = "Practitioner Nurse"
        )

        insertUser(
            id = INACTIVE_USER_ID,
            subject = "practitioner-test-inactive",
            email = "practitioner-inactive@example.com",
            displayName = "Inactive Practitioner"
        )

        insertUser(
            id = TARGET_B_USER_ID,
            subject = "practitioner-test-org-b-target",
            email = "practitioner-org-b@example.com",
            displayName = "Organization B Target"
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
            organizationId = ORGANIZATION_A_ID,
            status = "ACTIVE"
        )

        insertMembership(
            id = DOCTOR_A_MEMBERSHIP_ID,
            userId = DOCTOR_USER_ID,
            organizationId = ORGANIZATION_A_ID,
            status = "ACTIVE"
        )

        insertMembership(
            id = MANAGER_A_MEMBERSHIP_ID,
            userId = MANAGER_USER_ID,
            organizationId = ORGANIZATION_A_ID,
            status = "ACTIVE"
        )

        insertMembership(
            id = NURSE_A_MEMBERSHIP_ID,
            userId = NURSE_USER_ID,
            organizationId = ORGANIZATION_A_ID,
            status = "ACTIVE"
        )

        insertMembership(
            id = INACTIVE_A_MEMBERSHIP_ID,
            userId = INACTIVE_USER_ID,
            organizationId = ORGANIZATION_A_ID,
            status = "INACTIVE"
        )

        insertMembership(
            id = ADMIN_B_MEMBERSHIP_ID,
            userId = ADMIN_USER_ID,
            organizationId = ORGANIZATION_B_ID,
            status = "ACTIVE"
        )

        insertMembership(
            id = TARGET_B_MEMBERSHIP_ID,
            userId = TARGET_B_USER_ID,
            organizationId = ORGANIZATION_B_ID,
            status = "ACTIVE"
        )
    }

    private fun insertMembership(
        id: UUID,
        userId: UUID,
        organizationId: UUID,
        status: String
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO iam.organization_memberships (
                id,
                user_id,
                organization_id,
                status
            )
            VALUES (?, ?, ?, ?)
            """.trimIndent(),
            id,
            userId,
            organizationId,
            status
        )
    }

    private fun seedMembershipRoles() {

        insertMembershipRole(
            membershipId =
                ADMIN_A_MEMBERSHIP_ID,
            roleCode =
                "ORGANIZATION_ADMIN"
        )

        insertMembershipRole(
            membershipId =
                ADMIN_B_MEMBERSHIP_ID,
            roleCode =
                "ORGANIZATION_ADMIN"
        )

        insertMembershipRole(
            membershipId =
                DOCTOR_A_MEMBERSHIP_ID,
            roleCode =
                "DOCTOR"
        )

        insertMembershipRole(
            membershipId =
                MANAGER_A_MEMBERSHIP_ID,
            roleCode =
                "CLINIC_MANAGER"
        )

        insertMembershipRole(
            membershipId =
                NURSE_A_MEMBERSHIP_ID,
            roleCode =
                "NURSE"
        )
    }

    private fun insertMembershipRole(
        membershipId: UUID,
        roleCode: String
    ) {

        val inserted =
            jdbcTemplate.update(
                """
                INSERT INTO iam.membership_roles (
                    membership_id,
                    role_id
                )
                SELECT ?, id
                FROM iam.roles
                WHERE code = ?
                """.trimIndent(),
                membershipId,
                roleCode
            )

        check(inserted == 1) {
            "Expected role '$roleCode' to exist"
        }
    }

    private fun createPractitionerAsAdmin(
        practitionerCode: String
    ): UUID {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    createPractitionerJson(
                        practitionerCode =
                            practitionerCode
                    )
                )
        )
            .andExpect(status().isCreated)

        return practitionerIdByCode(
            practitionerCode
        )
    }

    private fun createPractitionerAsManager(
        practitionerCode: String
    ): UUID {

        mockMvc.perform(
            post(practitionerCollectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    createPractitionerJson(
                        practitionerCode =
                            practitionerCode
                    )
                )
        )
            .andExpect(status().isCreated)

        return practitionerIdByCode(
            practitionerCode
        )
    }

    private fun jwtFor(
        subject: String
    ) =
        jwt().jwt {
            it.subject(subject)
        }

    private fun practitionerCollectionUrl(
        organizationId: UUID =
            ORGANIZATION_A_ID
    ): String =
        "/api/v1/organizations/" +
            "$organizationId/practitioners"

    private fun practitionerItemUrl(
        practitionerId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID
    ): String =
        practitionerCollectionUrl(
            organizationId =
                organizationId
        ) + "/$practitionerId"

    private fun createPractitionerJson(
        membershipId: UUID =
            DOCTOR_A_MEMBERSHIP_ID,
        practitionerCode: String,
        fullName: String =
            "Nguyen Van Bac Si",
        practitionerType: String =
            "DOCTOR",
        licenseNumber: String =
            "LIC-001",
        specialty: String =
            "Internal Medicine",
        phone: String =
            "0901234567",
        email: String =
            "doctor@example.com"
    ): String =
        """
        {
          "membershipId": "$membershipId",
          "practitionerCode": "$practitionerCode",
          "fullName": "$fullName",
          "practitionerType": "$practitionerType",
          "licenseNumber": "$licenseNumber",
          "specialty": "$specialty",
          "phone": "$phone",
          "email": "$email"
        }
        """.trimIndent()

    private fun updatePractitionerJson(
        fullName: String =
            "Nguyen Van Bac Si Updated",
        practitionerType: String =
            "DOCTOR",
        licenseNumber: String =
            "LIC-UPDATED",
        specialty: String =
            "Cardiology",
        phone: String =
            "0909999999",
        email: String =
            "doctor.updated@example.com"
    ): String =
        """
        {
          "fullName": "$fullName",
          "practitionerType": "$practitionerType",
          "licenseNumber": "$licenseNumber",
          "specialty": "$specialty",
          "phone": "$phone",
          "email": "$email"
        }
        """.trimIndent()

    private fun practitionerIdByCode(
        practitionerCode: String
    ): UUID {

        val id =
            jdbcTemplate.queryForObject(
                """
                SELECT id::text
                FROM practitioner.practitioners
                WHERE organization_id = ?
                  AND practitioner_code = ?
                """.trimIndent(),
                String::class.java,
                ORGANIZATION_A_ID,
                practitionerCode
            )

        return UUID.fromString(id)
    }

    private fun practitionerCountByCode(
        practitionerCode: String
    ): Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM practitioner.practitioners
            WHERE organization_id = ?
              AND practitioner_code = ?
            """.trimIndent(),
            Long::class.javaObjectType,
            ORGANIZATION_A_ID,
            practitionerCode
        ) ?: 0L

    private fun practitionerCountByMembership(
        membershipId: UUID
    ): Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM practitioner.practitioners
            WHERE organization_id = ?
              AND membership_id = ?
            """.trimIndent(),
            Long::class.javaObjectType,
            ORGANIZATION_A_ID,
            membershipId
        ) ?: 0L

    private fun createdByUserId(
        practitionerId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT created_by_user_id::text
            FROM practitioner.practitioners
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            practitionerId
        )

    private fun updatedByUserId(
        practitionerId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT updated_by_user_id::text
            FROM practitioner.practitioners
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            practitionerId
        )

    private fun practitionerCode(
        practitionerId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT practitioner_code
            FROM practitioner.practitioners
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            practitionerId
        )

    private fun practitionerMembershipId(
        practitionerId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT membership_id::text
            FROM practitioner.practitioners
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            practitionerId
        )

    private fun practitionerFullName(
        practitionerId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT full_name
            FROM practitioner.practitioners
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            practitionerId
        )
}