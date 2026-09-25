package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.IamRole
import com.clinic.platform.modules.iam.domain.IamRoleRepository
import com.clinic.platform.modules.iam.domain.MembershipRoleRepository
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListMembershipRolesUseCase(
    private val membershipRepository: OrganizationMembershipRepository,
    private val roleRepository: IamRoleRepository,
    private val membershipRoleRepository: MembershipRoleRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        membershipId: UUID
    ): List<IamRole> {

        membershipRepository.findById(membershipId)
            ?: throw ResourceNotFoundException(
                "Organization membership not found: $membershipId"
            )

        return membershipRoleRepository
            .findRoleIdsByMembershipId(membershipId)
            .mapNotNull { roleRepository.findById(it) }
    }
}