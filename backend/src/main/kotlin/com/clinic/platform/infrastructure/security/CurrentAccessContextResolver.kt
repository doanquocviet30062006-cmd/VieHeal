package com.clinic.platform.infrastructure.security

import com.clinic.platform.modules.iam.domain.FacilityAssignmentRepository
import com.clinic.platform.modules.iam.domain.FacilityAssignmentStatus
import com.clinic.platform.modules.iam.domain.IamPermissionRepository
import com.clinic.platform.modules.iam.domain.IamRoleRepository
import com.clinic.platform.modules.iam.domain.MembershipRoleRepository
import com.clinic.platform.modules.iam.domain.MembershipStatus
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.modules.iam.domain.RolePermissionRepository
import com.clinic.platform.modules.iam.domain.RoleScope
import com.clinic.platform.modules.iam.domain.UserSystemRoleRepository
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CurrentAccessContextResolver(
    private val currentIamUserResolver: CurrentIamUserResolver,
    private val userSystemRoleRepository: UserSystemRoleRepository,
    private val membershipRepository: OrganizationMembershipRepository,
    private val membershipRoleRepository: MembershipRoleRepository,
    private val roleRepository: IamRoleRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val permissionRepository: IamPermissionRepository,
    private val facilityAssignmentRepository: FacilityAssignmentRepository
) {

    @Transactional(readOnly = true)
    fun resolve(jwt: Jwt): AccessContext {

        val user = currentIamUserResolver.resolve(jwt)

        val systemRoleIds =
            userSystemRoleRepository
                .findRoleIdsByUserId(user.id)

        val systemRoles =
            systemRoleIds
                .mapNotNull {
                    roleRepository.findById(it)
                }
                .filter {
                    it.scope == RoleScope.SYSTEM
                }

        val systemPermissionIds =
            if (systemRoles.isEmpty()) {
                emptySet()
            } else {
                rolePermissionRepository
                    .findPermissionIdsByRoleIds(
                        systemRoles.map { it.id }
                    )
            }

        val systemPermissions =
            if (systemPermissionIds.isEmpty()) {
                emptyList()
            } else {
                permissionRepository
                    .findAllByIds(systemPermissionIds)
            }

        val organizationAccess =
            membershipRepository
                .findAllByUserId(user.id)
                .filter {
                    it.status == MembershipStatus.ACTIVE
                }
                .map { membership ->

                    val roleIds =
                        membershipRoleRepository
                            .findRoleIdsByMembershipId(
                                membership.id
                            )

                    val roles =
                        roleIds
                            .mapNotNull {
                                roleRepository.findById(it)
                            }

                    val permissionIds =
                        rolePermissionRepository
                            .findPermissionIdsByRoleIds(
                                roles.map { it.id }
                            )

                    val permissions =
                        permissionRepository
                            .findAllByIds(permissionIds)

                    val facilityIds =
                        facilityAssignmentRepository
                            .findAllByMembershipId(
                                membership.id
                            )
                            .filter {
                                it.status ==
                                    FacilityAssignmentStatus.ACTIVE
                            }
                            .map { it.facilityId }
                            .toSet()

                    OrganizationAccess(
                        membershipId = membership.id,
                        organizationId =
                            membership.organizationId,
                        roles = roles
                            .map { it.code }
                            .toSortedSet(),
                        permissions = permissions
                            .map { it.code }
                            .toSortedSet(),
                        facilityIds = facilityIds
                    )
                }

        return AccessContext(
            user = user,
            organizations = organizationAccess,
            systemRoles = systemRoles
                .map { it.code }
                .toSortedSet(),
            systemPermissions = systemPermissions
                .map { it.code }
                .toSortedSet()
        )
    }
}