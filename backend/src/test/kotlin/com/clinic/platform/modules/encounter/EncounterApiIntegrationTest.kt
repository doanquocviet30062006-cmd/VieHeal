package com.clinic.platform.modules.encounter

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
class EncounterApiIntegrationTest(
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
                "10000000-0000-0000-0000-000000000901"
            )

        private val MANAGER_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000902"
            )

        private val RECEPTIONIST_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000903"
            )

        private val DOCTOR_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000904"
            )

        private val NURSE_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000905"
            )

        private val PHARMACIST_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000906"
            )

        private val LAB_TECHNICIAN_USER_ID =
            UUID.fromString(
                "10000000-0000-0000-0000-000000000907"
            )

        private val ADMIN_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000901"
            )

        private val ADMIN_B_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000902"
            )

        private val MANAGER_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000903"
            )

        private val RECEPTIONIST_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000904"
            )

        private val DOCTOR_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000905"
            )

        private val NURSE_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000906"
            )

        private val PHARMACIST_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000907"
            )

        private val LAB_TECHNICIAN_A_MEMBERSHIP_ID =
            UUID.fromString(
                "20000000-0000-0000-0000-000000000908"
            )

        private val PATIENT_A_ID =
            UUID.fromString(
                "50000000-0000-0000-0000-000000000901"
            )

        private val PATIENT_A2_ID =
            UUID.fromString(
                "50000000-0000-0000-0000-000000000902"
            )

        private val PRACTITIONER_A_ID =
            UUID.fromString(
                "30000000-0000-0000-0000-000000000901"
            )

        private val SERVICE_A_ID =
            UUID.fromString(
                "60000000-0000-0000-0000-000000000901"
            )

        private val FACILITY_SERVICE_A_ID =
            UUID.fromString(
                "70000000-0000-0000-0000-000000000901"
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
            "encounter-test-admin"

        private const val MANAGER_SUBJECT =
            "encounter-test-manager"

        private const val RECEPTIONIST_SUBJECT =
            "encounter-test-receptionist"

        private const val DOCTOR_SUBJECT =
            "encounter-test-doctor"

        private const val NURSE_SUBJECT =
            "encounter-test-nurse"

        private const val PHARMACIST_SUBJECT =
            "encounter-test-pharmacist"

        private const val LAB_TECHNICIAN_SUBJECT =
            "encounter-test-lab-technician"
    }

    private data class VisitSeed(
        val appointmentId: UUID,
        val queueEntryId: UUID,
        val patientId: UUID,
        val practitionerId: UUID
    )

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
    fun `start encounter without JWT returns 401`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        mockMvc.perform(
            post(collectionUrl())
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    startEncounterJson(
                        visit.queueEntryId
                    )
                )
        )
            .andExpect(status().isUnauthorized)

        assertEquals(0L, encounterCount())
        assertEquals(0L, clinicalNoteCount())
    }

    @Test
    fun `only doctor can start encounter`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        listOf(
            ADMIN_SUBJECT,
            MANAGER_SUBJECT,
            RECEPTIONIST_SUBJECT,
            NURSE_SUBJECT,
            PHARMACIST_SUBJECT,
            LAB_TECHNICIAN_SUBJECT
        )
            .forEach { subject ->

                mockMvc.perform(
                    post(collectionUrl())
                        .with(jwtFor(subject))
                        .contentType(
                            MediaType.APPLICATION_JSON
                        )
                        .content(
                            startEncounterJson(
                                visit.queueEntryId
                            )
                        )
                )
                    .andExpect(
                        status().isForbidden
                    )
            }

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    startEncounterJson(
                        visit.queueEntryId
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
                jsonPath("$.appointmentId")
                    .value(
                        visit.appointmentId.toString()
                    )
            )
            .andExpect(
                jsonPath("$.queueEntryId")
                    .value(
                        visit.queueEntryId.toString()
                    )
            )
            .andExpect(
                jsonPath("$.patientId")
                    .value(
                        visit.patientId.toString()
                    )
            )
            .andExpect(
                jsonPath("$.practitionerId")
                    .value(
                        visit.practitionerId.toString()
                    )
            )
            .andExpect(
                jsonPath("$.status")
                    .value("IN_PROGRESS")
            )

        assertEquals(1L, encounterCount())
        assertEquals(1L, clinicalNoteCount())
    }

    @Test
    fun `encounter metadata read permissions match role policy`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        listOf(
            ADMIN_SUBJECT,
            MANAGER_SUBJECT,
            DOCTOR_SUBJECT,
            NURSE_SUBJECT
        )
            .forEach { subject ->

                mockMvc.perform(
                    get(itemUrl(encounterId))
                        .with(jwtFor(subject))
                )
                    .andExpect(status().isOk)
                    .andExpect(
                        jsonPath("$.id")
                            .value(
                                encounterId.toString()
                            )
                    )

                mockMvc.perform(
                    get(collectionUrl())
                        .with(jwtFor(subject))
                )
                    .andExpect(status().isOk)
            }

        listOf(
            RECEPTIONIST_SUBJECT,
            PHARMACIST_SUBJECT,
            LAB_TECHNICIAN_SUBJECT
        )
            .forEach { subject ->

                mockMvc.perform(
                    get(itemUrl(encounterId))
                        .with(jwtFor(subject))
                )
                    .andExpect(
                        status().isForbidden
                    )

                mockMvc.perform(
                    get(collectionUrl())
                        .with(jwtFor(subject))
                )
                    .andExpect(
                        status().isForbidden
                    )
            }
    }

    @Test
    fun `clinical note read permissions are separate from encounter metadata`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        listOf(
            DOCTOR_SUBJECT,
            NURSE_SUBJECT
        )
            .forEach { subject ->

                mockMvc.perform(
                    get(
                        clinicalNoteUrl(
                            encounterId
                        )
                    )
                        .with(jwtFor(subject))
                )
                    .andExpect(status().isOk)
                    .andExpect(
                        jsonPath("$.encounterId")
                            .value(
                                encounterId.toString()
                            )
                    )
            }

        listOf(
            ADMIN_SUBJECT,
            MANAGER_SUBJECT,
            RECEPTIONIST_SUBJECT,
            PHARMACIST_SUBJECT,
            LAB_TECHNICIAN_SUBJECT
        )
            .forEach { subject ->

                mockMvc.perform(
                    get(
                        clinicalNoteUrl(
                            encounterId
                        )
                    )
                        .with(jwtFor(subject))
                )
                    .andExpect(
                        status().isForbidden
                    )
            }
    }

    @Test
    fun `only doctor can update clinical note`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        val requestBody =
            clinicalNoteJson(
                chiefComplaint =
                    "  Headache for two days  ",
                subjective =
                    "   ",
                objective =
                    "Blood pressure stable",
                assessment =
                    "Tension headache",
                plan =
                    "Hydration and rest"
            )

        listOf(
            ADMIN_SUBJECT,
            MANAGER_SUBJECT,
            RECEPTIONIST_SUBJECT,
            NURSE_SUBJECT,
            PHARMACIST_SUBJECT,
            LAB_TECHNICIAN_SUBJECT
        )
            .forEach { subject ->

                mockMvc.perform(
                    put(
                        clinicalNoteUrl(
                            encounterId
                        )
                    )
                        .with(jwtFor(subject))
                        .contentType(
                            MediaType.APPLICATION_JSON
                        )
                        .content(requestBody)
                )
                    .andExpect(
                        status().isForbidden
                    )
            }

        mockMvc.perform(
            put(
                clinicalNoteUrl(
                    encounterId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.chiefComplaint")
                    .value(
                        "Headache for two days"
                    )
            )
            .andExpect(
                jsonPath("$.objective")
                    .value(
                        "Blood pressure stable"
                    )
            )
            .andExpect(
                jsonPath("$.assessment")
                    .value(
                        "Tension headache"
                    )
            )
            .andExpect(
                jsonPath("$.plan")
                    .value(
                        "Hydration and rest"
                    )
            )

        assertEquals(
            "Headache for two days",
            clinicalNoteChiefComplaint(
                encounterId
            )
        )

        assertEquals(
            null,
            clinicalNoteSubjective(
                encounterId
            )
        )
    }

    @Test
    fun `only doctor can update encounter lifecycle`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        listOf(
            ADMIN_SUBJECT,
            MANAGER_SUBJECT,
            RECEPTIONIST_SUBJECT,
            NURSE_SUBJECT,
            PHARMACIST_SUBJECT,
            LAB_TECHNICIAN_SUBJECT
        )
            .forEach { subject ->

                mockMvc.perform(
                    put(
                        itemUrl(encounterId) +
                            "/complete"
                    )
                        .with(jwtFor(subject))
                )
                    .andExpect(
                        status().isForbidden
                    )
            }

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/complete"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.status")
                    .value("COMPLETED")
            )
    }

    // ========================================================
    // START ENCOUNTER
    // ========================================================

    @Test
    fun `start encounter requires serving queue entry`() {

        val visit =
            insertVisit(
                queueStatus = "WAITING"
            )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    startEncounterJson(
                        visit.queueEntryId
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "QUEUE_ENTRY_NOT_SERVING"
                    )
            )
            .andExpect(
                jsonPath("$.currentState")
                    .value("WAITING")
            )

        assertEquals(0L, encounterCount())
    }

    @Test
    fun `start encounter requires scheduled appointment`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING",
                appointmentStatus =
                    "CANCELLED"
            )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    startEncounterJson(
                        visit.queueEntryId
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "APPOINTMENT_NOT_SCHEDULED_FOR_ENCOUNTER"
                    )
            )
            .andExpect(
                jsonPath("$.currentState")
                    .value("CANCELLED")
            )

        assertEquals(0L, encounterCount())
    }

    @Test
    fun `duplicate encounter for same appointment and queue is rejected`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        startEncounter(
            visit.queueEntryId
        )

        mockMvc.perform(
            post(collectionUrl())
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    startEncounterJson(
                        visit.queueEntryId
                    )
                )
        )
            .andExpect(status().isConflict)

        assertEquals(1L, encounterCount())
        assertEquals(1L, clinicalNoteCount())
    }

    // ========================================================
    // READ / SCOPE
    // ========================================================

    @Test
    fun `encounter is hidden behind wrong facility and wrong organization scope`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        mockMvc.perform(
            get(
                itemUrl(
                    encounterId =
                        encounterId,
                    organizationId =
                        ORGANIZATION_A_ID,
                    facilityId =
                        FACILITY_A2_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
        )
            .andExpect(status().isNotFound)

        mockMvc.perform(
            get(
                itemUrl(
                    encounterId =
                        encounterId,
                    organizationId =
                        ORGANIZATION_B_ID,
                    facilityId =
                        FACILITY_B1_ID
                )
            )
                .with(jwtFor(ADMIN_SUBJECT))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `list encounters can filter by status`() {

        val firstVisit =
            insertVisit(
                queueStatus = "SERVING",
                startAt =
                    "2026-10-05T02:00:00Z",
                endAt =
                    "2026-10-05T02:30:00Z"
            )

        val firstEncounterId =
            startEncounter(
                firstVisit.queueEntryId
            )

        mockMvc.perform(
            put(
                itemUrl(
                    firstEncounterId
                ) + "/complete"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)

        val secondVisit =
            insertVisit(
                queueStatus = "SERVING",
                startAt =
                    "2026-10-05T03:00:00Z",
                endAt =
                    "2026-10-05T03:30:00Z"
            )

        val secondEncounterId =
            startEncounter(
                secondVisit.queueEntryId
            )

        mockMvc.perform(
            get(
                collectionUrl() +
                    "?status=COMPLETED"
            )
                .with(jwtFor(NURSE_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.length()")
                    .value(1)
            )
            .andExpect(
                jsonPath("$[0].id")
                    .value(
                        firstEncounterId.toString()
                    )
            )
            .andExpect(
                jsonPath("$[0].status")
                    .value("COMPLETED")
            )

        mockMvc.perform(
            get(
                collectionUrl() +
                    "?status=IN_PROGRESS"
            )
                .with(jwtFor(NURSE_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.length()")
                    .value(1)
            )
            .andExpect(
                jsonPath("$[0].id")
                    .value(
                        secondEncounterId.toString()
                    )
            )
    }

    // ========================================================
    // CLINICAL NOTE
    // ========================================================

    @Test
    fun `clinical note validation rejects oversized content`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        mockMvc.perform(
            put(
                clinicalNoteUrl(
                    encounterId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    clinicalNoteJson(
                        chiefComplaint =
                            "x".repeat(2001),
                        subjective = null,
                        objective = null,
                        assessment = null,
                        plan = null
                    )
                )
        )
            .andExpect(status().isBadRequest)

        assertEquals(
            null,
            clinicalNoteChiefComplaint(
                encounterId
            )
        )
    }

    @Test
    fun `clinical note becomes immutable after encounter completion`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/complete"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            put(
                clinicalNoteUrl(
                    encounterId
                )
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    clinicalNoteJson(
                        chiefComplaint =
                            "Late edit",
                        subjective = null,
                        objective = null,
                        assessment = null,
                        plan = null
                    )
                )
        )
            .andExpect(status().isConflict)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "ENCOUNTER_CLINICAL_NOTE_IMMUTABLE"
                    )
            )
            .andExpect(
                jsonPath("$.currentState")
                    .value("COMPLETED")
            )
    }

    // ========================================================
    // LIFECYCLE
    // ========================================================

    @Test
    fun `doctor can complete an in progress encounter`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/complete"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.id")
                    .value(
                        encounterId.toString()
                    )
            )
            .andExpect(
                jsonPath("$.status")
                    .value("COMPLETED")
            )

        assertEquals(
            "COMPLETED",
            encounterStatus(
                encounterId
            )
        )

        assertTrue(
            encounterCompletedAtPresent(
                encounterId
            )
        )

        assertEquals(
            false,
            encounterCancelledAtPresent(
                encounterId
            )
        )
    }

    @Test
    fun `completed encounter cannot be completed again`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/complete"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/complete"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "ENCOUNTER_INVALID_STATUS_TRANSITION"
                    )
            )
    }

    @Test
    fun `doctor can cancel encounter with normalized reason`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/cancel"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    cancelJson(
                        "  Patient requested transfer  "
                    )
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
                        "Patient requested transfer"
                    )
            )

        assertEquals(
            "CANCELLED",
            encounterStatus(
                encounterId
            )
        )

        assertEquals(
            "Patient requested transfer",
            encounterCancellationReason(
                encounterId
            )
        )

        assertTrue(
            encounterCancelledAtPresent(
                encounterId
            )
        )
    }

    @Test
    fun `cancel requires non blank reason`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/cancel"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    cancelJson("   ")
                )
        )
            .andExpect(status().isBadRequest)

        assertEquals(
            "IN_PROGRESS",
            encounterStatus(
                encounterId
            )
        )
    }

    @Test
    fun `cancelled encounter is terminal`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        val encounterId =
            startEncounter(
                visit.queueEntryId
            )

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/cancel"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    cancelJson(
                        "Cancelled for test"
                    )
                )
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            put(
                itemUrl(encounterId) +
                    "/complete"
            )
                .with(jwtFor(DOCTOR_SUBJECT))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.code")
                    .value(
                        "ENCOUNTER_INVALID_STATUS_TRANSITION"
                    )
            )
    }

    // ========================================================
    // DATABASE CONSTRAINTS
    // ========================================================

    @Test
    fun `database rejects second encounter for same appointment and queue`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        startEncounter(
            visit.queueEntryId
        )

        assertFailsWith<
            DataIntegrityViolationException
        > {
            insertRawEncounter(
                visit = visit
            )
        }

        assertEquals(1L, encounterCount())
    }

    @Test
    fun `database rejects encounter with mismatched patient context`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        assertFailsWith<
            DataIntegrityViolationException
        > {
            insertRawEncounter(
                visit = visit,
                patientId =
                    PATIENT_A2_ID
            )
        }

        assertEquals(0L, encounterCount())
    }

    @Test
    fun `database rejects cancelled encounter without cancellation reason`() {

        val visit =
            insertVisit(
                queueStatus = "SERVING"
            )

        assertFailsWith<
            DataIntegrityViolationException
        > {
            insertRawEncounter(
                visit = visit,
                status = "CANCELLED",
                cancellationReason = null
            )
        }

        assertEquals(0L, encounterCount())
    }

    // ========================================================
    // CLEANUP / SEED
    // ========================================================

    private fun cleanBusinessData() {

        jdbcTemplate.update(
            "DELETE FROM encounter.clinical_notes"
        )

        jdbcTemplate.update(
            "DELETE FROM encounter.encounters"
        )

        jdbcTemplate.update(
            "DELETE FROM reception.queue_entries"
        )

        jdbcTemplate.update(
            "DELETE FROM appointment.appointments"
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

        insertOrganization(
            id = ORGANIZATION_A_ID,
            code = "ORG-A",
            name = "Organization A"
        )

        insertOrganization(
            id = ORGANIZATION_B_ID,
            code = "ORG-B",
            name = "Organization B"
        )

        insertFacility(
            id = FACILITY_A1_ID,
            organizationId =
                ORGANIZATION_A_ID,
            code = "FAC-A1",
            name = "Facility A1"
        )

        insertFacility(
            id = FACILITY_A2_ID,
            organizationId =
                ORGANIZATION_A_ID,
            code = "FAC-A2",
            name = "Facility A2"
        )

        insertFacility(
            id = FACILITY_B1_ID,
            organizationId =
                ORGANIZATION_B_ID,
            code = "FAC-B1",
            name = "Facility B1"
        )
    }

    private fun seedUsers() {

        insertUser(
            id = ADMIN_USER_ID,
            subject = ADMIN_SUBJECT,
            email =
                "encounter-admin@example.com",
            displayName =
                "Encounter Admin"
        )

        insertUser(
            id = MANAGER_USER_ID,
            subject = MANAGER_SUBJECT,
            email =
                "encounter-manager@example.com",
            displayName =
                "Encounter Manager"
        )

        insertUser(
            id = RECEPTIONIST_USER_ID,
            subject =
                RECEPTIONIST_SUBJECT,
            email =
                "encounter-receptionist@example.com",
            displayName =
                "Encounter Receptionist"
        )

        insertUser(
            id = DOCTOR_USER_ID,
            subject = DOCTOR_SUBJECT,
            email =
                "encounter-doctor@example.com",
            displayName =
                "Encounter Doctor"
        )

        insertUser(
            id = NURSE_USER_ID,
            subject = NURSE_SUBJECT,
            email =
                "encounter-nurse@example.com",
            displayName =
                "Encounter Nurse"
        )

        insertUser(
            id = PHARMACIST_USER_ID,
            subject =
                PHARMACIST_SUBJECT,
            email =
                "encounter-pharmacist@example.com",
            displayName =
                "Encounter Pharmacist"
        )

        insertUser(
            id = LAB_TECHNICIAN_USER_ID,
            subject =
                LAB_TECHNICIAN_SUBJECT,
            email =
                "encounter-lab@example.com",
            displayName =
                "Encounter Lab Technician"
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
            id =
                RECEPTIONIST_A_MEMBERSHIP_ID,
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
            id =
                PHARMACIST_A_MEMBERSHIP_ID,
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
            patientCode =
                "PAT-A-001",
            fullName =
                "Patient A"
        )

        insertPatient(
            id = PATIENT_A2_ID,
            patientCode =
                "PAT-A-002",
            fullName =
                "Patient A2"
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

        insertFacilityService(
            id = FACILITY_SERVICE_A_ID,
            organizationId =
                ORGANIZATION_A_ID,
            facilityId =
                FACILITY_A1_ID,
            serviceId =
                SERVICE_A_ID
        )
    }

    // ========================================================
    // INSERT HELPERS
    // ========================================================

    private fun insertOrganization(
        id: UUID,
        code: String,
        name: String
    ) {

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
                ?,
                ?,
                'ACTIVE'
            )
            """.trimIndent(),
            id,
            code,
            name
        )
    }

    private fun insertFacility(
        id: UUID,
        organizationId: UUID,
        code: String,
        name: String
    ) {

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
                ?,
                ?,
                'VN',
                'ACTIVE'
            )
            """.trimIndent(),
            id,
            organizationId,
            code,
            name
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
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
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

    private fun insertVisit(
        queueStatus: String,
        appointmentStatus: String =
            "SCHEDULED",
        startAt: String =
            "2026-10-05T02:00:00Z",
        endAt: String =
            "2026-10-05T02:30:00Z"
    ): VisitSeed {

        val appointmentId =
            insertAppointment(
                status =
                    appointmentStatus,
                startAt =
                    startAt,
                endAt =
                    endAt
            )

        val queueEntryId =
            insertQueueEntry(
                appointmentId =
                    appointmentId,
                status =
                    queueStatus
            )

        return VisitSeed(
            appointmentId =
                appointmentId,
            queueEntryId =
                queueEntryId,
            patientId =
                PATIENT_A_ID,
            practitionerId =
                PRACTITIONER_A_ID
        )
    }

    private fun insertAppointment(
        status: String =
            "SCHEDULED",
        startAt: String =
            "2026-10-05T02:00:00Z",
        endAt: String =
            "2026-10-05T02:30:00Z"
    ): UUID {

        val appointmentId =
            UUID.randomUUID()

        val cancellationReason =
            if (status == "CANCELLED") {
                "Cancelled before encounter"
            } else {
                null
            }

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
                'Encounter integration test',
                ?,
                ?,
                ?
            )
            """.trimIndent(),
            appointmentId,
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            PATIENT_A_ID,
            PRACTITIONER_A_ID,
            FACILITY_SERVICE_A_ID,
            startAt,
            endAt,
            status,
            cancellationReason,
            ADMIN_USER_ID,
            ADMIN_USER_ID
        )

        return appointmentId
    }

    private fun insertQueueEntry(
        appointmentId: UUID,
        status: String
    ): UUID {

        val queueEntryId =
            UUID.randomUUID()

        when (status) {

            "WAITING" -> {
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
                    queueEntryId,
                    ORGANIZATION_A_ID,
                    FACILITY_A1_ID,
                    appointmentId,
                    PATIENT_A_ID,
                    PRACTITIONER_A_ID,
                    RECEPTIONIST_USER_ID,
                    RECEPTIONIST_USER_ID
                )
            }

            "SERVING" -> {
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
                        'SERVING',
                        CURRENT_TIMESTAMP,
                        CURRENT_TIMESTAMP,
                        CURRENT_TIMESTAMP,
                        ?,
                        ?
                    )
                    """.trimIndent(),
                    queueEntryId,
                    ORGANIZATION_A_ID,
                    FACILITY_A1_ID,
                    appointmentId,
                    PATIENT_A_ID,
                    PRACTITIONER_A_ID,
                    RECEPTIONIST_USER_ID,
                    RECEPTIONIST_USER_ID
                )
            }

            else ->
                error(
                    "Unsupported queue status in test seed: $status"
                )
        }

        return queueEntryId
    }

    private fun insertRawEncounter(
        visit: VisitSeed,
        patientId: UUID =
            visit.patientId,
        practitionerId: UUID =
            visit.practitionerId,
        status: String =
            "IN_PROGRESS",
        cancellationReason: String? =
            null
    ) {

        jdbcTemplate.update(
            """
            INSERT INTO encounter.encounters (
                id,
                organization_id,
                facility_id,
                appointment_id,
                queue_entry_id,
                patient_id,
                practitioner_id,
                status,
                started_at,
                completed_at,
                cancelled_at,
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
                ?,
                ?,
                CURRENT_TIMESTAMP,
                CASE
                    WHEN ? = 'COMPLETED'
                    THEN CURRENT_TIMESTAMP
                    ELSE NULL
                END,
                CASE
                    WHEN ? = 'CANCELLED'
                    THEN CURRENT_TIMESTAMP
                    ELSE NULL
                END,
                ?,
                ?,
                ?
            )
            """.trimIndent(),
            UUID.randomUUID(),
            ORGANIZATION_A_ID,
            FACILITY_A1_ID,
            visit.appointmentId,
            visit.queueEntryId,
            patientId,
            practitionerId,
            status,
            status,
            status,
            cancellationReason,
            DOCTOR_USER_ID,
            DOCTOR_USER_ID
        )
    }

    // ========================================================
    // API HELPERS
    // ========================================================

    private fun startEncounter(
        queueEntryId: UUID,
        subject: String =
            DOCTOR_SUBJECT
    ): UUID {

        val result =
            mockMvc.perform(
                post(collectionUrl())
                    .with(jwtFor(subject))
                    .contentType(
                        MediaType.APPLICATION_JSON
                    )
                    .content(
                        startEncounterJson(
                            queueEntryId
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
                    "Encounter id missing from response: $responseBody"
                )

        return UUID.fromString(id)
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
            "$facilityId/encounters"

    private fun itemUrl(
        encounterId: UUID,
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
        ) + "/$encounterId"

    private fun clinicalNoteUrl(
        encounterId: UUID,
        organizationId: UUID =
            ORGANIZATION_A_ID,
        facilityId: UUID =
            FACILITY_A1_ID
    ): String =
        itemUrl(
            encounterId =
                encounterId,
            organizationId =
                organizationId,
            facilityId =
                facilityId
        ) + "/clinical-note"

    private fun startEncounterJson(
        queueEntryId: UUID
    ): String =
        """
        {
          "queueEntryId": "$queueEntryId"
        }
        """.trimIndent()

    private fun cancelJson(
        reason: String
    ): String =
        """
        {
          "cancellationReason": "${escapeJson(reason)}"
        }
        """.trimIndent()

    private fun clinicalNoteJson(
        chiefComplaint: String?,
        subjective: String?,
        objective: String?,
        assessment: String?,
        plan: String?
    ): String =
        """
        {
          "chiefComplaint": ${jsonString(chiefComplaint)},
          "subjective": ${jsonString(subjective)},
          "objective": ${jsonString(objective)},
          "assessment": ${jsonString(assessment)},
          "plan": ${jsonString(plan)}
        }
        """.trimIndent()

    private fun jsonString(
        value: String?
    ): String =
        if (value == null) {
            "null"
        } else {
            "\"${escapeJson(value)}\""
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
            .replace(
                "\n",
                "\\n"
            )
            .replace(
                "\r",
                "\\r"
            )

    // ========================================================
    // DATABASE ASSERTIONS
    // ========================================================

    private fun encounterCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM encounter.encounters
            """.trimIndent(),
            Long::class.java
        )!!

    private fun clinicalNoteCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM encounter.clinical_notes
            """.trimIndent(),
            Long::class.java
        )!!

    private fun encounterStatus(
        encounterId: UUID
    ): String =
        jdbcTemplate.queryForObject(
            """
            SELECT status
            FROM encounter.encounters
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            encounterId
        )!!

    private fun encounterCancellationReason(
        encounterId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT cancellation_reason
            FROM encounter.encounters
            WHERE id = ?
            """.trimIndent(),
            String::class.java,
            encounterId
        )

    private fun encounterCompletedAtPresent(
        encounterId: UUID
    ): Boolean =
        jdbcTemplate.queryForObject(
            """
            SELECT completed_at IS NOT NULL
            FROM encounter.encounters
            WHERE id = ?
            """.trimIndent(),
            Boolean::class.java,
            encounterId
        )!!

    private fun encounterCancelledAtPresent(
        encounterId: UUID
    ): Boolean =
        jdbcTemplate.queryForObject(
            """
            SELECT cancelled_at IS NOT NULL
            FROM encounter.encounters
            WHERE id = ?
            """.trimIndent(),
            Boolean::class.java,
            encounterId
        )!!

    private fun clinicalNoteChiefComplaint(
        encounterId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT chief_complaint
            FROM encounter.clinical_notes
            WHERE encounter_id = ?
            """.trimIndent(),
            String::class.java,
            encounterId
        )

    private fun clinicalNoteSubjective(
        encounterId: UUID
    ): String? =
        jdbcTemplate.queryForObject(
            """
            SELECT subjective
            FROM encounter.clinical_notes
            WHERE encounter_id = ?
            """.trimIndent(),
            String::class.java,
            encounterId
        )
}
