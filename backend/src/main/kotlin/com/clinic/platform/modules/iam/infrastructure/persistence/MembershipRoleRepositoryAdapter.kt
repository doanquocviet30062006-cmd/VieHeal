package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.MembershipRoleRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MembershipRoleRepositoryAdapter(
    private val jpaRepository: MembershipRoleJpaRepository
) : MembershipRoleRepository {

    override fun assign(
        membershipId: UUID,
        roleId: UUID
    ) {
        jpaRepository.save(
            MembershipRoleJpaEntity(
                MembershipRoleId(
                    membershipId = membershipId,
                    roleId = roleId
                )
            )
        )
    }

    override fun exists(
        membershipId: UUID,
        roleId: UUID
    ): Boolean {
        return jpaRepository.existsById(
            MembershipRoleId(
                membershipId = membershipId,
                roleId = roleId
            )
        )
    }

    override fun findRoleIdsByMembershipId(
        membershipId: UUID
    ): List<UUID> {
        return jpaRepository
            .findAllByIdMembershipId(membershipId)
            .map { it.id.roleId }
    }
}