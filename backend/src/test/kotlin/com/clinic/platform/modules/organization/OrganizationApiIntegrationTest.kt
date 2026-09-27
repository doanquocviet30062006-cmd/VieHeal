package com.clinic.platform.modules.organization

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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
class OrganizationApiIntegrationTest(
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

        private val ORGANIZATION_ADMIN_USER_ID =
            UUID.fromString(
                "41000000-0000-0000-0000-000000000001"
            )

        private val NORMAL_USER_ID =
            UUID.fromString(
                "41000000-0000-0000-0000-000000000002"
            )

        private val SYSTEM_ADMIN_USER_ID =
            UUID.fromString(
                "41000000-0000-0000-0000-000000000003"
            )

        private val EXISTING_ORGANIZATION_ID =
            UUID.fromString(
                "42000000-0000-0000-0000-000000000001"
            )

        private val ORGANIZATION_ADMIN_MEMBERSHIP_ID =
            UUID.fromString(
                "43000000-0000-0000-0000-000000000001"
            )

        private const val ORGANIZATION_ADMIN_SUBJECT =
            "organization-test-org-admin"

        private const val NORMAL_USER_SUBJECT =
            "organization-test-normal-user"

        private const val SYSTEM_ADMIN_SUBJECT =
            "organization-test-system-admin"
    }

    @BeforeEach
    fun setUp() {

        cleanBusinessData()

        seedExistingOrganization()
        seedUsers()
        seedOrganizationAdminMembership()
        seedOrganizationAdminRole()
        seedSystemAdminRole()
    }

    @Test
    fun `create organization without JWT returns 401 and is not persisted`() {

        val organizationCode =
            "ORG-ANON-401"

        mockMvc.perform(
            post("/api/v1/organizations")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    createOrganizationJson(
                        code = organizationCode,
                        name =
                            "Anonymous Organization"
                    )
                )
        )
            .andExpect(
                status().isUnauthorized
            )

        assertEquals(
            0L,
            organizationCountByCode(
                organizationCode
            )
        )
    }

    @Test
    fun `authenticated user without system role cannot create organization`() {

        val organizationCode =
            "ORG-NORMAL-403"

        mockMvc.perform(
            post("/api/v1/organizations")
                .with(
                    jwtFor(
                        NORMAL_USER_SUBJECT
                    )
                )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    createOrganizationJson(
                        code = organizationCode,
                        name =
                            "Normal User Organization"
                    )
                )
        )
            .andExpect(
                status().isForbidden
            )

        assertEquals(
            0L,
            organizationCountByCode(
                organizationCode
            )
        )
    }

    @Test
    fun `organization admin with organization manage cannot create global organization`() {

        val organizationCode =
            "ORG-ADMIN-403"

        assertEquals(
            1L,
            organizationAdminManagePermissionCount()
        )

        assertEquals(
            0L,
            systemRoleCount(
                ORGANIZATION_ADMIN_USER_ID
            )
        )

        mockMvc.perform(
            post("/api/v1/organizations")
                .with(
                    jwtFor(
                        ORGANIZATION_ADMIN_SUBJECT
                    )
                )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    createOrganizationJson(
                        code = organizationCode,
                        name =
                            "Organization Admin Attempt"
                    )
                )
        )
            .andExpect(
                status().isForbidden
            )

        assertEquals(
            0L,
            organizationCountByCode(
                organizationCode
            )
        )
    }

    @Test
    fun `system admin with organization manage can create organization`() {

        val organizationCode =
            "ORG-SYSTEM-201"

        assertEquals(
            1L,
            systemAdminManagePermissionCount()
        )

        mockMvc.perform(
            post("/api/v1/organizations")
                .with(
                    jwtFor(
                        SYSTEM_ADMIN_SUBJECT
                    )
                )
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    createOrganizationJson(
                        code = organizationCode,
                        name =
                            "System Created Organization"
                    )
                )
        )
            .andExpect(
                status().isCreated
            )
            .andExpect(
                jsonPath("$.code")
                    .value(organizationCode)
            )
            .andExpect(
                jsonPath("$.name")
                    .value(
                        "System Created Organization"
                    )
            )

        assertEquals(
            1L,
            organizationCountByCode(
                organizationCode
            )
        )
    }

    private fun cleanBusinessData() {

        jdbcTemplate.update(
            "DELETE FROM iam.user_system_roles"
        )

        jdbcTemplate.update(
            "DELETE FROM iam.membership_roles"
        )

        jdbcTemplate.update(
            "DELETE FROM iam.facility_assignments"
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

    private fun seedExistingOrganization() {

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
            EXISTING_ORGANIZATION_ID,
            "ORG-EXISTING",
            "Existing Test Organization"
        )
    }

    private fun seedUsers() {

        insertUser(
            id =
                ORGANIZATION_ADMIN_USER_ID,
            subject =
                ORGANIZATION_ADMIN_SUBJECT,
            email =
                "organization-admin@example.com",
            displayName =
                "Organization Admin"
        )

        insertUser(
            id =
                NORMAL_USER_ID,
            subject =
                NORMAL_USER_SUBJECT,
            email =
                "normal-user@example.com",
            displayName =
                "Normal User"
        )

        insertUser(
            id =
                SYSTEM_ADMIN_USER_ID,
            subject =
                SYSTEM_ADMIN_SUBJECT,
            email =
                "system-admin@example.com",
            displayName =
                "System Admin"
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

    private fun seedOrganizationAdminMembership() {

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
            ORGANIZATION_ADMIN_MEMBERSHIP_ID,
            ORGANIZATION_ADMIN_USER_ID,
            EXISTING_ORGANIZATION_ID
        )
    }

    private fun seedOrganizationAdminRole() {

        val inserted =
            jdbcTemplate.update(
                """
                INSERT INTO iam.membership_roles (
                    membership_id,
                    role_id
                )
                SELECT ?, id
                FROM iam.roles
                WHERE code = 'ORGANIZATION_ADMIN'
                  AND scope = 'ORGANIZATION'
                """.trimIndent(),
                ORGANIZATION_ADMIN_MEMBERSHIP_ID
            )

        assertEquals(
            1,
            inserted
        )
    }

    private fun seedSystemAdminRole() {

        val inserted =
            jdbcTemplate.update(
                """
                INSERT INTO iam.user_system_roles (
                    user_id,
                    role_id
                )
                SELECT ?, id
                FROM iam.roles
                WHERE code = 'SYSTEM_ADMIN'
                  AND scope = 'SYSTEM'
                """.trimIndent(),
                SYSTEM_ADMIN_USER_ID
            )

        assertEquals(
            1,
            inserted
        )
    }

    private fun organizationAdminManagePermissionCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM iam.organization_memberships membership
            JOIN iam.membership_roles membership_role
              ON membership_role.membership_id =
                 membership.id
            JOIN iam.roles role
              ON role.id =
                 membership_role.role_id
            JOIN iam.role_permissions role_permission
              ON role_permission.role_id =
                 role.id
            JOIN iam.permissions permission
              ON permission.id =
                 role_permission.permission_id
            WHERE membership.user_id = ?
              AND membership.status = 'ACTIVE'
              AND role.code =
                  'ORGANIZATION_ADMIN'
              AND role.scope =
                  'ORGANIZATION'
              AND permission.code =
                  'organization.manage'
            """.trimIndent(),
            Long::class.javaObjectType,
            ORGANIZATION_ADMIN_USER_ID
        ) ?: 0L

    private fun systemAdminManagePermissionCount():
        Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM iam.user_system_roles user_role
            JOIN iam.roles role
              ON role.id =
                 user_role.role_id
            JOIN iam.role_permissions role_permission
              ON role_permission.role_id =
                 role.id
            JOIN iam.permissions permission
              ON permission.id =
                 role_permission.permission_id
            WHERE user_role.user_id = ?
              AND role.code =
                  'SYSTEM_ADMIN'
              AND role.scope =
                  'SYSTEM'
              AND permission.code =
                  'organization.manage'
            """.trimIndent(),
            Long::class.javaObjectType,
            SYSTEM_ADMIN_USER_ID
        ) ?: 0L

    private fun systemRoleCount(
        userId: UUID
    ): Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM iam.user_system_roles
            WHERE user_id = ?
            """.trimIndent(),
            Long::class.javaObjectType,
            userId
        ) ?: 0L

    private fun organizationCountByCode(
        code: String
    ): Long =
        jdbcTemplate.queryForObject(
            """
            SELECT count(*)
            FROM organization.organizations
            WHERE code = ?
            """.trimIndent(),
            Long::class.javaObjectType,
            code
        ) ?: 0L

    private fun jwtFor(
        subject: String
    ) =
        jwt().jwt {
            it.subject(subject)
        }

    private fun createOrganizationJson(
        code: String,
        name: String
    ): String =
        """
        {
          "code": "$code",
          "name": "$name"
        }
        """.trimIndent()
}