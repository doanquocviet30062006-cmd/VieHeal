package com.clinic.platform.modules.reception

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.dao.DataIntegrityViolationException
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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
class QueueApiIntegrationTest(
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
                "10000000-0000-0000-0000-000000000801"
            )

        private val MANAGER_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000802"
            )

        private val RECEPTIONIST_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000803"
            )

        private val DOCTOR_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000804"
            )

        private val NURSE_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000805"
            )

        private val PHARMACIST_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000806"
            )

        private val LAB_TECHNICIAN_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000807"
            )

        private val ADMIN_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000801"
            )

        private val ADMIN_B_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000802"
            )

        private val MANAGER_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000803"
            )

        private val RECEPTIONIST_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000804"
            )

        private val DOCTOR_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000805"
            )

        private val NURSE_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000806"
            )

        private val PHARMACIST_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000807"
            )

        private val LAB_TECHNICIAN_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000808"
            )

        private val PATIENT_A_ID =
            UUID.fromString(
                "50000000-0000-0000-0000-000000000801"
            )

        private val PATIENT_B_ID =
            UUID.fromString(
                "50000000-0000-0000-0000-000000000802"
            )

        private val PRACTITIONER_A_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000801"
            )

        private val PRACTITIONER_B_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000802"
            )

        private val SERVICE_A_ID =
            UUID.fromString(
                "60000000-0000-0000-0000-000000000801"
            )

        private val SERVICE_B_ID =
            UUID.fromString(
                "60000000-0000-0000-0000-000000000802"
            )

        private val FACILITY_SERVICE_A_ID =
            UUID.fromString(
                "70000000-0000-0000-0000-000000000801"
            )

        private val FACILITY_SERVICE_B_ID =
            UUID.fromString(
                "70000000-0000-0000-0000-000000000802"
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

        private val NURSE_ROLE_ID =
            UUID.fromString(
                "00000000-0000-0000-0000-000000000005"
            )

        private val RECEPTIONIST_ROLE_ID =
            UUID.fromString(
                "00000000-0000-0000-0000-000000000006"
            )

        private val PHARMACIST_ROLE_ID =
            UUID.fromString(
                "00000000-0000-0000-0000-000000000007"
            )

        private val LAB_TECHNICIAN_ROLE_ID =
            UUID.fromString(
                "00000000-0000-0000-0000-000000000008"
            )

        private const val ADMIN_SUBJECT =
            "queue-test-admin"

        private const val MANAGER_SUBJECT =
            "queue-test-manager"

        private const val RECEPTIONIST_SUBJECT =
            "queue-test-receptionist"

        private const val DOCTOR_SUBJECT =
            "queue-test-doctor"

        private const val NURSE_SUBJECT =
            "queue-test-nurse"

        private const val PHARMACIST_SUBJECT =
            "queue-test-pharmacist"

        private const val LAB_TECHNICIAN_SUBJECT =
            "queue-test-lab-technician"
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
        seedServiceCatalog()
    }

    // ========================================================
    // AUTHENTICATION / AUTHORIZATION
    // ========================================================

    @Test
    fun `check in without JWT returns 401`() {

        val appointmentId =
            insertAppointment()

        mockMvc.perform(
            post(collectionUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    checkInJson(
                        appointmentId = appointmentId
                    )
                )
        )
            .andExpect(status().isUnauthorized)

        assertEquals(
            0L,
            queueCount()
        )
    }

    @Test
    fun `receptionist can create queue entry`() {

        val appointmentId =
            insertAppointment()

        mockMvc.perform(
            post(collectionUrl())
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    checkInJson(
                        appointmentId = appointmentId,
                        note = "  Patient arrived  "
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(
                jsonPath("\$.appointmentId")
                    .value(
                        appointmentId.toString()
                    )
            )
            .andExpect(
                jsonPath("\$.patientId")
                    .value(
                        PATIENT_A_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("\$.practitionerId")
                    .value(
                        PRACTITIONER_A_ID.toString()
                    )
            )
            .andExpect(
                jsonPath("\$.status")
                    .value("WAITING")
            )
            .andExpect(
                jsonPath("\$.note")
                    .value("Patient arrived")
            )
            .andExpect(
                jsonPath("\$.createdByUserId")
                    .value(
                        RECEPTIONIST_USER_ID.toString()
                    )
            )

        assertEquals(
            1L,
            queueCount()
        )
    }

    @Test
    fun `doctor and nurse cannot create queue entry`() {

        val appointmentId =
            insertAppointment()

        for (
            subject in listOf(
                DOCTOR_SUBJECT,
                NURSE_SUBJECT
            )
        ) {
            mockMvc.perform(
                post(collectionUrl())
                    .with(
                        jwtFor(subject)
                    )
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content(
                        checkInJson(
                            appointmentId
                        )
                    )
            )
                .andExpect(status().isForbidden)
        }

        assertEquals(
            0L,
            queueCount()
        )
    }

    @Test
    fun `pharmacist and lab technician cannot read queue`() {

        for (
            subject in listOf(
                PHARMACIST_SUBJECT,
                LAB_TECHNICIAN_SUBJECT
            )
        ) {
            mockMvc.perform(
                get(collectionUrl())
                    .with(
                        jwtFor(subject)
                    )
            )
                .andExpect(status().isForbidden)
        }
    }

    @Test
    fun `receptionist cannot access organization without membership`() {

        mockMvc.perform(
            get(
                collectionUrl(
                    organizationId =
                        ORGANIZATION_B_ID,
                    facilityId =
                        FACILITY_B1_ID
                )
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(status().isForbidden)
    }

    // ========================================================
    // CHECK-IN
    // ========================================================

    @Test
    fun `duplicate check in is rejected`() {

        val appointmentId =
            insertAppointment()

        checkIn(
            appointmentId = appointmentId
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    checkInJson(
                        appointmentId
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            1L,
            queueCount()
        )
    }

    @Test
    fun `non scheduled appointment cannot be checked in`() {

        val appointmentId =
            insertAppointment(
                status = "CANCELLED",
                cancellationReason =
                    "Cancelled before arrival"
            )

        mockMvc.perform(
            post(collectionUrl())
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    checkInJson(
                        appointmentId
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(
            0L,
            queueCount()
        )
    }

    // ========================================================
    // READ
    // ========================================================

    @Test
    fun `doctor and nurse can read queue`() {

        val appointmentId =
            insertAppointment()

        val queueEntryId =
            checkIn(
                appointmentId = appointmentId
            )

        for (
            subject in listOf(
                DOCTOR_SUBJECT,
                NURSE_SUBJECT
            )
        ) {
            mockMvc.perform(
                get(
                    itemUrl(queueEntryId)
                )
                    .with(
                        jwtFor(subject)
                    )
            )
                .andExpect(status().isOk)
                .andExpect(
                    jsonPath("\$.id")
                        .value(
                            queueEntryId.toString()
                        )
                )
                .andExpect(
                    jsonPath("\$.status")
                        .value("WAITING")
                )

            mockMvc.perform(
                get(collectionUrl())
                    .with(
                        jwtFor(subject)
                    )
            )
                .andExpect(status().isOk)
        }
    }

    @Test
    fun `queue entry is isolated by organization and facility`() {

        val appointmentId =
            insertAppointment()

        val queueEntryId =
            checkIn(
                appointmentId = appointmentId
            )

        mockMvc.perform(
            get(
                itemUrl(
                    queueEntryId = queueEntryId,
                    organizationId =
                        ORGANIZATION_B_ID,
                    facilityId =
                        FACILITY_B1_ID
                )
            )
                .with(
                    jwtFor(
                        ADMIN_SUBJECT
                    )
                )
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `queue list is ordered by check in time and supports status filter`() {

        val appointment1 =
            insertAppointment(
                startAt =
                    "2026-10-05T02:00:00Z",
                endAt =
                    "2026-10-05T02:30:00Z"
            )

        val appointment2 =
            insertAppointment(
                startAt =
                    "2026-10-05T03:00:00Z",
                endAt =
                    "2026-10-05T03:30:00Z"
            )

        val queue1 =
            checkIn(
                appointmentId = appointment1
            )

        Thread.sleep(10)

        val queue2 =
            checkIn(
                appointmentId = appointment2
            )

        mockMvc.perform(
            get(collectionUrl())
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("\$[0].id")
                    .value(queue1.toString())
            )
            .andExpect(
                jsonPath("\$[1].id")
                    .value(queue2.toString())
            )

        mockMvc.perform(
            put(
                itemUrl(queue1) +
                    "/call"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get(
                collectionUrl() +
                    "?status=WAITING"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("\$[0].id")
                    .value(queue2.toString())
            )
            .andExpect(
                jsonPath("\$[0].status")
                    .value("WAITING")
            )
    }

    // ========================================================
    // LIFECYCLE
    // ========================================================

    @Test
    fun `queue lifecycle waiting called serving completed succeeds`() {

        val appointmentId =
            insertAppointment()

        val queueEntryId =
            checkIn(
                appointmentId = appointmentId
            )

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/call"
            )
                .with(
                    jwtFor(
                        DOCTOR_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("\$.status")
                    .value("CALLED")
            )
            .andExpect(
                jsonPath("\$.calledAt")
                    .exists()
            )

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/start-serving"
            )
                .with(
                    jwtFor(
                        NURSE_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("\$.status")
                    .value("SERVING")
            )
            .andExpect(
                jsonPath("\$.servingStartedAt")
                    .exists()
            )

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/complete"
            )
                .with(
                    jwtFor(
                        DOCTOR_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("\$.status")
                    .value("COMPLETED")
            )
            .andExpect(
                jsonPath("\$.completedAt")
                    .exists()
            )

        assertEquals(
            "COMPLETED",
            queueStatus(queueEntryId)
        )
    }

    @Test
    fun `queue can be cancelled from waiting called and serving`() {

        val waitingAppointment =
            insertAppointment(
                startAt =
                    "2026-10-05T02:00:00Z",
                endAt =
                    "2026-10-05T02:30:00Z"
            )

        val calledAppointment =
            insertAppointment(
                startAt =
                    "2026-10-05T03:00:00Z",
                endAt =
                    "2026-10-05T03:30:00Z"
            )

        val servingAppointment =
            insertAppointment(
                startAt =
                    "2026-10-05T04:00:00Z",
                endAt =
                    "2026-10-05T04:30:00Z"
            )

        val waitingQueue =
            checkIn(waitingAppointment)

        val calledQueue =
            checkIn(calledAppointment)

        val servingQueue =
            checkIn(servingAppointment)

        mockMvc.perform(
            put(
                itemUrl(calledQueue) +
                    "/call"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            put(
                itemUrl(servingQueue) +
                    "/call"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            put(
                itemUrl(servingQueue) +
                    "/start-serving"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)

        for (
            queueEntryId in listOf(
                waitingQueue,
                calledQueue,
                servingQueue
            )
        ) {
            mockMvc.perform(
                put(
                    itemUrl(queueEntryId) +
                        "/cancel"
                )
                    .with(
                        jwtFor(
                            RECEPTIONIST_SUBJECT
                        )
                    )
            )
                .andExpect(status().isOk)
                .andExpect(
                    jsonPath("\$.status")
                        .value("CANCELLED")
                )
                .andExpect(
                    jsonPath("\$.cancelledAt")
                        .exists()
                )
        }
    }

    @Test
    fun `invalid queue transition is rejected`() {

        val appointmentId =
            insertAppointment()

        val queueEntryId =
            checkIn(appointmentId)

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/call"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/call"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
        )
            .andExpect(
                status().is4xxClientError
            )

        assertEquals(
            "CALLED",
            queueStatus(queueEntryId)
        )
    }

    // ========================================================
    // NOTE
    // ========================================================

    @Test
    fun `active queue note can be updated and blank note becomes null`() {

        val appointmentId =
            insertAppointment()

        val queueEntryId =
            checkIn(
                appointmentId = appointmentId,
                note = "Initial note"
            )

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/note"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    """
                    {
                      "note": "  Updated queue note  "
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("\$.note")
                    .value(
                        "Updated queue note"
                    )
            )

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/note"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    """
                    {
                      "note": "   "
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)

        val noteIsNull =
            jdbcTemplate.queryForObject(
                """
                SELECT note IS NULL
                FROM reception.queue_entries
                WHERE id = ?
                """.trimIndent(),
                Boolean::class.java,
                queueEntryId
            )!!

        assertTrue(noteIsNull)
    }

    @Test
    fun `terminal queue note cannot be updated`() {

        val appointmentId =
            insertAppointment()

        val queueEntryId =
            checkIn(appointmentId)

        performQueueAction(
            queueEntryId,
            "call"
        )

        performQueueAction(
            queueEntryId,
            "start-serving"
        )

        performQueueAction(
            queueEntryId,
            "complete"
        )

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/note"
            )
                .with(
                    jwtFor(
                        RECEPTIONIST_SUBJECT
                    )
                )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    """
                    {
                      "note": "Should fail"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(
                status().is4xxClientError
            )
    }

    // ========================================================
    // DATABASE CONSTRAINTS
    // ========================================================

    @Test
    fun `database prevents two queue entries for same appointment`() {

        val appointmentId =
            insertAppointment()

        checkIn(appointmentId)

        assertFailsWith<
            DataIntegrityViolationException
        > {
            insertRawWaitingQueueEntry(
                appointmentId =
                    appointmentId,
                patientId =
                    PATIENT_A_ID,
                practitionerId =
                    PRACTITIONER_A_ID
            )
        }

        assertEquals(
            1L,
            queueCount()
        )
    }

    @Test
    fun `database prevents queue appointment patient mismatch`() {

        val appointmentId =
            insertAppointment()

        assertFailsWith<
            DataIntegrityViolationException
        > {
            insertRawWaitingQueueEntry(
                appointmentId =
                    appointmentId,
                patientId =
                    PATIENT_B_ID,
                practitionerId =
                    PRACTITIONER_A_ID
            )
        }

        assertEquals(
            0L,
            queueCount()
        )
    }

    @Test
    fun `database prevents inconsistent lifecycle timestamps`() {

        val appointmentId =
            insertAppointment()

        assertFailsWith<
            DataIntegrityViolationException
        > {
            jdbcTemplate.update(
                """
                INSERT INTO reception.queue_entries (
                    id,
                    organization_id,
                    facility_id,
                    appointment_id,
                    patient_id,
                    practitioner_id,
                    status,
                    checked_in_at,
                    called_at,
                    serving_started_at,
                    completed_at,
                    cancelled_at,
                    created_by_user_id,
                    updated_by_user_id
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    'CALLED',
                    CURRENT_TIMESTAMP,
                    NULL,
                    NULL,
                    NULL,
                    NULL,
                    ?,
                    ?
                )
                """.trimIndent(),
                UUID.randomUUID(),
                ORGANIZATION_A_ID,
                FACILITY_A1_ID,
                appointmentId,
                PATIENT_A_ID,
                PRACTITIONER_A_ID,
                RECEPTIONIST_USER_ID,
                RECEPTIONIST_USER_ID
            )
        }

        assertEquals(
            0L,
            queueCount()
        )
    }

    // ========================================================
    // CLEAN / SEED
    // ========================================================

    private fun cleanBusinessData() {

        jdbcTemplate.update(
            "DELETE FROM reception.queue_entries"
        )

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
            VALUES (
                ?,
                'ORG-A',
                'Organization A',
                'ACTIVE'
            )
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
            VALUES (
                ?,
                'ORG-B',
                'Organization B',
                'ACTIVE'
            )
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
            email = "queue-admin@example.com",
            displayName = "Queue Admin"
        )

        insertUser(
            id = MANAGER_USER_ID,
            subject = MANAGER_SUBJECT,
            email = "queue-manager@example.com",
            displayName = "Queue Manager"
        )

        insertUser(
            id = RECEPTIONIST_USER_ID,
            subject = RECEPTIONIST_SUBJECT,
            email = "queue-receptionist@example.com",
            displayName = "Queue Receptionist"
        )

        insertUser(
            id = DOCTOR_USER_ID,
            subject = DOCTOR_SUBJECT,
            email = "queue-doctor@example.com",
            displayName = "Queue Doctor"
        )

        insertUser(
            id = NURSE_USER_ID,
            subject = NURSE_SUBJECT,
            email = "queue-nurse@example.com",
            displayName = "Queue Nurse"
        )

        insertUser(
            id = PHARMACIST_USER_ID,
            subject = PHARMACIST_SUBJECT,
            email = "queue-pharmacist@example.com",
            displayName = "Queue Pharmacist"
        )

        insertUser(
            id = LAB_TECHNICIAN_USER_ID,
            subject = LAB_TECHNICIAN_SUBJECT,
            email = "queue-lab@example.com",
            displayName = "Queue Lab Technician"
        )
    }

    private fun seedMemberships() {

        insertMembership(
            id = ADMIN_A_MEMBERSHIP_ID,
            userId = ADMIN_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
        )

        insertMembership(
            id = ADMIN_B_MEMBERSHIP_ID,
            userId = ADMIN_USER_ID,
            organizationId =
                ORGANIZATION_B_ID
        )

        insertMembership(
            id = MANAGER_A_MEMBERSHIP_ID,
            userId = MANAGER_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
        )

        insertMembership(
            id = RECEPTIONIST_A_MEMBERSHIP_ID,
            userId =
                RECEPTIONIST_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
        )

        insertMembership(
            id = DOCTOR_A_MEMBERSHIP_ID,
            userId = DOCTOR_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
        )

        insertMembership(
            id = NURSE_A_MEMBERSHIP_ID,
            userId = NURSE_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
        )

        insertMembership(
            id = PHARMACIST_A_MEMBERSHIP_ID,
            userId =
                PHARMACIST_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
        )

        insertMembership(
            id =
                LAB_TECHNICIAN_A_MEMBERSHIP_ID,
            userId =
                LAB_TECHNICIAN_USER_ID,
            organizationId =
                ORGANIZATION_A_ID
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
                RECEPTIONIST_A_MEMBERSHIP_ID,
            roleId =
                RECEPTIONIST_ROLE_ID
        )

        insertMembershipRole(
            membershipId =
                DOCTOR_A_MEMBERSHIP_ID,
            roleId =
                DOCTOR_ROLE_ID
        )

        insertMembershipRole(
            membershipId =
                NURSE_A_MEMBERSHIP_ID,
            roleId =
                NURSE_ROLE_ID
        )

        insertMembershipRole(
            membershipId =
                PHARMACIST_A_MEMBERSHIP_ID,
            roleId =
                PHARMACIST_ROLE_ID
        )

        insertMembershipRole(
            membershipId =
                LAB_TECHNICIAN_A_MEMBERSHIP_ID,
            roleId =
                LAB_TECHNICIAN_ROLE_ID
        )
    }

    private fun seedPatients() {

        insertPatient(
            id = PATIENT_A_ID,
            organizationId =
                ORGANIZATION_A_ID,
            facilityId =
                FACILITY_A1_ID,
            patientCode =
                "PAT-A-001",
            fullName =
                "Patient A"
        )

        insertPatient(
            id = PATIENT_B_ID,
            organizationId =
                ORGANIZATION_B_ID,
            facilityId =
                FACILITY_B1_ID,
            patientCode =
                "PAT-B-001",
            fullName =
                "Patient B"
        )
    }

    private fun seedPractitioners() {

        insertPractitioner(
            id = PRACTITIONER_A_ID,
            organizationId =
                ORGANIZATION_A_ID,
            membershipId =
                DOCTOR_A_MEMBERSHIP_ID,
            code =
                "DOC-A-001",
            name =
                "Doctor A"
        )

        insertPractitioner(
            id = PRACTITIONER_B_ID,
            organizationId =
                ORGANIZATION_B_ID,
            membershipId =
                ADMIN_B_MEMBERSHIP_ID,
            code =
                "DOC-B-001",
            name =
                "Doctor B"
        )
    }

    private fun seedServiceCatalog() {

        insertService(
            id = SERVICE_A_ID,
            organizationId =
                ORGANIZATION_A_ID,
            code =
                "SRV-A-001",
            name =
                "General Consultation A"
        )

        insertService(
            id = SERVICE_B_ID,
            organizationId =
                ORGANIZATION_B_ID,
            code =
                "SRV-B-001",
            name =
                "General Consultation B"
        )

        insertFacilityService(
            id = FACILITY_SERVICE_A_ID,
            organizationId =
                ORGANIZATION_A_ID,
            facilityId =
                FACILITY_A1_ID,
            serviceId =
                SERVICE_A_ID
        )

        insertFacilityService(
            id = FACILITY_SERVICE_B_ID,
            organizationId =
                ORGANIZATION_B_ID,
            facilityId =
                FACILITY_B1_ID,
            serviceId =
                SERVICE_B_ID
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
            VALUES (
                ?,
                ?,
                ?,
                'ACTIVE'
            )
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

    private fun insertAppointment(
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID,
        patientId: UUID =
            PATIENT_A_ID,
        practitionerId: UUID =
            PRACTITIONER_A_ID,
        facilityServiceId: UUID =
            FACILITY_SERVICE_A_ID,
        startAt: String =
            "2026-10-05T02:00:00Z",
        endAt: String =
            "2026-10-05T02:30:00Z",
        status: String =
            "SCHEDULED",
        cancellationReason: String? =
            null
    ): UUID {

        val appointmentId =
            UUID.randomUUID()

        jdbcTemplate.update(
            """
            INSERT INTO appointment.appointments (
                id,
                organization_id,
                facility_id,
                patient_id,
                practitioner_id,
                facility_service_id,
                scheduled_start_at,
                scheduled_end_at,
                status,
                reason,
                cancellation_reason,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                CAST(? AS TIMESTAMPTZ),
                CAST(? AS TIMESTAMPTZ),
                ?,
                'Queue integration test',
                ?,
                ?,
                ?
            )
            """.trimIndent(),
            appointmentId,
            organizationId,
            facilityId,
            patientId,
            practitionerId,
            facilityServiceId,
            startAt,
            endAt,
            status,
            cancellationReason,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )

        return appointmentId
    }

    private fun insertRawWaitingQueueEntry(
        appointmentId: UUID,
        patientId: UUID,
        practitionerId: UUID
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO reception.queue_entries (
                id,
                organization_id,
                facility_id,
                appointment_id,
                patient_id,
                practitioner_id,
                status,
                checked_in_at,
                created_by_user_id,
                updated_by_user_id
            )
            VALUES (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                'WAITING',
                CURRENT_TIMESTAMP,
                ?,
                ?
            )
            """.trimIndent(),
            UUID.randomUUID(),
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            appointmentId,
            patientId,
            practitionerId,
            RECEPTIONIST_USER_ID,
            RECEPTIONIST_USER_ID
        )
    }

    // ========================================================
    // API HELPERS
    // ========================================================

    private fun checkIn(
        appointmentId: UUID,
        note: String? = null,
        subject: String =
            RECEPTIONIST_SUBJECT
    ): UUID {

        val result =
            mockMvc.perform(
                post(collectionUrl())
                    .with(
                        jwtFor(subject)
                    )
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content(
                        checkInJson(
                            appointmentId =
                                appointmentId,
                            note = note
                        )
                    )
            )
                .andExpect(
                    status().isCreated
                )
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
                    "Queue entry id missing from response: $responseBody"
                )

        return UUID.fromString(id)
    }

    private fun performQueueAction(
        queueEntryId: UUID,
        action: String,
        subject: String =
            RECEPTIONIST_SUBJECT
    ) {

        mockMvc.perform(
            put(
                itemUrl(queueEntryId) +
                    "/$action"
            )
                .with(
                    jwtFor(subject)
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
            "$facilityId/queue"

    private fun itemUrl(
        queueEntryId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        collectionUrl(
            organizationId =
                organizationId,
            facilityId =
                facilityId
        ) + "/$queueEntryId"

    private fun checkInJson(
        appointmentId: UUID,
        note: String? = null
    ): String {

        val noteJson =
            if (note == null) {
                "null"
            } else {
                "\"${escapeJson(note)}\""
            }

        return """
            {
              "appointmentId": "$appointmentId",
              "note": $noteJson
            }
        """.trimIndent()
    }

    private fun escapeJson(
        value: String
    ): String =
        value
            .replace(
                "\\",
                "\\\\"
            )
            .replace(
                "\"",
                "\\\""
            )

    // ========================================================
    // DATABASE ASSERTIONS
    // ========================================================

    private fun queueCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM reception.queue_entries
            """.trimIndent(),
            Long::class.java
        )!!

    private fun queueStatus(
        queueEntryId: UUID
    ): String =
        jdbcTemplate.queryForObject(
            """
            SELECT status
            FROM reception.queue_entries
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            queueEntryId
        )!!
}