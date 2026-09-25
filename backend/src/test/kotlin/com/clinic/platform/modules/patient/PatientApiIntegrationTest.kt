package com.clinic.platform.modules.patient

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
class PatientApiIntegrationTest(
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

        private val FACILITY_A2_ID =
            UUID.fromString(
                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2"
            )

        private val FACILITY_B1_ID =
            UUID.fromString(
                "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1"
            )

        private val ADMIN_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000101"
            )

        private val DOCTOR_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000102"
            )

        private val ADMIN_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000101"
            )

        private val DOCTOR_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000102"
            )

        private val ADMIN_ASSIGNMENT_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000101"
            )

        private val DOCTOR_ASSIGNMENT_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000102"
            )

        private val NON_EXISTENT_PATIENT_ID =
            UUID.fromString(
                "99999999-9999-9999-9999-999999999999"
            )

        private const val ADMIN_SUBJECT =
            "integration-test-admin"

        private const val DOCTOR_SUBJECT =
            "integration-test-doctor"
    }

    @BeforeEach
    fun setUp() {

        cleanBusinessData()

        seedOrganizationsAndFacilities()
        seedUsers()
        seedMemberships()
        seedMembershipRoles()
        seedFacilityAssignments()
    }

    @Test
    fun `create patient without JWT returns 401`() {

        mockMvc.perform(
            post(patientCollectionUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPatientJson(
                        patientCode = "PAT-401"
                    )
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `doctor cannot create patient`() {

        mockMvc.perform(
            post(patientCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPatientJson(
                        patientCode = "PAT-DOCTOR-CREATE"
                    )
                )
        )
            .andExpect(status().isForbidden)

        assertEquals(
            0L,
            patientCountByCode(
                "PAT-DOCTOR-CREATE"
            )
        )
    }

    @Test
    fun `organization admin can create patient and audit actor is stored`() {

        mockMvc.perform(
            post(patientCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPatientJson(
                        patientCode = "PAT-CREATE-001"
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.patientCode")
                    .value("PAT-CREATE-001")
            )
            .andExpect(
                jsonPath("$.fullName")
                    .value("Nguyen Van An")
            )
            .andExpect(
                jsonPath("$.organizationId")
                    .value(
                        ORGANIZATION_A_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("$.managingFacilityId")
                    .value(
                        FACILITY_A1_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("$.status")
                    .value("ACTIVE")
            )

        val patientId =
            patientIdByCode(
                "PAT-CREATE-001"
            )

        assertEquals(
            ADMIN_USER_ID.toString(),
            createdByUserId(patientId)
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            updatedByUserId(patientId)
        )
    }

    @Test
    fun `duplicate patient code returns 409`() {

        createPatientAsAdmin(
            patientCode = "PAT-DUPLICATE"
        )

        mockMvc.perform(
            post(patientCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPatientJson(
                        patientCode = "PAT-DUPLICATE"
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1L,
            patientCountByCode(
                "PAT-DUPLICATE"
            )
        )
    }

    @Test
    fun `future date of birth on create returns 400 and is not persisted`() {

        mockMvc.perform(
            post(patientCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    createPatientJson(
                        patientCode = "PAT-FUTURE-CREATE",
                        dateOfBirth = "2999-01-01"
                    )
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "PATIENT_DATE_OF_BIRTH_IN_FUTURE"
                    )
            )

        assertEquals(
            0L,
            patientCountByCode(
                "PAT-FUTURE-CREATE"
            )
        )
    }

    @Test
    fun `get patient without JWT returns 401`() {

        mockMvc.perform(
            get(
                patientItemUrl(
                    NON_EXISTENT_PATIENT_ID
                )
            )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `doctor can get and list patient in assigned facility`() {

        val patientId =
            createPatientAsAdmin(
                patientCode = "PAT-READ-001"
            )

        mockMvc.perform(
            get(
                patientItemUrl(
                    patientId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(patientId.toString())
            )
            .andExpect(
                jsonPath("$.patientCode")
                    .value("PAT-READ-001")
            )

        mockMvc.perform(
            get(patientCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$[0].id")
                    .value(patientId.toString())
            )
            .andExpect(
                jsonPath("$[0].patientCode")
                    .value("PAT-READ-001")
            )
    }

    @Test
    fun `get nonexistent patient returns 404`() {

        mockMvc.perform(
            get(
                patientItemUrl(
                    NON_EXISTENT_PATIENT_ID
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get patient through wrong organization returns 404`() {

        val patientId =
            createPatientAsAdmin(
                patientCode = "PAT-WRONG-ORG"
            )

        mockMvc.perform(
            get(
                patientItemUrl(
                    patientId = patientId,
                    organizationId =
                        ORGANIZATION_B_ID,
                    facilityId =
                        FACILITY_A1_ID
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get patient through unassigned facility returns 403`() {

        val patientId =
            createPatientAsAdmin(
                patientCode = "PAT-WRONG-FACILITY"
            )

        mockMvc.perform(
            get(
                patientItemUrl(
                    patientId = patientId,
                    organizationId =
                        ORGANIZATION_A_ID,
                    facilityId =
                        FACILITY_A2_ID
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `update patient without JWT returns 401`() {

        val patientId =
            createPatientAsAdmin(
                patientCode = "PAT-UPDATE-401"
            )

        mockMvc.perform(
            put(
                patientItemUrl(
                    patientId
                )
            )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    updatePatientJson()
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `doctor can update patient and audit actors are correct`() {

        val patientId =
            createPatientAsAdmin(
                patientCode = "PAT-UPDATE-001"
            )

        assertEquals(
            ADMIN_USER_ID.toString(),
            createdByUserId(patientId)
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            updatedByUserId(patientId)
        )

        mockMvc.perform(
            put(
                patientItemUrl(
                    patientId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    updatePatientJson(
                        fullName =
                            "Nguyen Van An Updated",
                        phone =
                            "0909999999",
                        email =
                            "an.updated@example.com",
                        addressLine =
                            "456 Nguyen Trai"
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(patientId.toString())
            )
            .andExpect(
                jsonPath("$.patientCode")
                    .value("PAT-UPDATE-001")
            )
            .andExpect(
                jsonPath("$.fullName")
                    .value(
                        "Nguyen Van An Updated"
                    )
            )
            .andExpect(
                jsonPath("$.phone")
                    .value("0909999999")
            )
            .andExpect(
                jsonPath("$.email")
                    .value(
                        "an.updated@example.com"
                    )
            )
            .andExpect(
                jsonPath("$.addressLine")
                    .value(
                        "456 Nguyen Trai"
                    )
            )

        assertEquals(
            "PAT-UPDATE-001",
            patientCode(patientId)
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            createdByUserId(patientId)
        )

        assertEquals(
            DOCTOR_USER_ID.toString(),
            updatedByUserId(patientId)
        )
    }

    @Test
    fun `future date of birth on update returns 400 and changes are not persisted`() {

        val patientId =
            createPatientAsAdmin(
                patientCode =
                    "PAT-FUTURE-UPDATE"
            )

        val originalName =
            patientFullName(patientId)

        val originalDateOfBirth =
            patientDateOfBirth(patientId)

        mockMvc.perform(
            put(
                patientItemUrl(
                    patientId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    updatePatientJson(
                        fullName =
                            "Should Not Persist",
                        dateOfBirth =
                            "2999-01-01"
                    )
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "PATIENT_DATE_OF_BIRTH_IN_FUTURE"
                    )
            )

        assertEquals(
            originalName,
            patientFullName(patientId)
        )

        assertEquals(
            originalDateOfBirth,
            patientDateOfBirth(patientId)
        )

        assertEquals(
            ADMIN_USER_ID.toString(),
            updatedByUserId(patientId)
        )
    }

    @Test
    fun `update patient through wrong organization returns 404`() {

        val patientId =
            createPatientAsAdmin(
                patientCode =
                    "PAT-UPDATE-WRONG-ORG"
            )

        mockMvc.perform(
            put(
                patientItemUrl(
                    patientId = patientId,
                    organizationId =
                        ORGANIZATION_B_ID,
                    facilityId =
                        FACILITY_A1_ID
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    updatePatientJson()
                )
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `update patient through unassigned facility returns 403`() {

        val patientId =
            createPatientAsAdmin(
                patientCode =
                    "PAT-UPDATE-WRONG-FACILITY"
            )

        mockMvc.perform(
            put(
                patientItemUrl(
                    patientId = patientId,
                    organizationId =
                        ORGANIZATION_A_ID,
                    facilityId =
                        FACILITY_A2_ID
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    updatePatientJson()
                )
        )
            .andExpect(status().isForbidden)
    }

    private fun cleanBusinessData() {

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
            VALUES (?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            ORGANIZATION_A_ID,
            "ORG-A",
            "Integration Test Organization A"
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
            "Integration Test Organization B"
        )

        jdbcTemplate.update(
            """
            INSERT INTO organization.facilities (
                id,
                organization_id,
                code,
                name,
                status
            )
            VALUES (?, ?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            FACILITY_A1_ID,
            ORGANIZATION_A_ID,
            "A1",
            "Facility A1"
        )

        jdbcTemplate.update(
            """
            INSERT INTO organization.facilities (
                id,
                organization_id,
                code,
                name,
                status
            )
            VALUES (?, ?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            FACILITY_A2_ID,
            ORGANIZATION_A_ID,
            "A2",
            "Facility A2"
        )

        jdbcTemplate.update(
            """
            INSERT INTO organization.facilities (
                id,
                organization_id,
                code,
                name,
                status
            )
            VALUES (?, ?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            FACILITY_B1_ID,
            ORGANIZATION_B_ID,
            "B1",
            "Facility B1"
        )
    }

    private fun seedUsers() {

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
            ADMIN_USER_ID,
            ADMIN_SUBJECT,
            "integration-admin@example.com",
            "Integration Admin"
        )

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
            DOCTOR_USER_ID,
            DOCTOR_SUBJECT,
            "integration-doctor@example.com",
            "Integration Doctor"
        )
    }

    private fun seedMemberships() {

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
            ADMIN_MEMBERSHIP_ID,
            ADMIN_USER_ID,
            ORGANIZATION_A_ID
        )

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
            DOCTOR_MEMBERSHIP_ID,
            DOCTOR_USER_ID,
            ORGANIZATION_A_ID
        )
    }

    private fun seedMembershipRoles() {

        insertMembershipRole(
            membershipId =
                ADMIN_MEMBERSHIP_ID,
            roleCode =
                "ORGANIZATION_ADMIN"
        )

        insertMembershipRole(
            membershipId =
                DOCTOR_MEMBERSHIP_ID,
            roleCode =
                "DOCTOR"
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

    private fun seedFacilityAssignments() {

        jdbcTemplate.update(
            """
            INSERT INTO iam.facility_assignments (
                id,
                membership_id,
                facility_id,
                status
            )
            VALUES (?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            ADMIN_ASSIGNMENT_ID,
            ADMIN_MEMBERSHIP_ID,
            FACILITY_A1_ID
        )

        jdbcTemplate.update(
            """
            INSERT INTO iam.facility_assignments (
                id,
                membership_id,
                facility_id,
                status
            )
            VALUES (?, ?, ?, 'ACTIVE')
            """.trimIndent(),
            DOCTOR_ASSIGNMENT_ID,
            DOCTOR_MEMBERSHIP_ID,
            FACILITY_A1_ID
        )
    }

    private fun createPatientAsAdmin(
        patientCode: String
    ): UUID {

        mockMvc.perform(
            post(patientCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    createPatientJson(
                        patientCode =
                            patientCode
                    )
                )
        )
            .andExpect(status().isCreated)

        return patientIdByCode(
            patientCode
        )
    }

    private fun jwtFor(
        subject: String
    ) =
        jwt().jwt {
            it.subject(subject)
        }

    private fun patientCollectionUrl(
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        "/api/v1/organizations/" +
            "$organizationId/facilities/" +
            "$facilityId/patients"

    private fun patientItemUrl(
        patientId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        patientCollectionUrl(
            organizationId =
                organizationId,
            facilityId =
                facilityId
        ) + "/$patientId"

    private fun createPatientJson(
        patientCode: String,
        fullName: String =
            "Nguyen Van An",
        dateOfBirth: String =
            "1990-01-01"
    ): String =
        """
        {
          "patientCode": "$patientCode",
          "fullName": "$fullName",
          "dateOfBirth": "$dateOfBirth",
          "sex": "MALE",
          "phone": "0901234567",
          "email": "an@example.com",
          "addressLine": "123 Test Street",
          "ward": "Test Ward",
          "district": "Test District",
          "province": "Ha Noi",
          "countryCode": "VN"
        }
        """.trimIndent()

    private fun updatePatientJson(
        fullName: String =
            "Nguyen Van An Updated",
        dateOfBirth: String =
            "1990-01-01",
        phone: String =
            "0909999999",
        email: String =
            "an.updated@example.com",
        addressLine: String =
            "456 Nguyen Trai"
    ): String =
        """
        {
          "fullName": "$fullName",
          "dateOfBirth": "$dateOfBirth",
          "sex": "MALE",
          "phone": "$phone",
          "email": "$email",
          "addressLine": "$addressLine",
          "ward": "Updated Ward",
          "district": "Updated District",
          "province": "Ha Noi",
          "countryCode": "VN"
        }
        """.trimIndent()

    private fun patientIdByCode(
        patientCode: String
    ): UUID {

        val id =
            jdbcTemplate.queryForObject(
                """
                SELECT id::text
                FROM patient.patients
                WHERE organization_id = ?
                  AND patient_code = ?
                """.trimIndent(),
                String::class.java,
                ORGANIZATION_A_ID,
                patientCode
            )

        return UUID.fromString(id)
    }

    private fun patientCountByCode(
        patientCode: String
    ): Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM patient.patients
            WHERE organization_id = ?
              AND patient_code = ?
            """.trimIndent(),
            Long::class.javaObjectType,
            ORGANIZATION_A_ID,
            patientCode
        ) ?: 0L

    private fun createdByUserId(
        patientId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT created_by_user_id::text
            FROM patient.patients
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            patientId
        )

    private fun updatedByUserId(
        patientId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT updated_by_user_id::text
            FROM patient.patients
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            patientId
        )

    private fun patientCode(
        patientId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT patient_code
            FROM patient.patients
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            patientId
        )

    private fun patientFullName(
        patientId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT full_name
            FROM patient.patients
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            patientId
        )

    private fun patientDateOfBirth(
        patientId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT date_of_birth::text
            FROM patient.patients
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            patientId
        )
}