package com.clinic.platform.modules.iam.application

import com.clinic.platform.modules.iam.domain.IamUser
import com.clinic.platform.modules.iam.domain.IamUserRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetIamUserUseCase(
    private val userRepository: IamUserRepository
) {

    @Transactional(readOnly = true)
    fun execute(id: UUID): IamUser {
        return userRepository.findById(id)
            ?: throw ResourceNotFoundException(
                "IAM user not found: $id"
            )
    }
}