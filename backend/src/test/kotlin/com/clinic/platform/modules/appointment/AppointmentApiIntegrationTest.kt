package com.clinic.platform.modules.appointment

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
class AppointmentApiIntegrationTest(
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
                "10000000-0000-0000-0000-000000000701"
            )

        private val MANAGER_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000702"
            )

        private val DOCTOR_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000703"
            )

        private val DOCTOR_2_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000704"
            )

        private val ADMIN_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000701"
            )

        private val ADMIN_B_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000702"
            )

        private val MANAGER_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000703"
            )

        private val DOCTOR_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000704"
            )

        private val DOCTOR_2_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000705"
            )

        private val PRACTITIONER_A_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000701"
            )

        private val PRACTITIONER_A2_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000702"
            )

        private val PRACTITIONER_B_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000703"
            )

        private val DOCTOR_A_ASSIGNMENT_ID =
            UUID.fromString(
                "40000000-0000-0000-0000-000000000701"
            )

        private val DOCTOR_A2_ASSIGNMENT_ID =
            UUID.fromString(
                "40000000-0000-0000-0000-000000000702"
            )

        private val PATIENT_A_ID =
            UUID.fromString(
                "50000000-0000-0000-0000-000000000701"
            )

        private val PATIENT_A2_ID =
            UUID.fromString(
                "50000000-0000-0000-0000-000000000702"
            )

        private val PATIENT_B_ID =
            UUID.fromString(
                "50000000-0000-0000-0000-000000000703"
            )

        private val SERVICE_A_ID =
            UUID.fromString(
                "60000000-0000-0000-0000-000000000701"
            )

        private val SERVICE_B_ID =
            UUID.fromString(
                "60000000-0000-0000-0000-000000000702"
            )

        private val FACILITY_SERVICE_A_ID =
            UUID.fromString(
                "70000000-0000-0000-0000-000000000701"
            )

        private val FACILITY_SERVICE_B_ID =
            UUID.fromString(
                "70000000-0000-0000-0000-000000000702"
            )

        private val SCHEDULING_SETTINGS_A_ID =
            UUID.fromString(
                "80000000-0000-0000-0000-000000000701"
            )

        private val RULE_A_ID =
            UUID.fromString(
                "81000000-0000-0000-0000-000000000701"
            )

        private val RULE_A2_ID =
            UUID.fromString(
                "81000000-0000-0000-0000-000000000702"
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
            "appointment-test-admin"

        private const val MANAGER_SUBJECT =
            "appointment-test-manager"

        private const val DOCTOR_SUBJECT =
            "appointment-test-doctor"

        /*
         * 2026-10-05 is Monday.
         *
         * Facility timezone:
         * Asia/Ho_Chi_Minh = UTC+07.
         *
         * Recurring availability:
         * Monday 08:00 - 12:00 local.
         *
         * Therefore:
         * 02:00Z = 09:00 local.
         */
        private const val VALID_START =
            "2026-10-05T02:00:00Z"

        private const val VALID_END =
            "2026-10-05T02:30:00Z"
    }

    @BeforeEach
    fun setUp() {

        cleanBusinessData()

        seedOrganizationsAndFacilities()
        seedUsers()
        seedMemberships()
        seedMembershipRoles()
        seedPatients()
        seedPractitioners()
        seedFacilityAssignments()
        seedServiceCatalog()
        seedScheduling()
    }

    // ========================================================
    // AUTHENTICATION / AUTHORIZATION
    // ========================================================

    @Test
    fun `create appointment without JWT returns 401`() {

        mockMvc.perform(
            post(collectionUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson()
                )
        )
            .andExpect(status().isUnauthorized)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `doctor cannot create appointment`() {

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson()
                )
        )
            .andExpect(status().isForbidden)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `manager can create appointment and end time is derived from service duration`() {

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        reason = " General consultation "
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
                jsonPath("$.patientId")
                    .value(PATIENT_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.practitionerId")
                    .value(PRACTITIONER_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.facilityServiceId")
                    .value(FACILITY_SERVICE_A_ID.toString())
            )
            .andExpect(
                jsonPath("$.scheduledStartAt")
                    .value(VALID_START)
            )
            .andExpect(
                jsonPath("$.scheduledEndAt")
                    .value(VALID_END)
            )
            .andExpect(
                jsonPath("$.status")
                    .value("SCHEDULED")
            )
            .andExpect(
                jsonPath("$.reason")
                    .value("General consultation")
            )
            .andExpect(
                jsonPath("$.createdByUserId")
                    .value(MANAGER_USER_ID.toString())
            )
            .andExpect(
                jsonPath("$.updatedByUserId")
                    .value(MANAGER_USER_ID.toString())
            )

        assertEquals(
            1L,
            appointmentCount()
        )
    }

    @Test
    fun `doctor can read and list appointments`() {

        val appointmentId =
            createAppointment()

        mockMvc.perform(
            get(itemUrl(appointmentId))
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(appointmentId.toString())
            )

        mockMvc.perform(
            get(collectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$[0].id")
                    .value(appointmentId.toString())
            )
    }

    // ========================================================
    // TENANT SAFETY
    // ========================================================

    @Test
    fun `cross organization patient returns 404`() {

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        patientId = PATIENT_B_ID
                    )
                )
        )
            .andExpect(status().isNotFound)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `cross organization practitioner returns 404`() {

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        practitionerId =
                            PRACTITIONER_B_ID
                    )
                )
        )
            .andExpect(status().isNotFound)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `facility service from another organization returns 404`() {

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        facilityServiceId =
                            FACILITY_SERVICE_B_ID
                    )
                )
        )
            .andExpect(status().isNotFound)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `cross tenant appointment get returns 404`() {

        val appointmentId =
            createAppointment()

        mockMvc.perform(
            get(
                itemUrl(
                    appointmentId = appointmentId,
                    organizationId = ORGANIZATION_B_ID,
                    facilityId = FACILITY_B1_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
        )
            .andExpect(status().isNotFound)
    }

    // ========================================================
    // BOOKING PRECONDITIONS
    // ========================================================

    @Test
    fun `inactive patient cannot be booked`() {

        jdbcTemplate.update(
            """
            UPDATE patient.patients
            SET status = 'INACTIVE'
            WHERE id = ?
            """.trimIndent(),
            PATIENT_A_ID
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson()
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `practitioner without active facility assignment cannot be booked`() {

        jdbcTemplate.update(
            """
            UPDATE iam.facility_assignments
            SET status = 'INACTIVE'
            WHERE id = ?
            """.trimIndent(),
            DOCTOR_A_ASSIGNMENT_ID
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson()
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `inactive facility service cannot be booked`() {

        jdbcTemplate.update(
            """
            UPDATE service_catalog.facility_services
            SET status = 'INACTIVE'
            WHERE id = ?
            """.trimIndent(),
            FACILITY_SERVICE_A_ID
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson()
                )
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `booking disabled facility service cannot be booked`() {

        jdbcTemplate.update(
            """
            UPDATE service_catalog.facility_services
            SET booking_enabled = FALSE
            WHERE id = ?
            """.trimIndent(),
            FACILITY_SERVICE_A_ID
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson()
                )
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `missing scheduling settings prevents booking`() {

        jdbcTemplate.update(
            """
            DELETE FROM scheduling.practitioner_availability_rules
            """
                .trimIndent()
        )

        jdbcTemplate.update(
            """
            DELETE FROM scheduling.facility_scheduling_settings
            """
                .trimIndent()
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson()
                )
        )
            .andExpect(status().isConflict)
    }

    // ========================================================
    // AVAILABILITY
    // ========================================================

    @Test
    fun `appointment outside recurring availability returns 409`() {

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        scheduledStartAt =
                            "2026-10-05T06:00:00Z"
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `unavailable exception blocks overlapping appointment`() {

        insertAvailabilityException(
            exceptionType = "UNAVAILABLE",
            startAt = "2026-10-05T02:10:00Z",
            endAt = "2026-10-05T02:20:00Z"
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson()
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            0L,
            appointmentCount()
        )
    }

    @Test
    fun `available exception can open time outside recurring availability`() {

        insertAvailabilityException(
            exceptionType = "AVAILABLE",
            startAt = "2026-10-05T06:00:00Z",
            endAt = "2026-10-05T07:00:00Z"
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        scheduledStartAt =
                            "2026-10-05T06:00:00Z"
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("$.scheduledEndAt")
                    .value("2026-10-05T06:30:00Z")
            )
    }

    // ========================================================
    // DOUBLE BOOKING
    // ========================================================

    @Test
    fun `practitioner double booking returns 409`() {

        createAppointment()

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        patientId = PATIENT_A2_ID,
                        scheduledStartAt =
                            "2026-10-05T02:15:00Z"
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1L,
            appointmentCount()
        )
    }

    @Test
    fun `patient double booking returns 409`() {

        createAppointment()

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        practitionerId =
                            PRACTITIONER_A2_ID,
                        scheduledStartAt =
                            "2026-10-05T02:15:00Z"
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1L,
            appointmentCount()
        )
    }

    @Test
    fun `touching appointment boundary is allowed`() {

        createAppointment()

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    appointmentJson(
                        patientId = PATIENT_A2_ID,
                        scheduledStartAt =
                            "2026-10-05T02:30:00Z"
                    )
                )
        )
            .andExpect(status().isCreated)

        assertEquals(
            2L,
            appointmentCount()
        )
    }

    // ========================================================
    // MUTATION / LIFECYCLE
    // ========================================================

    @Test
    fun `manager can reschedule appointment`() {

        val appointmentId =
            createAppointment()

        mockMvc.perform(
            put(
                itemUrl(appointmentId) +
                    "/reschedule"
            )
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "scheduledStartAt":
                        "2026-10-05T03:00:00Z"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.scheduledStartAt")
                    .value("2026-10-05T03:00:00Z")
            )
            .andExpect(
                jsonPath("$.scheduledEndAt")
                    .value("2026-10-05T03:30:00Z")
            )
            .andExpect(
                jsonPath("$.updatedByUserId")
                    .value(MANAGER_USER_ID.toString())
            )
    }

    @Test
    fun `manager can update appointment reason`() {

        val appointmentId =
            createAppointment()

        mockMvc.perform(
            put(
                itemUrl(appointmentId) +
                    "/reason"
            )
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "reason":
                        " Updated consultation reason "
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.reason")
                    .value(
                        "Updated consultation reason"
                    )
            )
    }

    @Test
    fun `manager can cancel appointment`() {

        val appointmentId =
            createAppointment()

        mockMvc.perform(
            put(
                itemUrl(appointmentId) +
                    "/cancel"
            )
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "cancellationReason":
                        " Patient requested cancellation "
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.status")
                    .value("CANCELLED")
            )
            .andExpect(
                jsonPath("$.cancellationReason")
                    .value(
                        "Patient requested cancellation"
                    )
            )
    }

    @Test
    fun `cancelled appointment cannot be rescheduled`() {

        val appointmentId =
            createAppointment()

        cancelAppointment(
            appointmentId
        )

        mockMvc.perform(
            put(
                itemUrl(appointmentId) +
                    "/reschedule"
            )
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "scheduledStartAt":
                        "2026-10-05T03:00:00Z"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `manager can complete appointment`() {

        val appointmentId =
            createAppointment()

        mockMvc.perform(
            put(
                itemUrl(appointmentId) +
                    "/complete"
            )
                .with(jwtFor(MANAGER_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.status")
                    .value("COMPLETED")
            )
    }

    @Test
    fun `manager can mark appointment no show`() {

        val appointmentId =
            createAppointment()

        mockMvc.perform(
            put(
                itemUrl(appointmentId) +
                    "/no-show"
            )
                .with(jwtFor(MANAGER_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.status")
                    .value("NO_SHOW")
            )
    }

    // ========================================================
    // CLEAN / SEED
    // ========================================================

    private fun cleanBusinessData() {

        jdbcTemplate.update(
            "DELETE FROM appointment.appointments"
        )

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
            email = "appointment-admin@example.com",
            displayName = "Appointment Admin"
        )

        insertUser(
            id = MANAGER_USER_ID,
            subject = MANAGER_SUBJECT,
            email = "appointment-manager@example.com",
            displayName = "Appointment Manager"
        )

        insertUser(
            id = DOCTOR_USER_ID,
            subject = DOCTOR_SUBJECT,
            email = "appointment-doctor@example.com",
            displayName = "Appointment Doctor"
        )

        insertUser(
            id = DOCTOR_2_USER_ID,
            subject = "appointment-test-doctor-2",
            email = "appointment-doctor-2@example.com",
            displayName = "Appointment Doctor 2"
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

        insertMembership(
            id = DOCTOR_2_A_MEMBERSHIP_ID,
            userId = DOCTOR_2_USER_ID,
            organizationId = ORGANIZATION_A_ID
        )
    }

    private fun seedMembershipRoles() {

        insertMembershipRole(
            membershipId = ADMIN_A_MEMBERSHIP_ID,
            roleId = ORGANIZATION_ADMIN_ROLE_ID
        )

        insertMembershipRole(
            membershipId = ADMIN_B_MEMBERSHIP_ID,
            roleId = ORGANIZATION_ADMIN_ROLE_ID
        )

        insertMembershipRole(
            membershipId = MANAGER_A_MEMBERSHIP_ID,
            roleId = CLINIC_MANAGER_ROLE_ID
        )

        insertMembershipRole(
            membershipId = DOCTOR_A_MEMBERSHIP_ID,
            roleId = DOCTOR_ROLE_ID
        )

        insertMembershipRole(
            membershipId = DOCTOR_2_A_MEMBERSHIP_ID,
            roleId = DOCTOR_ROLE_ID
        )
    }

    private fun seedPatients() {

        insertPatient(
            id = PATIENT_A_ID,
            organizationId = ORGANIZATION_A_ID,
            facilityId = FACILITY_A1_ID,
            patientCode = "PAT-A-001",
            fullName = "Patient A"
        )

        insertPatient(
            id = PATIENT_A2_ID,
            organizationId = ORGANIZATION_A_ID,
            facilityId = FACILITY_A1_ID,
            patientCode = "PAT-A-002",
            fullName = "Patient A2"
        )

        insertPatient(
            id = PATIENT_B_ID,
            organizationId = ORGANIZATION_B_ID,
            facilityId = FACILITY_B1_ID,
            patientCode = "PAT-B-001",
            fullName = "Patient B"
        )
    }

    private fun seedPractitioners() {

        insertPractitioner(
            id = PRACTITIONER_A_ID,
            organizationId = ORGANIZATION_A_ID,
            membershipId = DOCTOR_A_MEMBERSHIP_ID,
            code = "DOC-A-001",
            name = "Doctor A"
        )

        insertPractitioner(
            id = PRACTITIONER_A2_ID,
            organizationId = ORGANIZATION_A_ID,
            membershipId = DOCTOR_2_A_MEMBERSHIP_ID,
            code = "DOC-A-002",
            name = "Doctor A2"
        )

        insertPractitioner(
            id = PRACTITIONER_B_ID,
            organizationId = ORGANIZATION_B_ID,
            membershipId = ADMIN_B_MEMBERSHIP_ID,
            code = "DOC-B-001",
            name = "Doctor B"
        )
    }

    private fun seedFacilityAssignments() {

        insertFacilityAssignment(
            id = DOCTOR_A_ASSIGNMENT_ID,
            membershipId = DOCTOR_A_MEMBERSHIP_ID
        )

        insertFacilityAssignment(
            id = DOCTOR_A2_ASSIGNMENT_ID,
            membershipId = DOCTOR_2_A_MEMBERSHIP_ID
        )
    }

    private fun seedServiceCatalog() {

        insertService(
            id = SERVICE_A_ID,
            organizationId = ORGANIZATION_A_ID,
            code = "CONSULT-A",
            name = "Consultation A"
        )

        insertService(
            id = SERVICE_B_ID,
            organizationId = ORGANIZATION_B_ID,
            code = "CONSULT-B",
            name = "Consultation B"
        )

        insertFacilityService(
            id = FACILITY_SERVICE_A_ID,
            organizationId = ORGANIZATION_A_ID,
            facilityId = FACILITY_A1_ID,
            serviceId = SERVICE_A_ID
        )

        insertFacilityService(
            id = FACILITY_SERVICE_B_ID,
            organizationId = ORGANIZATION_B_ID,
            facilityId = FACILITY_B1_ID,
            serviceId = SERVICE_B_ID
        )
    }

    private fun seedScheduling() {

        jdbcTemplate.update(
            """
            INSERT INTO scheduling.facility_scheduling_settings (
                id,
                organization_id,
                facility_id,
                time_zone_id,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                'Asia/Ho_Chi_Minh',
                ?,
                ?
            )
            """.trimIndent(),
            SCHEDULING_SETTINGS_A_ID,
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )

        insertAvailabilityRule(
            id = RULE_A_ID,
            practitionerId = PRACTITIONER_A_ID
        )

        insertAvailabilityRule(
            id = RULE_A2_ID,
            practitionerId = PRACTITIONER_A2_ID
        )
    }

    // ========================================================
    // INSERT HELPERS
    // ========================================================

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

    private fun insertPatient(
        id: UUID,
        organizationId: UUID,
        facilityId: UUID,
        patientCode: String,
        fullName: String
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO patient.patients (
                id,
                organization_id,
                managing_facility_id,
                patient_code,
                full_name,
                status,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                ?,
                ?,
                'ACTIVE',
                ?,
                ?
            )
            """.trimIndent(),
            id,
            organizationId,
            facilityId,
            patientCode,
            fullName,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )
    }

    private fun insertPractitioner(
        id: UUID,
        organizationId: UUID,
        membershipId: UUID,
        code: String,
        name: String
    ) {

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
                ?,
                ?,
                'DOCTOR',
                'ACTIVE',
                ?,
                ?
            )
            """.trimIndent(),
            id,
            organizationId,
            membershipId,
            code,
            name,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )
    }

    private fun insertFacilityAssignment(
        id: UUID,
        membershipId: UUID
    ) {

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
            id,
            membershipId,
            FACILITY_A1_ID
        )
    }

    private fun insertService(
        id: UUID,
        organizationId: UUID,
        code: String,
        name: String
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO service_catalog.services (
                id,
                organization_id,
                service_code,
                name,
                default_duration_minutes,
                status,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                ?,
                30,
                'ACTIVE',
                ?,
                ?
            )
            """.trimIndent(),
            id,
            organizationId,
            code,
            name,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )
    }

    private fun insertFacilityService(
        id: UUID,
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO service_catalog.facility_services (
                id,
                organization_id,
                facility_id,
                service_id,
                duration_minutes,
                price_amount,
                currency_code,
                booking_enabled,
                status,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                ?,
                30,
                100000.00,
                'VND',
                TRUE,
                'ACTIVE',
                ?,
                ?
            )
            """.trimIndent(),
            id,
            organizationId,
            facilityId,
            serviceId,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )
    }

    private fun insertAvailabilityRule(
        id: UUID,
        practitionerId: UUID
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO scheduling.practitioner_availability_rules (
                id,
                organization_id,
                facility_id,
                practitioner_id,
                day_of_week,
                start_local_time,
                end_local_time,
                effective_from,
                effective_to,
                status,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                ?,
                1,
                TIME '08:00:00',
                TIME '12:00:00',
                DATE '2026-10-01',
                DATE '2026-12-31',
                'ACTIVE',
                ?,
                ?
            )
            """.trimIndent(),
            id,
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            practitionerId,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )
    }

    private fun insertAvailabilityException(
        exceptionType: String,
        startAt: String,
        endAt: String
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO scheduling.practitioner_availability_exceptions (
                id,
                organization_id,
                facility_id,
                practitioner_id,
                exception_type,
                start_at,
                end_at,
                reason,
                status,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                ?,
                ?,
                CAST(? AS TIMESTAMPTZ),
                CAST(? AS TIMESTAMPTZ),
                'Appointment integration test',
                'ACTIVE',
                ?,
                ?
            )
            """.trimIndent(),
            UUID.randomUUID(),
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            PRACTITIONER_A_ID,
            exceptionType,
            startAt,
            endAt,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )
    }

    // ========================================================
    // API HELPERS
    // ========================================================

    private fun createAppointment(
        patientId: UUID = PATIENT_A_ID,
        practitionerId: UUID = PRACTITIONER_A_ID,
        facilityServiceId: UUID = FACILITY_SERVICE_A_ID,
        scheduledStartAt: String = VALID_START,
        subject: String = MANAGER_SUBJECT
    ): UUID {

        val result =
            mockMvc.perform(
                post(collectionUrl())
                    .with(jwtFor(subject))
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content(
                        appointmentJson(
                            patientId = patientId,
                            practitionerId =
                                practitionerId,
                            facilityServiceId =
                                facilityServiceId,
                            scheduledStartAt =
                                scheduledStartAt
                        )
                    )
            )
                .andExpect(status().isCreated)
                .andReturn()

        val responseBody =
            result.response.contentAsString

        val id =
            Regex(
                """"id"\s*:\s*"([^"]+)""""
            )
                .find(responseBody)
                ?.groupValues
                ?.get(1)
                ?: error(
                    "Appointment id missing from response: $responseBody"
                )

        return UUID.fromString(id)
    }

    private fun cancelAppointment(
        appointmentId: UUID
    ) {

        mockMvc.perform(
            put(
                itemUrl(appointmentId) +
                    "/cancel"
            )
                .with(jwtFor(MANAGER_SUBJECT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "cancellationReason":
                        "Cancelled by integration test"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
    }

    private fun jwtFor(
        subject: String
    ) =
        jwt().jwt {
            it.subject(subject)
        }

    private fun collectionUrl(
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        "/api/v1/organizations/" +
            "$organizationId/facilities/" +
            "$facilityId/appointments"

    private fun itemUrl(
        appointmentId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        collectionUrl(
            organizationId = organizationId,
            facilityId = facilityId
        ) + "/$appointmentId"

    private fun appointmentJson(
        patientId: UUID =
            PATIENT_A_ID,
        practitionerId: UUID =
            PRACTITIONER_A_ID,
        facilityServiceId: UUID =
            FACILITY_SERVICE_A_ID,
        scheduledStartAt: String =
            VALID_START,
        reason: String =
            "General consultation"
    ): String =
        """
        {
          "patientId": "$patientId",
          "practitionerId": "$practitionerId",
          "facilityServiceId": "$facilityServiceId",
          "scheduledStartAt": "$scheduledStartAt",
          "reason": "$reason"
        }
        """.trimIndent()

    // ========================================================
    // DATABASE ASSERTIONS
    // ========================================================

    private fun appointmentCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM appointment.appointments
            """.trimIndent(),
            Long::class.java
        )!!
}