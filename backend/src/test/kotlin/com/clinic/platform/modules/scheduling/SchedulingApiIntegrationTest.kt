package com.clinic.platform.modules.scheduling

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
class SchedulingApiIntegrationTest(
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
                "10000000-0000-0000-0000-000000000601"
            )

        private val MANAGER_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000602"
            )

        private val DOCTOR_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000603"
            )

        private val ADMIN_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000601"
            )

        private val ADMIN_B_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000602"
            )

        private val MANAGER_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000603"
            )

        private val DOCTOR_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000604"
            )

        private val PRACTITIONER_A_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000601"
            )

        private val PRACTITIONER_B_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000602"
            )

        private val DOCTOR_A_ASSIGNMENT_ID =
            UUID.fromString(
                "40000000-0000-0000-0000-000000000601"
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
            "scheduling-test-admin"

        private const val MANAGER_SUBJECT =
            "scheduling-test-manager"

        private const val DOCTOR_SUBJECT =
            "scheduling-test-doctor"
    }

    @BeforeEach
    fun setUp() {

        cleanBusinessData()

        seedOrganizationsAndFacilities()
        seedUsers()
        seedMemberships()
        seedMembershipRoles()
        seedPractitioners()
        seedFacilityAssignments()
    }

    // ========================================================
    // FACILITY SCHEDULING SETTINGS
    // ========================================================

    @Test
    fun `create scheduling settings without JWT returns 401`() {

        mockMvc.perform(
            post(settingsUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    settingsJson(
                        timeZoneId = "Asia/Ho_Chi_Minh"
                    )
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `doctor cannot create scheduling settings`() {

        mockMvc.perform(
            post(settingsUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    settingsJson(
                        timeZoneId = "Asia/Ho_Chi_Minh"
                    )
                )
        )
            .andExpect(status().isForbidden)

        assertEquals(
            0L,
            schedulingSettingsCount()
        )
    }

    @Test
    fun `manager can create scheduling settings with normalized timezone and audit`() {

        mockMvc.perform(
            post(settingsUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    settingsJson(
                        timeZoneId =
                            " Asia/Ho_Chi_Minh "
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.organizationId")
                    .value(ORGANIZATION_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.facilityId")
                    .value(FACILITY_A1_ID.toString())
            )
            .andExpect(
                jsonPath("$.timeZoneId")
                    .value("Asia/Ho_Chi_Minh")
            )

        val settingsId =
            schedulingSettingsId()

        assertEquals(
            MANAGER_USER_ID,
            schedulingSettingsCreatedBy(
                settingsId
            )
        )

        assertEquals(
            MANAGER_USER_ID,
            schedulingSettingsUpdatedBy(
                settingsId
            )
        )
    }

    @Test
    fun `invalid timezone returns 400`() {

        mockMvc.perform(
            post(settingsUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    settingsJson(
                        timeZoneId = "Mars/Olympus"
                    )
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "SCHEDULE_TIME_ZONE_INVALID"
                    )
            )
    }

    @Test
    fun `duplicate scheduling settings returns 409`() {

        createSettings(
            subject = ADMIN_SUBJECT
        )

        mockMvc.perform(
            post(settingsUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    settingsJson(
                        timeZoneId =
                            "Asia/Ho_Chi_Minh"
                    )
                )
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `facility from another organization returns 404 when creating settings`() {

        mockMvc.perform(
            post(
                settingsUrl(
                    organizationId =
                        ORGANIZATION_A_ID,
                    facilityId =
                        FACILITY_B1_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    settingsJson(
                        timeZoneId =
                            "Asia/Ho_Chi_Minh"
                    )
                )
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `doctor can read scheduling settings`() {

        val settingsId =
            createSettings(
                subject = ADMIN_SUBJECT
            )

        mockMvc.perform(
            get(settingsUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(settingsId.toString())
            )
            .andExpect(
                jsonPath("$.timeZoneId")
                    .value("Asia/Ho_Chi_Minh")
            )
    }

    @Test
    fun `admin can update scheduling settings while preserving identity and created audit`() {

        val settingsId =
            createSettings(
                subject = MANAGER_SUBJECT
            )

        assertEquals(
            MANAGER_USER_ID,
            schedulingSettingsCreatedBy(
                settingsId
            )
        )

        mockMvc.perform(
            put(settingsUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    settingsJson(
                        timeZoneId = "Asia/Bangkok"
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(settingsId.toString())
            )
            .andExpect(
                jsonPath("$.organizationId")
                    .value(ORGANIZATION_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.facilityId")
                    .value(FACILITY_A1_ID.toString())
            )
            .andExpect(
                jsonPath("$.timeZoneId")
                    .value("Asia/Bangkok")
            )

        assertEquals(
            MANAGER_USER_ID,
            schedulingSettingsCreatedBy(
                settingsId
            )
        )

        assertEquals(
            ADMIN_USER_ID,
            schedulingSettingsUpdatedBy(
                settingsId
            )
        )
    }

    // ========================================================
    // PRACTITIONER AVAILABILITY RULES
    // ========================================================

    @Test
    fun `availability rule requires scheduling settings`() {

        mockMvc.perform(
            post(ruleCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ruleJson(
                        startLocalTime = "08:00:00",
                        endLocalTime = "10:00:00"
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "SCHEDULING_SETTINGS_NOT_CONFIGURED"
                    )
            )
    }

    @Test
    fun `availability rule requires active practitioner facility assignment`() {

        createSettings()

        jdbcTemplate.update(
            """
            UPDATE iam.facility_assignments
            SET status = 'INACTIVE'
            WHERE membership_id = ?
              AND facility_id = ?
            """.trimIndent(),
            DOCTOR_A_MEMBERSHIP_ID,
            FACILITY_A1_ID
        )

        mockMvc.perform(
            post(ruleCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ruleJson(
                        startLocalTime = "08:00:00",
                        endLocalTime = "10:00:00"
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "PRACTITIONER_NOT_ASSIGNED_TO_FACILITY"
                    )
            )
    }

    @Test
    fun `manager can create availability rule with audit`() {

        createSettings()

        mockMvc.perform(
            post(ruleCollectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ruleJson(
                        dayOfWeek = 1,
                        startLocalTime = "08:00:00",
                        endLocalTime = "10:00:00"
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.organizationId")
                    .value(ORGANIZATION_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.facilityId")
                    .value(FACILITY_A1_ID.toString())
            )
            .andExpect(
                jsonPath("$.practitionerId")
                    .value(PRACTITIONER_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.dayOfWeek")
                    .value(1)
            )
            .andExpect(
                jsonPath("$.status")
                    .value("ACTIVE")
            )

        val ruleId =
            latestRuleId()

        assertEquals(
            MANAGER_USER_ID,
            availabilityRuleCreatedBy(
                ruleId
            )
        )

        assertEquals(
            MANAGER_USER_ID,
            availabilityRuleUpdatedBy(
                ruleId
            )
        )
    }

    @Test
    fun `overlapping active availability rule returns 409`() {

        createSettings()

        createRule(
            startLocalTime = "08:00:00",
            endLocalTime = "10:00:00"
        )

        mockMvc.perform(
            post(ruleCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ruleJson(
                        startLocalTime = "09:00:00",
                        endLocalTime = "11:00:00"
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value("SCHEDULE_RULE_OVERLAP")
            )
    }

    @Test
    fun `availability rules may touch at boundary`() {

        createSettings()

        createRule(
            startLocalTime = "08:00:00",
            endLocalTime = "10:00:00"
        )

        mockMvc.perform(
            post(ruleCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ruleJson(
                        startLocalTime = "10:00:00",
                        endLocalTime = "12:00:00"
                    )
                )
        )
            .andExpect(status().isCreated)

        assertEquals(
            2L,
            availabilityRuleCount()
        )
    }

    @Test
    fun `practitioner from another organization returns 404 when creating rule`() {

        createSettings()

        mockMvc.perform(
            post(
                ruleCollectionUrl(
                    practitionerId =
                        PRACTITIONER_B_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ruleJson(
                        startLocalTime = "08:00:00",
                        endLocalTime = "10:00:00"
                    )
                )
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `doctor can get and list availability rules`() {

        createSettings()

        val ruleId =
            createRule(
                startLocalTime = "08:00:00",
                endLocalTime = "10:00:00"
            )

        mockMvc.perform(
            get(
                ruleItemUrl(
                    ruleId = ruleId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(ruleId.toString())
            )

        mockMvc.perform(
            get(ruleCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$[0].id")
                    .value(ruleId.toString())
            )
    }

    @Test
    fun `admin can update availability rule while preserving identity and created audit`() {

        createSettings()

        val ruleId =
            createRule(
                subject = MANAGER_SUBJECT,
                startLocalTime = "08:00:00",
                endLocalTime = "10:00:00"
            )

        assertEquals(
            MANAGER_USER_ID,
            availabilityRuleCreatedBy(
                ruleId
            )
        )

        mockMvc.perform(
            put(
                ruleItemUrl(
                    ruleId = ruleId
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ruleJson(
                        dayOfWeek = 2,
                        startLocalTime = "09:00:00",
                        endLocalTime = "11:00:00",
                        effectiveFrom = "2026-11-01",
                        effectiveTo = "2027-01-31"
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(ruleId.toString())
            )
            .andExpect(
                jsonPath("$.organizationId")
                    .value(ORGANIZATION_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.facilityId")
                    .value(FACILITY_A1_ID.toString())
            )
            .andExpect(
                jsonPath("$.practitionerId")
                    .value(PRACTITIONER_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.dayOfWeek")
                    .value(2)
            )

        assertEquals(
            MANAGER_USER_ID,
            availabilityRuleCreatedBy(
                ruleId
            )
        )

        assertEquals(
            ADMIN_USER_ID,
            availabilityRuleUpdatedBy(
                ruleId
            )
        )
    }

    // ========================================================
    // PRACTITIONER AVAILABILITY EXCEPTIONS
    // ========================================================

    @Test
    fun `invalid exception time range returns 400`() {

        createSettings()

        mockMvc.perform(
            post(exceptionCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    exceptionJson(
                        startAt =
                            "2026-10-01T10:00:00Z",
                        endAt =
                            "2026-10-01T09:00:00Z"
                    )
                )
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "SCHEDULE_EXCEPTION_TIME_RANGE_INVALID"
                    )
            )
    }

    @Test
    fun `manager can create availability exception with normalized reason and audit`() {

        createSettings()

        mockMvc.perform(
            post(exceptionCollectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    exceptionJson(
                        exceptionType = "UNAVAILABLE",
                        startAt =
                            "2026-10-01T01:00:00Z",
                        endAt =
                            "2026-10-01T03:00:00Z",
                        reason =
                            " Annual leave "
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.organizationId")
                    .value(ORGANIZATION_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.facilityId")
                    .value(FACILITY_A1_ID.toString())
            )
            .andExpect(
                jsonPath("$.practitionerId")
                    .value(PRACTITIONER_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.exceptionType")
                    .value("UNAVAILABLE")
            )
            .andExpect(
                jsonPath("$.reason")
                    .value("Annual leave")
            )
            .andExpect(
                jsonPath("$.status")
                    .value("ACTIVE")
            )

        val exceptionId =
            latestExceptionId()

        assertEquals(
            MANAGER_USER_ID,
            availabilityExceptionCreatedBy(
                exceptionId
            )
        )

        assertEquals(
            MANAGER_USER_ID,
            availabilityExceptionUpdatedBy(
                exceptionId
            )
        )
    }

    @Test
    fun `overlapping active availability exception returns 409`() {

        createSettings()

        createException(
            exceptionType = "UNAVAILABLE",
            startAt = "2026-10-01T01:00:00Z",
            endAt = "2026-10-01T03:00:00Z"
        )

        mockMvc.perform(
            post(exceptionCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    exceptionJson(
                        exceptionType = "AVAILABLE",
                        startAt =
                            "2026-10-01T02:00:00Z",
                        endAt =
                            "2026-10-01T04:00:00Z"
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "SCHEDULE_EXCEPTION_OVERLAP"
                    )
            )
    }

    @Test
    fun `availability exceptions may touch at boundary`() {

        createSettings()

        createException(
            startAt =
                "2026-10-01T01:00:00Z",
            endAt =
                "2026-10-01T03:00:00Z"
        )

        mockMvc.perform(
            post(exceptionCollectionUrl())
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    exceptionJson(
                        startAt =
                            "2026-10-01T03:00:00Z",
                        endAt =
                            "2026-10-01T05:00:00Z"
                    )
                )
        )
            .andExpect(status().isCreated)

        assertEquals(
            2L,
            availabilityExceptionCount()
        )
    }

    @Test
    fun `doctor can get and list availability exceptions`() {

        createSettings()

        val exceptionId =
            createException(
                startAt =
                    "2026-10-01T01:00:00Z",
                endAt =
                    "2026-10-01T03:00:00Z"
            )

        mockMvc.perform(
            get(
                exceptionItemUrl(
                    exceptionId = exceptionId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(exceptionId.toString())
            )

        mockMvc.perform(
            get(exceptionCollectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$[0].id")
                    .value(exceptionId.toString())
            )
    }

    @Test
    fun `admin can update availability exception while preserving identity and created audit`() {

        createSettings()

        val exceptionId =
            createException(
                subject = MANAGER_SUBJECT,
                exceptionType = "UNAVAILABLE",
                startAt =
                    "2026-10-01T01:00:00Z",
                endAt =
                    "2026-10-01T03:00:00Z",
                reason =
                    "Training"
            )

        assertEquals(
            MANAGER_USER_ID,
            availabilityExceptionCreatedBy(
                exceptionId
            )
        )

        mockMvc.perform(
            put(
                exceptionItemUrl(
                    exceptionId = exceptionId
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    exceptionJson(
                        exceptionType = "AVAILABLE",
                        startAt =
                            "2026-10-02T04:00:00Z",
                        endAt =
                            "2026-10-02T06:00:00Z",
                        reason =
                            " Extra clinic session "
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(exceptionId.toString())
            )
            .andExpect(
                jsonPath("$.organizationId")
                    .value(ORGANIZATION_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.facilityId")
                    .value(FACILITY_A1_ID.toString())
            )
            .andExpect(
                jsonPath("$.practitionerId")
                    .value(PRACTITIONER_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.exceptionType")
                    .value("AVAILABLE")
            )
            .andExpect(
                jsonPath("$.reason")
                    .value(
                        "Extra clinic session"
                    )
            )

        assertEquals(
            MANAGER_USER_ID,
            availabilityExceptionCreatedBy(
                exceptionId
            )
        )

        assertEquals(
            ADMIN_USER_ID,
            availabilityExceptionUpdatedBy(
                exceptionId
            )
        )
    }

    // ========================================================
    // SETUP / CLEANUP
    // ========================================================

    private fun cleanBusinessData() {

        jdbcTemplate.update(
            """
            DELETE FROM scheduling.practitioner_availability_exceptions
            """.trimIndent()
        )

        jdbcTemplate.update(
            """
            DELETE FROM scheduling.practitioner_availability_rules
            """.trimIndent()
        )

        jdbcTemplate.update(
            """
            DELETE FROM scheduling.facility_scheduling_settings
            """.trimIndent()
        )

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
            VALUES (
                ?,
                ?,
                'FAC-A1',
                'Facility A1',
                'VN',
                'ACTIVE'
            )
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
            VALUES (
                ?,
                ?,
                'FAC-B1',
                'Facility B1',
                'VN',
                'ACTIVE'
            )
            """.trimIndent(),
            FACILITY_B1_ID,
            ORGANIZATION_B_ID
        )
    }

    private fun seedUsers() {

        insertUser(
            id = ADMIN_USER_ID,
            subject = ADMIN_SUBJECT,
            email =
                "scheduling-admin@example.com",
            displayName =
                "Scheduling Admin"
        )

        insertUser(
            id = MANAGER_USER_ID,
            subject = MANAGER_SUBJECT,
            email =
                "scheduling-manager@example.com",
            displayName =
                "Scheduling Manager"
        )

        insertUser(
            id = DOCTOR_USER_ID,
            subject = DOCTOR_SUBJECT,
            email =
                "scheduling-doctor@example.com",
            displayName =
                "Scheduling Doctor"
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
            VALUES (
                ?,
                ?,
                'keycloak',
                ?,
                ?,
                'ACTIVE'
            )
            """.trimIndent(),
            id,
            subject,
            email,
            displayName
        )
    }

    private fun seedMemberships() {

        insertMembership(
            id =
                ADMIN_A_MEMBERSHIP_ID,
            userId =
                ADMIN_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
        )

        insertMembership(
            id =
                ADMIN_B_MEMBERSHIP_ID,
            userId =
                ADMIN_USER_ID,
            organizationId =
                ORGANIZATION_B_ID
        )

        insertMembership(
            id =
                MANAGER_A_MEMBERSHIP_ID,
            userId =
                MANAGER_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
        )

        insertMembership(
            id =
                DOCTOR_A_MEMBERSHIP_ID,
            userId =
                DOCTOR_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
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

    private fun seedPractitioners() {

        jdbcTemplate.update(
            """
            INSERT INTO practitioner.practitioners (
                id,
                organization_id,
                membership_id,
                practitioner_code,
                full_name,
                practitioner_type,
                status,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                'DOC-A',
                'Doctor A',
                'DOCTOR',
                'ACTIVE',
                ?,
                ?
            )
            """.trimIndent(),
            PRACTITIONER_A_ID,
            ORGANIZATION_A_ID,
            DOCTOR_A_MEMBERSHIP_ID,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )

        /*
         * Organization B practitioner intentionally uses the
         * admin's Organization B membership. The profile only
         * exists to verify tenant isolation in this test class.
         */
        jdbcTemplate.update(
            """
            INSERT INTO practitioner.practitioners (
                id,
                organization_id,
                membership_id,
                practitioner_code,
                full_name,
                practitioner_type,
                status,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                'DOC-B',
                'Doctor B',
                'DOCTOR',
                'ACTIVE',
                ?,
                ?
            )
            """.trimIndent(),
            PRACTITIONER_B_ID,
            ORGANIZATION_B_ID,
            ADMIN_B_MEMBERSHIP_ID,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )
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
            DOCTOR_A_ASSIGNMENT_ID,
            DOCTOR_A_MEMBERSHIP_ID,
            FACILITY_A1_ID
        )
    }

    // ========================================================
    // CREATE HELPERS
    // ========================================================

    private fun createSettings(
        subject: String =
            ADMIN_SUBJECT,
        timeZoneId: String =
            "Asia/Ho_Chi_Minh"
    ): UUID {

        mockMvc.perform(
            post(settingsUrl())
                .with(jwtFor(subject))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    settingsJson(
                        timeZoneId = timeZoneId
                    )
                )
        )
            .andExpect(status().isCreated)

        return schedulingSettingsId()
    }

    private fun createRule(
        subject: String =
            ADMIN_SUBJECT,
        dayOfWeek: Int = 1,
        startLocalTime: String =
            "08:00:00",
        endLocalTime: String =
            "10:00:00",
        effectiveFrom: String =
            "2026-10-01",
        effectiveTo: String =
            "2026-12-31"
    ): UUID {

        mockMvc.perform(
            post(ruleCollectionUrl())
                .with(jwtFor(subject))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ruleJson(
                        dayOfWeek = dayOfWeek,
                        startLocalTime =
                            startLocalTime,
                        endLocalTime =
                            endLocalTime,
                        effectiveFrom =
                            effectiveFrom,
                        effectiveTo =
                            effectiveTo
                    )
                )
        )
            .andExpect(status().isCreated)

        return latestRuleId()
    }

    private fun createException(
        subject: String =
            ADMIN_SUBJECT,
        exceptionType: String =
            "UNAVAILABLE",
        startAt: String =
            "2026-10-01T01:00:00Z",
        endAt: String =
            "2026-10-01T03:00:00Z",
        reason: String =
            "Unavailable"
    ): UUID {

        mockMvc.perform(
            post(exceptionCollectionUrl())
                .with(jwtFor(subject))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    exceptionJson(
                        exceptionType =
                            exceptionType,
                        startAt =
                            startAt,
                        endAt =
                            endAt,
                        reason =
                            reason
                    )
                )
        )
            .andExpect(status().isCreated)

        return latestExceptionId()
    }

    // ========================================================
    // JWT / URL HELPERS
    // ========================================================

    private fun jwtFor(
        subject: String
    ) =
        jwt().jwt {
            it.subject(subject)
        }

    private fun settingsUrl(
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        "/api/v1/organizations/" +
            "$organizationId/facilities/" +
            "$facilityId/scheduling-settings"

    private fun ruleCollectionUrl(
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID,
        practitionerId: UUID =
            PRACTITIONER_A_ID
    ): String =
        "/api/v1/organizations/" +
            "$organizationId/facilities/" +
            "$facilityId/practitioners/" +
            "$practitionerId/availability-rules"

    private fun ruleItemUrl(
        ruleId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID,
        practitionerId: UUID =
            PRACTITIONER_A_ID
    ): String =
        ruleCollectionUrl(
            organizationId = organizationId,
            facilityId = facilityId,
            practitionerId = practitionerId
        ) + "/$ruleId"

    private fun exceptionCollectionUrl(
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID,
        practitionerId: UUID =
            PRACTITIONER_A_ID
    ): String =
        "/api/v1/organizations/" +
            "$organizationId/facilities/" +
            "$facilityId/practitioners/" +
            "$practitionerId/availability-exceptions"

    private fun exceptionItemUrl(
        exceptionId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID,
        practitionerId: UUID =
            PRACTITIONER_A_ID
    ): String =
        exceptionCollectionUrl(
            organizationId = organizationId,
            facilityId = facilityId,
            practitionerId = practitionerId
        ) + "/$exceptionId"

    // ========================================================
    // JSON HELPERS
    // ========================================================

    private fun settingsJson(
        timeZoneId: String
    ): String =
        """
        {
          "timeZoneId": "$timeZoneId"
        }
        """.trimIndent()

    private fun ruleJson(
        dayOfWeek: Int = 1,
        startLocalTime: String =
            "08:00:00",
        endLocalTime: String =
            "10:00:00",
        effectiveFrom: String =
            "2026-10-01",
        effectiveTo: String =
            "2026-12-31"
    ): String =
        """
        {
          "dayOfWeek": $dayOfWeek,
          "startLocalTime": "$startLocalTime",
          "endLocalTime": "$endLocalTime",
          "effectiveFrom": "$effectiveFrom",
          "effectiveTo": "$effectiveTo"
        }
        """.trimIndent()

    private fun exceptionJson(
        exceptionType: String =
            "UNAVAILABLE",
        startAt: String,
        endAt: String,
        reason: String =
            "Unavailable"
    ): String =
        """
        {
          "exceptionType": "$exceptionType",
          "startAt": "$startAt",
          "endAt": "$endAt",
          "reason": "$reason"
        }
        """.trimIndent()

    // ========================================================
    // DATABASE ASSERTION HELPERS
    // ========================================================

    private fun schedulingSettingsCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM scheduling.facility_scheduling_settings
            """.trimIndent(),
            Long::class.java
        )!!

    private fun schedulingSettingsId():
        UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT id
            FROM scheduling.facility_scheduling_settings
            WHERE organization_id = ?
              AND facility_id = ?
            """.trimIndent(),
            UUID::class.java,
            ORGANIZATION_A_ID,
            FACILITY_A1_ID
        )!!

    private fun schedulingSettingsCreatedBy(
        settingsId: UUID
    ): UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT created_by_user_id
            FROM scheduling.facility_scheduling_settings
            WHERE id = ?
            """.trimIndent(),
            UUID::class.java,
            settingsId
        )!!

    private fun schedulingSettingsUpdatedBy(
        settingsId: UUID
    ): UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT updated_by_user_id
            FROM scheduling.facility_scheduling_settings
            WHERE id = ?
            """.trimIndent(),
            UUID::class.java,
            settingsId
        )!!

    private fun availabilityRuleCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM scheduling.practitioner_availability_rules
            WHERE organization_id = ?
              AND facility_id = ?
              AND practitioner_id = ?
            """.trimIndent(),
            Long::class.java,
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            PRACTITIONER_A_ID
        )!!

    private fun latestRuleId():
        UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT id
            FROM scheduling.practitioner_availability_rules
            WHERE organization_id = ?
              AND facility_id = ?
              AND practitioner_id = ?
            ORDER BY created_at DESC, id DESC
            LIMIT 1
            """.trimIndent(),
            UUID::class.java,
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            PRACTITIONER_A_ID
        )!!

    private fun availabilityRuleCreatedBy(
        ruleId: UUID
    ): UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT created_by_user_id
            FROM scheduling.practitioner_availability_rules
            WHERE id = ?
            """.trimIndent(),
            UUID::class.java,
            ruleId
        )!!

    private fun availabilityRuleUpdatedBy(
        ruleId: UUID
    ): UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT updated_by_user_id
            FROM scheduling.practitioner_availability_rules
            WHERE id = ?
            """.trimIndent(),
            UUID::class.java,
            ruleId
        )!!

    private fun availabilityExceptionCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM scheduling.practitioner_availability_exceptions
            WHERE organization_id = ?
              AND facility_id = ?
              AND practitioner_id = ?
            """.trimIndent(),
            Long::class.java,
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            PRACTITIONER_A_ID
        )!!

    private fun latestExceptionId():
        UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT id
            FROM scheduling.practitioner_availability_exceptions
            WHERE organization_id = ?
              AND facility_id = ?
              AND practitioner_id = ?
            ORDER BY created_at DESC, id DESC
            LIMIT 1
            """.trimIndent(),
            UUID::class.java,
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            PRACTITIONER_A_ID
        )!!

    private fun availabilityExceptionCreatedBy(
        exceptionId: UUID
    ): UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT created_by_user_id
            FROM scheduling.practitioner_availability_exceptions
            WHERE id = ?
            """.trimIndent(),
            UUID::class.java,
            exceptionId
        )!!

    private fun availabilityExceptionUpdatedBy(
        exceptionId: UUID
    ): UUID =
        jdbcTemplate.queryForObject(
            """
            SELECT updated_by_user_id
            FROM scheduling.practitioner_availability_exceptions
            WHERE id = ?
            """.trimIndent(),
            UUID::class.java,
            exceptionId
        )!!
}